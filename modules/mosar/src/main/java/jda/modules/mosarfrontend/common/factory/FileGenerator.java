package jda.modules.mosarfrontend.common.factory;

import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.*;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexUtils {
    String createSlotRegex(String slot) {
        return String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot);
    }

    String createLoopRegex(LoopReplacement loop) {
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
public class FileGenerator {
    @NonNull
    private Class<?> FileTemplateDesc;
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
        if (!FileTemplateDesc.isAnnotationPresent(FileTemplateDesc.class)) {
            throw new Exception("The class is not TemplateHandler (without @TemplateHandler annotation)");
        } else {
            FileTemplateDesc ano = FileTemplateDesc.getAnnotation(FileTemplateDesc.class);
            this.handler = this.FileTemplateDesc.getConstructor().newInstance();
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

    private void updateFileName(Method withFileName){
        try {
            this.fileName = (String) withFileName.invoke(this.handler, paramsFactory.getParamsForMethod(withFileName));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void updateFilePath(Method withFilePath){
        try {
            this.filePath = (String) withFilePath.invoke(this.handler, paramsFactory.getParamsForMethod(withFilePath));
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void replaceSlot(Method replaceMethod) {
        try {
            String value = (String) replaceMethod.invoke(this.handler, paramsFactory.getParamsForMethod(replaceMethod));
            SlotReplacement desc = new SlotReplacement();
            SlotReplacementDesc ano = replaceMethod.getAnnotation(SlotReplacementDesc.class);
            RFSGenTk.parseAnnotation2Config(ano, desc);
            this.fileContent = this.fileContent
                    .replaceAll(regexUtils.createSlotRegex(desc.getSlot()), value);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void replaceLoops(Method replaceMethod) {
        try {
            Slot[][] loopValues = (Slot[][]) replaceMethod.invoke(this.handler, paramsFactory.getParamsForMethod(replaceMethod));
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
                for (Slot[] loopValue : loopValues) {
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
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void updateFileContent() {
        for (Method method : this.FileTemplateDesc.getMethods()) {
            if (method.getReturnType() == String.class || method.getReturnType() == Slot[][].class) {
                if (method.isAnnotationPresent(LoopReplacementDesc.class)) {
                    replaceLoops(method);
                };
                if (method.isAnnotationPresent(SlotReplacementDesc.class)) {
                    replaceSlot(method);
                };
                if (method.isAnnotationPresent(CustomFileName.class)) {
                    updateFileName(method);
                };
                if (method.isAnnotationPresent(CustomFilePath.class)) {
                    updateFilePath(method);
                };
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
        Path classFile = new File(outPutFolder + this.filePath + this.fileName + this.fileExt).toPath();
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


