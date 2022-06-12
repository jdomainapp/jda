package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.*;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class MethodUtils {
    public static <T> T execute(Object executor, Method method, Class<T> returnType) {
        Object[] params = ParamsFactory.getInstance().getParamsForMethod(method);
        if (method.getParameters().length == params.length) {
            try {
                return returnType.cast(method.invoke(executor, params));
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.out.println("Error when trigger " + method.getName() + "()");
                e.printStackTrace();
                return null;
            } catch (IllegalArgumentException e) {
                System.out.println("Error when trigger " + method.getName() + "()");
                System.out.println("Wrong argument type!");
                System.out.println("Method arguments: " + Arrays.stream(method.getParameters())
                        .map(p -> String.format("(%s:%s),", p.getName(), p.getType().getSimpleName())).collect(Collectors.joining()));
                System.out.println("Try to pass arguments: (" + Arrays.stream(params)
                        .map(p -> String.format("%s ,", p.getClass().getSimpleName())).collect(Collectors.joining()) + ")");
                e.printStackTrace();
                return null;
            }
        } else return null;
    }
}

class RegexUtils {
    public Pattern createSlotRegex(String slot) {
        return Pattern.compile(String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot), Pattern.DOTALL);
    }

    public Pattern createLoopRegex(LoopReplacement loop) {
        StringBuilder singleSlotsRegex = new StringBuilder();
        for (String slot : loop.getSlots()) {
            singleSlotsRegex.append(createSlotRegex(slot));
            singleSlotsRegex.append(".*");
        }
        return Pattern.compile(String.format("@loop(?<li>\\{%s})\\[\\[(.*%s)]]loop(\\k<li>)@",
                loop.getId(), singleSlotsRegex), Pattern.DOTALL);
    }

    public Pattern createIfRegex(String id) {
        return Pattern.compile(String.format("@if(?<li>\\{%s\\})\\(\\((.*)\\)\\)if(\\k<li>)@", id), Pattern.DOTALL);
    }
}

@Data
public class FileFactory {
    @NonNull
    private final Class<?> fileTemplateDesc;
    @NonNull
    private final String outPutFolder;
    @NonNull
    private final String templateRootFolder;
    private FileTemplateDesc fileTemplate;
    private Object handler;
    //Output file info:
    private String fileContent = null;
    private String fileName = null;
    private String filePath = null;
    private String fileExt = null;
    // Utils
    private final RegexUtils regexUtils = new RegexUtils();
    private final ParamsFactory paramsFactory = ParamsFactory.getInstance();

    private final Map<Class<? extends Annotation>, ArrayList<Method>> handlerMapByAnnotation = new HashMap<>();
    private final Map<Class<? extends Annotation>, Method> actionMapByAnnotation = new HashMap<>();

    public FileFactory(Class<?> fileTemplateDesc, String feOutputPath, String templateFolder) {
        this.fileTemplateDesc = fileTemplateDesc;
        this.outPutFolder = feOutputPath;
        this.templateRootFolder = templateFolder;
        for (Method declaredMethod : this.getClass().getDeclaredMethods()) {
            Annotation[] annotations = declaredMethod.getDeclaredAnnotations();
            if (annotations.length > 0) {
                this.actionMapByAnnotation.put(annotations[0].annotationType(), declaredMethod);
            }
        }
    }

    private void initDefaultFileInfo() {
        StringBuilder buffer = new StringBuilder("");
        String templateFile = this.fileTemplate.templateFile();
        for (int i = templateFile.length() - 1; i >= 0; i--) {
            buffer.insert(0, templateFile.charAt(i));
            if (this.fileExt == null && templateFile.charAt(i) == '.') {
                this.fileExt = buffer.toString();
                buffer.setLength(0);
            }
            if (this.fileName == null && templateFile.charAt(i) == '/') {
                this.fileName = buffer.toString();
                buffer.setLength(0);
            }
            if (this.filePath == null && i == 0) {
                this.filePath = buffer.toString();
            }
        }
    }

    private void initFileTemplate() throws Exception {
        // get template file content
        String templateFilePath = templateRootFolder + this.fileTemplate.templateFile().replace("/", "\\");
        try {
            this.fileContent = Files.readString(Paths.get(templateFilePath));
        } catch (IOException e) {
            throw new Exception("Template file not found");
        }
        // init default properties of file ( ext, name, path)
        initDefaultFileInfo();

    }

    @WithFileName
    private void updateFileName(Method withFileName) {
        if (withFileName.getReturnType() != String.class) return;
        String value = MethodUtils.execute(handler, withFileName, String.class);
        if (value != null) {
            this.fileName = value;
        }
    }

    @WithFilePath
    private void updateFilePath(Method withFilePath) {
        if (withFilePath.getReturnType() != String.class) return;
        String value = MethodUtils.execute(handler, withFilePath, String.class);
        if (value != null) {
            this.filePath = value;
        }
    }

    @WithFileExtension
    private void updateFileExt(Method withFileExtension) {
        if (withFileExtension.getReturnType() != String.class) return;
        String value = MethodUtils.execute(handler, withFileExtension, String.class);
        if (value != null) {
            this.fileExt = value;
        }
    }

