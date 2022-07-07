package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosarfrontend.common.anotation.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
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
                System.out.println("Method arguments: " + Arrays.stream(method.getParameters()).map(p -> String.format("(%s:%s),", p.getName(), p.getType().getSimpleName())).collect(Collectors.joining()));
                System.out.println("Try to pass arguments: (" + Arrays.stream(params).map(p -> String.format("%s ,", p.getClass().getSimpleName())).collect(Collectors.joining()) + ")");
                e.printStackTrace();
                return null;
            }
        } else return null;
    }
}


public class FileFactory {
    private final Class<?> fileTemplateDesc;
    private final String outPutFolder;
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
    private boolean updateFileName(Method withFileName) {
        if (withFileName.getReturnType() != String.class) return true;
        String value = MethodUtils.execute(handler, withFileName, String.class);
        if (value != null) {
            this.fileName = value;
        }
        return false;
    }

    @WithFilePath
    private boolean updateFilePath(Method withFilePath) {
        if (withFilePath.getReturnType() != String.class) return true;
        String value = MethodUtils.execute(handler, withFilePath, String.class);
        if (value != null) {
            this.filePath = value;
        }
        return false;
    }

    @WithFileExtension
    private boolean updateFileExt(Method withFileExtension) {
        if (withFileExtension.getReturnType() != String.class) return true;
        String value = MethodUtils.execute(handler, withFileExtension, String.class);
        if (value != null) {
            this.fileExt = value;
        }
        return false;
    }

    @SlotReplacement
    private boolean replaceSlot(Method replaceMethod) {
        if (replaceMethod.getReturnType() != String.class) return true;
        String value = MethodUtils.execute(handler, replaceMethod, String.class);
        if (value != null) {
            SlotReplacement ano = replaceMethod.getAnnotation(SlotReplacement.class);
            Pattern pattern = regexUtils.createSlotRegex(ano.slot());
            this.fileContent = pattern.matcher(this.fileContent).replaceAll(value);
        }
        return false;
    }

    @LoopReplacement
    private boolean replaceLoops(Method replaceMethod) {
        if (replaceMethod.getReturnType() != Slot[][].class) return true;
        Slot[][] loopValues = MethodUtils.execute(handler, replaceMethod, Slot[][].class);
        if (loopValues != null) {
            LoopReplacement ano = replaceMethod.getAnnotation(LoopReplacement.class);
            // get loop content
            final Pattern pattern = regexUtils.createLoopRegex(ano);
            final Matcher matcher = pattern.matcher(this.fileContent);
            if (matcher.find()) {
                //replace single_slot in loop
                StringBuilder replaceValue = new StringBuilder();
                for (Slot[] loopValue : loopValues) {
                    String loopContent = matcher.group(2);
                    for (Slot slot : loopValue) {
                        Pattern regex = regexUtils.createSlotRegex(slot.getSlotName());
                        Matcher subMatcher = regex.matcher(loopContent);
                        if (subMatcher.find()) {
                            loopContent = subMatcher.replaceAll(slot.getSlotValue() != null ? slot.getSlotValue() : "");
                        }
                    }
                    replaceValue.append(loopContent);
                }
                this.fileContent = matcher.replaceAll(replaceValue.toString());
            }
        }
        return false;
    }

    @IfReplacement
    private boolean replaceIf(Method conditionMethod) {
        if (conditionMethod.getReturnType() != boolean.class) return true;
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
        return false;
    }

    @SkipGenDecision
    private boolean checkSkip(Method method) {
        if (method.getReturnType() != boolean.class) return false;
        return Boolean.TRUE.equals(MethodUtils.execute(this.handler, method, Boolean.class));
    }

    public static final ArrayList<Class<? extends Annotation>> AnoPoints = new ArrayList<>( // Thực hiện các hàm gen theo thứ tự
            List.of(
                    SkipGenDecision.class,
                    IfReplacement.class,
                    LoopReplacement.class,
                    SlotReplacement.class,
                    WithFileName.class,
                    WithFileExtension.class,
                    WithFilePath.class
            ));

    private void updateFileContent(boolean saveWhenDone) {
        Class<? extends Annotation>[] annotations = this.handlerMapByAnnotation.keySet().toArray(Class[]::new);
        Arrays.sort(annotations, (ano1, ano2) -> AnoPoints.indexOf(ano1) - AnoPoints.indexOf(ano2));
        for (Class<? extends Annotation> aClass : annotations) {
            Method[] handlerMethods = this.handlerMapByAnnotation.get(aClass).toArray(Method[]::new);
            for (Method handlerMethod : handlerMethods) {
                Method action = this.actionMapByAnnotation.get(aClass);
                if (action != null) {
                    Object[] params = {handlerMethod};
                    try {
                        boolean stop = (boolean) action.invoke(this, params);
                        if (stop) return;
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        if (saveWhenDone) saveFile();
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
        String currentFolder = outPutFolder;
        for (String folder : this.filePath.split("/")) {
            currentFolder = currentFolder + "/" + folder;
            Path dir = new File(currentFolder).toPath();
            if (!Files.exists(dir)) {
                try {
                    Files.createDirectory(dir);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        Path classFile = new File(outPutFolder + this.filePath + "\\" + this.fileName + this.fileExt).toPath();
        if (!Files.exists(classFile)) {
            System.out.println("Path: " + classFile);
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

    public String genFile(boolean saveFileAfterDone) throws Exception {
        this.handler = this.fileTemplateDesc.getConstructor().newInstance();
        if (!fileTemplateDesc.isAnnotationPresent(jda.modules.mosarfrontend.common.anotation.FileTemplateDesc.class)) {
            throw new Exception("The class is not TemplateHandler (without @TemplateHandler annotation)");
        } else {
            this.fileTemplate = fileTemplateDesc.getAnnotation(FileTemplateDesc.class);
            // init template handler methods
            Method[] methods = this.fileTemplateDesc.getMethods();
            // Reverse array to ensure the last method (have same annotation with previous declared method) will be executed last
            Collections.reverse(Arrays.asList(methods));
            for (Method method : methods) {
                Annotation[] annotations = method.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    this.handlerMapByAnnotation.computeIfAbsent(annotation.annotationType(), k -> new ArrayList<>()); // init new if not exits
                    ArrayList<Method> listMethod = this.handlerMapByAnnotation.get(annotation.annotationType());
                    listMethod.add(method);
                }
            }

            initFileTemplate();
            updateFileContent(true);
            return this.fileContent;
        }
    }

    public String genAndGetContent() throws Exception {
        return genFile(false);
    }
}


