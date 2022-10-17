package org.jda.eg.coursemanmsa.msatool;

import java.io.File;

import org.jda.example.coursemanmsa.common.msatool.ServiceReconfigurer;

public class SenderTest {
    public static void main(String[] args) {
    	ServiceReconfigurer sender = new ServiceReconfigurer();
    	String sendingFilePath="/home/vietdo/Ha/JDA/Git/jda/examples/courseman/msa/modules/servicestmsa/coursemodulemgmt-service/target/"
    			+ "cmodulemgnt-service-0.0.1-SNAPSHOT.jar";
    	sender.sendJar("127.0.0.1", 4444, sendingFilePath);
    }
}
