package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.*;
import jda.modules.mosarfrontend.common.anotation.FileTemplate;
import jda.modules.mosarfrontend.common.anotation.FileTemplateDesc;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
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
    public String createSlotRegex(String slot) {
        return String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot);
    }

    public String createLoopRegex(LoopReplacement loop) {
        StringBuilder singleSlotsRegex = new StringBuilder();
        for (String slot : loop.getSlots()) {
            singleSlotsRegex.append(createSlotRegex(slot));
            singleSlotsRegex.append(".*");
        }
        return String.format("@loop(?<li>\\{%s})\\[\\[(.*%s)]]loop(\\k<li>)@",
                loop.getId(), singleSlotsRegex);
    }
}

@Data
public class FileFactory {
    @NonNull
    private Class<?> fileTemplateDesc;
    @NonNull
    private String outPutFolder;
    @NonNull
    private String templateRootFolder;
    private FileTemplate fileTemplate;
    private Class<?> moduleClass;
    private Object handler;
    //Output file info:
    private String fileContent = null;
    private String fileName = null;
    private String filePath = null;
    private String fileExt = null;
    // Utils
    private RegexUtils regexUtils = new RegexUtils();
    private final ParamsFactory paramsFactory = ParamsFactory.getInstance();

    private void initDefaultFileInfo() {
        StringBuilder buffer = new StringBuilder("");
        String templateFile = this.fileTemplate.getTemplateFile();
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
        if (!fileTemplateDesc.isAnnotationPresent(jda.modules.mosarfrontend.common.anotation.FileTemplateDesc.class)) {
            throw new Exception("The class is not TemplateHandler (without @TemplateHandler annotation)");
        } else {
            FileTemplateDesc ano = fileTemplateDesc.getAnnotation(FileTemplateDesc.class);
            this.handler = this.fileTemplateDesc.getConstructor().newInstance();
            this.fileTemplate = new FileTemplate();
            RFSGenTk.parseAnnotation2Config(ano, this.fileTemplate);
            // default output file info
            initDefaultFileInfo();
            // get template file content
            String templateFilePath = templateRootFolder + this.fileTemplate.getTemplateFile().replace("/", "\\");
            try {
                this.fileContent = Files.readString(Paths.get(templateFilePath));
            } catch (IOException e) {
                throw new Exception("Template file not found");
            }
        }
    }

    private void updateFileName(Method withFileName) {
        String value = MethodUtils.execute(handler, withFileName, String.class);
        if (value != null) {
            this.fileName = value;
        }
    }

    private void updateFilePath(Method withFilePath) {
        String value = MethodUtils.execute(handler, withFilePath, String.class);
        if (value != null) {
            this.filePath = value;
        }
    }

    private void updateFileExt(Method withFileExtension) {
        String value = MethodUtils.execute(handler, withFileExtension, String.class);
        if (value != null) {
            this.fileExt = value;
        }
    }

    private void replaceSlot(Method replaceMethod) {
        String value = MethodUtils.execute(handler, replaceMethod, String.class);
        if (value != null) {
            SlotReplacement desc = new SlotReplacement();
            SlotReplacementDesc ano = replaceMethod.getAnnotation(SlotReplacementDesc.class);
            RFSGenTk.parseAnnotation2Config(ano, desc);
            this.fileContent = this.fileContent
                    .replaceAll(regexUtils.createSlotRegex(desc.getSlot()), value);
        }

    }

    private void replaceLoops(Method replaceMethod) {
        Slot[][] loopValues = MethodUtils.execute(handler, replaceMethod, Slot[][].class);
        if (loopValues != null) {
            LoopReplacement desc = new LoopReplacement();
            LoopReplacementDesc ano = replaceMethod.getAnnotation(LoopReplacementDesc.class);
            RFSGenTk.parseAnnotation2Config(ano, desc);

            String loopRegex = regexUtils.createLoopRegex(desc);
            // get loop content
            final Pattern pattern = Pattern.compile(loopRegex, Pattern.DOTALL);
            final Matcher matcher = pattern.matcher(this.fileContent);
            if (matcher.find()) {
                //replace single_slot in loop
                StringBuilder replaceValue = new StringBuilder();
                for (Slot[] loopValue : (Slot[][]) loopValues) {
                    String loopContent = matcher.group(2);
                    for (Slot slotValue : loopValue) {
                        String regex = regexUtils.createSlotRegex(slotValue.getSlotName());
                        loopContent = loopContent.replaceAll(regex,
                                slotValue.getSlotValue());
                    }
                    replaceValue.append(loopContent);
                }
                this.fileContent = matcher.replaceAll(replaceValue.toString());
            }
        }

    }

    private void updateFileContent() {
        for (Method method : this.fileTemplateDesc.getMethods()) {
            if (method.getReturnType() == String.class || method.getReturnType() == Slot[][].class) {
                if (method.isAnnotationPresent(LoopReplacementDesc.class)) {
                    replaceLoops(method);
                }
                ;
                if (method.isAnnotationPresent(SlotReplacementDesc.class)) {
                    replaceSlot(method);
                }
                ;
                if (method.isAnnotationPresent(WithFileName.class)) {
                    updateFileName(method);
                }
                ;
                if (method.isAnnotationPresent(WithFilePath.class)) {
                    updateFilePath(method);
                }
                ;
                if (method.isAnnotationPresent(WithFileExtension.class)) {
                    updateFileExt(method);
                }
                ;
            }
        }
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
        initFileTemplate();
        updateFileContent();
        saveFile();
    }
}


