package jda.modules.mosarfrontend.common.factory;

import jda.modules.mccl.conceptualmodel.MCC;
import jda.modules.mosar.utils.RFSGenTk;
import jda.modules.mosarfrontend.common.anotation.*;
import lombok.Data;
import lombok.NonNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class RegexUtils {
    String createSlotRegex(String slot) {
        return String.format("@slot\\{\\{\\s*%s\\s*\\}\\}", slot);
    }

    String createLoopRegex(String[] slots) {
        StringBuilder singleSlotsRegex = new StringBuilder();
        for (String slot : slots) {
            singleSlotsRegex.append(createSlotRegex(slot));
        }
        return String.format("@loop(?<li>[_\\w\\d]*)\\[\\[(.*%s)]]loop(\\k<li>)@",
                singleSlotsRegex);
    }
}

@Data
public class FileGenerator {
    @NonNull
    private Class<?> FileTemplateDesc;
    @NonNull
    private GenConfig genConfig;
    private Class<?> moduleClass;
    private Object handler;
    private FileTemplate config;
    private String fileContent = null;
    private String fileName = null;
    private String filePath = null;
    private String fileExt = null;
    private Method getFileName = null;
    private Method getFilePath = null;
    private ArrayList<Method> singleReplacement = new ArrayList<>();
    private ArrayList<Method> loopReplacement = new ArrayList<>();
    private RegexUtils regexUtils = new RegexUtils();

    private void initConfig() throws Exception {
        if (!FileTemplateDesc.isAnnotationPresent(FileTemplateDesc.class)) {
            throw new Exception("The class is not TemplateHandler (without @TemplateHandler annotation)");
        } else {
            FileTemplateDesc ano = FileTemplateDesc.getAnnotation(FileTemplateDesc.class);
            this.handler = this.FileTemplateDesc.getConstructor().newInstance();
            this.config = new FileTemplate();
            RFSGenTk.parseAnnotation2Config(ano, this.config);
            // default output file info
            initDefaultFileInfo();
            // get template file content
            String templateFilePath = this.genConfig.getTemplateRootFolder() + this.config.getTemplateFile().replace("/", "\\");
            try {
                this.fileContent = Files.readString(Paths.get(templateFilePath));
            } catch (IOException e) {
                throw new Exception("Template file not found");
            }
        }
    }

    private void initDefaultFileInfo() {
        StringBuilder buffer = new StringBuilder("");
        String templateFile = this.config.getTemplateFile();
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

    private void initMethods() {
        for (Method method : this.FileTemplateDesc.getMethods()) {
            if (method.getReturnType() == String.class || method.getReturnType() == Slot[][].class) {
                if (method.isAnnotationPresent(LoopReplacementDesc.class)) this.loopReplacement.add(method);
                if (method.isAnnotationPresent(SlotReplacementDesc.class)) this.singleReplacement.add(method);
                if (method.isAnnotationPresent(GetFileName.class)) this.getFileName = method;
                if (method.isAnnotationPresent(GetFilePath.class)) this.getFilePath = method;
            }
        }
    }

    private void updateFileInfo() {
        if (getFileName != null) {
            try {
                this.fileName = (String) getFileName.invoke(this.handler, getMethodArgs(getFileName));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        if (getFilePath != null) {
            try {
                this.filePath = (String) getFilePath.invoke(this.handler, getMethodArgs(getFilePath));
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private Object[] getMethodArgs(Method method) {
        Parameter[] parameters = method.getParameters();
        ArrayList<Object> args = new ArrayList<>();
        for (Parameter p : parameters) {
            if (p.isAnnotationPresent(RequireParam.MCC.class) && p.getType() == MCC.class
                    && this.moduleClass != null) {
                args.add(this.genConfig.getModelModuleMap().get(this.moduleClass));
            } else if (p.isAnnotationPresent(RequireParam.ModuleMap.class) && p.getType() == Map.class) {
                args.add(this.genConfig.getModelModuleMap());
            }
        }
        return args.toArray();
    }

    private void replaceSingleSlot() {
        for (Method method : this.singleReplacement) {
            try {
                String value = (String) method.invoke(this.handler, getMethodArgs(method));
                SlotReplacement desc = new SlotReplacement();
                SlotReplacementDesc ano = method.getAnnotation(SlotReplacementDesc.class);
                RFSGenTk.parseAnnotation2Config(ano, desc);
                this.fileContent = this.fileContent
                        .replaceAll(regexUtils.createSlotRegex(desc.getSlot()), value);
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    private void replaceLoopSlots() {
        for (Method method : this.loopReplacement) {
            try {
                Slot[][] loopValues = (Slot[][]) method.invoke(this.handler, getMethodArgs(method));
                LoopReplacement desc = new LoopReplacement();
                LoopReplacementDesc ano = method.getAnnotation(LoopReplacementDesc.class);
                RFSGenTk.parseAnnotation2Config(ano, desc);

                final String loopRegex = regexUtils.createLoopRegex(desc.getSlots());
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
                    System.out.println(replaceValue.toString());
                    System.out.println(this.fileContent);
                }
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
//        System.out.println(this.fileContent);
    }

    private void saveFile() {
        Path path = new File(genConfig.getOutputFolder()).toPath();
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path dir = new File(genConfig.getOutputFolder() + this.filePath).toPath();
        if (!Files.exists(dir)) {
            try {
                Files.createDirectory(dir);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Path classFile = new File(genConfig.getOutputFolder() + this.filePath + this.fileName + this.fileExt).toPath();
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
        initConfig();
        initMethods();
        updateFileInfo();
        replaceSingleSlot();
        replaceLoopSlots();
        saveFile();
    }
}


