package jda.modules.tmsa.tasl.conceptmodel.infra;


import jda.modules.tasltool.contracts.IApp;
import jda.modules.tasltool.contracts.IGenerator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigServer implements IGenerator, IApp {
    private String url;
    private String configFilesPath;

    private List<File> configFiles = new ArrayList<>();
    public void addConfigFiles(File[] configFile) {
        // TODO:
    }

    public void run() {

    }

    public void generate() {
        // if path exist
            // ignore copy template

        // generate config files
    }
}
