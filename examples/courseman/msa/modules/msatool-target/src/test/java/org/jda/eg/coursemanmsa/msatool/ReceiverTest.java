package org.jda.eg.coursemanmsa.msatool;

import java.io.File;

import org.jda.example.coursemanmsa.common.msatool.ServiceReconfigurer;

public class ReceiverTest {
    public static void main(String[] args) {
    	ServiceReconfigurer receiver = new ServiceReconfigurer();
    	String receiverLocation = System.getProperty("user.dir")+File.separator+"execute";
    	String fileName = "coursemodule-service.jar";
    	receiver.receiveJar(4444, receiverLocation,fileName);
    }
}
