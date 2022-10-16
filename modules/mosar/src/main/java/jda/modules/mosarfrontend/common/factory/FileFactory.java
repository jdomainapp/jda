package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosarfrontend.common.anotation.gen_controlers.*;
import jda.modules.mosarfrontend.common.anotation.template_desc.FileTemplateDesc;
import jda.modules.mosarfrontend.common.utils.MethodUtils;
import jda.modules.mosarfrontend.common.utils.RegexUtils;

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

    private static String getTemplate(String pathFromRoot) throws Exception {
        try {
            return Files.readString(Paths.get(ParamsFactory.getInstance().getTEMPLATE_ROOT_FOLDER())
                    .resolve(pathFromRoot.charAt(0) == '/' ? pathFromRoot.substring(1) : pathFromRoot));
        } catch (IOException e) {
            throw new Exception("Template file not found: " + pathFromRoot);
        }
    }

    private void initFileTemplate() throws Exception {
        // get template file content
        this.fileContent = getTemplate(this.fileTemplate.templateFile());
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
            Pattern pattern = regexUtils.createSlotRegex(ano.id());
            this.fileContent = pattern.matcher(this.fileContent).replaceAll(value);
        }
        return false;
    }

    public static String replaceLoopWithTemplate(String template, String loopID, Slot[][] slots) {
        String content = new String(template);
        final Pattern pattern = RegexUtils.createLoopRegex(loopID);
        final Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            //replace single_slot in loop
            StringBuilder replaceValue = new StringBuilder();
            for (Slot[] loopValue : slots) {
                String loopContent = matcher.group(2);
                for (Slot slot : loopValue) {
                    Pattern regex = RegexUtils.createSlotRegex(slot.getSlotName());
                    Matcher subMatcher = regex.matcher(loopContent);
                    if (subMatcher.find()) {
                        loopContent = subMatcher.replaceAll(slot.getSlotValue() != null ? slot.getSlotValue() : "");
                    }
                }
                replaceValue.append(loopContent);
            }
            content = matcher.replaceAll(replaceValue.toString());
        }
        return content;
    }

    public static String replaceLoopWithFileTemplate(String filePath, String loopID, Slot[][] slots) throws Exception {
        try {
            String template = FileFactory.getTemplate(filePath);
            return replaceLoopWithTemplate(template, loopID, slots);
        } catch (IOException e) {
            throw new Exception("Template file not found");
        }
    }

    @LoopReplacement
    private boolean replaceLoops(Method replaceMethod) {
        if (replaceMethod.getReturnType() != Slot[][].class) return true;
        Slot[][] loopValues = MethodUtils.execute(handler, replaceMethod, Slot[][].class);
        if (loopValues != null) {
            LoopReplacement ano = replaceMethod.getAnnotation(LoopReplacement.class);
            // get loop content
            this.fileContent = replaceLoopWithTemplate(this.fileContent, ano.id(), loopValues);
            for (String id : ano.ids()) {
                this.fileContent = replaceLoopWithTemplate(this.fileContent, id, loopValues);
            }
        }
        return false;
    }

    @IfReplacement
    private boolean replaceIf(Method conditionMethod) {
        if (conditionMethod.getReturnType() != boolean.class) return true;
        IfReplacement ifReplacement = conditionMethod.getAnnotation(IfReplacement.class);
        String[] ids = ifReplacement.id().length() > 0 ? new String[]{ifReplacement.id()} : ifReplacement.ids();
        for (String id : ids) {
            Pattern pattern = regexUtils.createIfRegex(id);
            Matcher matcher = pattern.matcher(this.fileContent);
                if (matcher.find()) {
                    if (Boolean.FALSE.equals(MethodUtils.execute(this.handler, conditionMethod, Boolean.class))) {
                        this.fileContent = matcher.replaceAll("");
                    } else {
                        String content = matcher.group(2);
                        this.fileContent = matcher.replaceAll(content);
                    }
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
        this.fileContent = RegexUtils.createSlotRegex("DOLAR").matcher(this.fileContent).replaceAll("\\$");
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

        Path classFile = new File(outPutFolder + this.filePath + "/" + this.fileName + this.fileExt).toPath();
        if (!Files.exists(classFile)) {
            System.out.println("Create new file : " + classFile);
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
        if (!fileTemplateDesc.isAnnotationPresent(FileTemplateDesc.class)) {
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


