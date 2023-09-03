package org.jda.eg.coursemanmsa.reconfigurer;

import jda.modules.common.io.ToolkitIO;
import jda.modules.msacommon.msatool.MonitorAction;
import jda.modules.msacommon.msatool.ServiceMonitor;

import java.io.File;
import java.net.URISyntaxException;
import java.util.function.Function;

/**
 * @version 1.0
 * @overview
 */
public class ServiceExecutionTest {

  public static void main(String[] args) {
    File deployJarFile = new File("/data/projects/jda/examples/courseman/msa/tmp/target/hello-service/hello-service.jar");
    assert deployJarFile.exists();

    File logFile = new File("/data/projects/jda/log/service-shell.log");

    String cmd = "java -jar " + deployJarFile.getPath();
    File workDir = null;
    boolean waitFor = false;

    // create a service monitor function to check service for "Up" after
    // starting it
    Function<Object, Integer> servMonitorFunc = null;
    try {
      String serviceUrl = "http://localhost:8999";
      int timeOut = 60; //secs
      servMonitorFunc = new ServiceMonitor(serviceUrl, MonitorAction.health,
          ServiceMonitor.healthContentHandler)
          .getMonitorFunc();
    } catch (URISyntaxException e) {
      e.printStackTrace();
      return;
    }

    boolean isServiceStared = ToolkitIO.executeSysCommand(cmd, workDir, logFile, waitFor, servMonitorFunc);

    if (isServiceStared)
      System.out.println("Service execution: completed");
    else
      System.out.println("Service execution: NOT completed (successfully)");
  }
}
