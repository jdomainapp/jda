package jda.modules.common.test.io;

import jda.modules.common.zipextractor.ZipExtractor;
import org.junit.Test;

import java.io.File;

/**
 * @version 1.0
 * @overview
 */
public class ZipExtractorTest {
  public static void main(String[] args) {
    new ZipExtractorTest().testZipExtractor();
  }

  @Test
  public void testZipExtractor() {
    File tmpDir = new File(System.getProperty("java.io.tmpdir"));
    String zipFilePath = "/home/ducmle/short/jda.eg/temp/target/jda-eg-temp-5.4-SNAPSHOT.jar";
    File zipFile = new File(zipFilePath);

    System.out.printf("Extracting zip file (%s) to folder: %s%n", zipFile, tmpDir);
    File extractedFolder = ZipExtractor.extractFile(zipFile, tmpDir);
    System.out.printf("...extracted folder: %s%n", extractedFolder);
  }
}