    @SlotReplacementDesc
    private void replaceSlot(Method replaceMethod) {
        if (replaceMethod.getReturnType() != String.class) return;
        String value = MethodUtils.execute(handler, replaceMethod, String.class);
        if (value != null) {
            SlotReplacement desc = new SlotReplacement();
            SlotReplacementDesc ano = replaceMethod.getAnnotation(SlotReplacementDesc.class);
            RFSGenTk.parseAnnotation2Config(ano, desc);
            Pattern pattern = regexUtils.createSlotRegex(desc.getSlot());
            this.fileContent = pattern.matcher(this.fileContent)
                    .replaceAll(value);
        }

    }

    @LoopReplacementDesc
    private void replaceLoops(Method replaceMethod) {
        if (replaceMethod.getReturnType() != Slot[][].class) return;
        Slot[][] loopValues = MethodUtils.execute(handler, replaceMethod, Slot[][].class);
        if (loopValues != null) {
            LoopReplacement desc = new LoopReplacement();
            LoopReplacementDesc ano = replaceMethod.getAnnotation(LoopReplacementDesc.class);
            RFSGenTk.parseAnnotation2Config(ano, desc);
            // get loop content
            final Pattern pattern = regexUtils.createLoopRegex(desc);
            final Matcher matcher = pattern.matcher(this.fileContent);
            if (matcher.find()) {
                //replace single_slot in loop
                StringBuilder replaceValue = new StringBuilder();
                for (Slot[] loopValue : (Slot[][]) loopValues) {
                    String loopContent = matcher.group(2);
                    for (Slot slotValue : loopValue) {
                        Pattern regex = regexUtils.createSlotRegex(slotValue.getSlotName());
                        loopContent = regex.matcher(loopContent).replaceAll(
                                slotValue.getSlotValue());
                    }
                    replaceValue.append(loopContent);
                }
                this.fileContent = matcher.replaceAll(replaceValue.toString());
            }
        }

    }

    @IfReplacement
    private void replaceIf(Method conditionMethod) {
        if (conditionMethod.getReturnType() != boolean.class) return;
        IfReplacement ifReplacement = conditionMethod.getAnnotation(IfReplacement.class);
        Pattern pattern = regexUtils.createIfRegex(ifReplacement.id());
        Matcher matcher = pattern.matcher(this.fileContent);
        if (matcher.find()) {
            if (Boolean.FALSE.equals(MethodUtils.execute(this.handler, conditionMethod, Boolean.class))) {
                this.fileContent = matcher.replaceAll("");
            } else {
                this.fileContent = matcher.replaceAll(matcher.group(2));
            }
        }
    }

    private boolean checkSkip(Method method) {
        if (method.getReturnType() != boolean.class) return false;
        return Boolean.TRUE.equals(MethodUtils.execute(this.handler, method, Boolean.class));
    }

    private void updateFileContent() {
        for (Class<? extends Annotation> aClass : this.handlerMapByAnnotation.keySet()) {
            Method[] handlerMethods = this.handlerMapByAnnotation.get(aClass).toArray(Method[]::new);
            for (Method handlerMethod : handlerMethods) {
                Method action = this.actionMapByAnnotation.get(aClass);
                if (action != null) {
                    Object[] params = {handlerMethod};
                    try {
                        action.invoke(this, params);
                    } catch (IllegalAccessException | InvocationTargetException e) {
//                        e.printStackTrace();
                    }
                }
            }

        }
//        for (Method method : this.fileTemplateDesc.getMethods()) {
//            if (method.getReturnType() == String.class || method.getReturnType() == Slot[][].class) {
//                if (method.isAnnotationPresent(LoopReplacementDesc.class)) {
//                    replaceLoops(method);
//                }
//                ;
//                if (method.isAnnotationPresent(SlotReplacementDesc.class)) {
//                    replaceSlot(method);
//                }
//                ;
//                if (method.isAnnotationPresent(WithFileName.class)) {
//                    updateFileName(method);
//                }
//                ;
//                if (method.isAnnotationPresent(WithFilePath.class)) {
//                    updateFilePath(method);
//                }
//                ;
//                if (method.isAnnotationPresent(WithFileExtension.class)) {
//                    updateFileExt(method);
//                }
//                ;
//            }
//        }
    }

    private void saveFile() {
        Path path = new File(outPutFolder).toPath();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path dir = new File(outPutFolder + this.filePath).toPath();
        if (!Files.exists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path classFile = new File(outPutFolder + this.filePath + "\\" + this.fileName + this.fileExt).toPath();
        if (!Files.exists(classFile)) {
            try {
                Files.createFile(classFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Files.writeString(classFile, this.fileContent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void genAndSave() throws Exception {
        this.handler = this.fileTemplateDesc.getConstructor().newInstance();
        if (!fileTemplateDesc.isAnnotationPresent(jda.modules.mosarfrontend.common.anotation.FileTemplateDesc.class)) {
            throw new Exception("The class is not TemplateHandler (without @TemplateHandler annotation)");
        } else {
            this.fileTemplate = fileTemplateDesc.getAnnotation(FileTemplateDesc.class);
            // init template handler methods
            Method[] methods = Arrays.stream(this.fileTemplateDesc.getDeclaredMethods()).toArray(Method[]::new);
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                if (annotations.length > 0) {
                    this.handlerMapByAnnotation.computeIfAbsent(annotations[0].annotationType(), k -> new ArrayList<>()); // init new if not exits
                    ArrayList<Method> listMethod = this.handlerMapByAnnotation.get(annotations[0].annotationType());
                    listMethod.add(method);
                    if (annotations[0].annotationType() == SkipGenDecision.class && checkSkip(method)) return;
                }
            }

            initFileTemplate();
            updateFileContent();
            saveFile();
        }
    }
}


