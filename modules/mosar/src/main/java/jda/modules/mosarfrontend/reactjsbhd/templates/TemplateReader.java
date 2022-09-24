package jda.modules.mosarfrontend.reactjsbhd.templates;

import jda.modules.mosar.utils.FileUtils;

final class TemplateReader {

    /**
     * Read template from /resources/react/templates folder
     *
     * @param fileName
     * @return
     */
    synchronized static String readFromFile(String fileName) {
        return FileUtils.readWholeFile(TemplateReader.class.getClassLoader()
                .getResource("react/templates/" + fileName + ".js").getFile());
    }
}
