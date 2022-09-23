package org.jda.eg.coursemanmsa.msatool;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.io.ToolkitIO;

public class ServiceReconfigurer {

	private static final Logger logger = LoggerFactory.getLogger(ServiceReconfigurer.class);

	/**
	 * Sending jar file over network
	 */
	public void sendJar(String serverHost, int serverPort, String filePath) {
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			socket = new Socket(serverHost, serverPort);
			if (filePath.endsWith(".jar")) {
				File jarFile = new File(filePath);
				long length = jarFile.length();
				in = new FileInputStream(jarFile);
				out = socket.getOutputStream();

				byte[] buffer = new byte[4069];
				int count;
				while ((count = in.read(buffer)) > 0) {
					out.write(buffer, 0, count);
				}
				out.flush();
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage() + ": Error when sending jar file");
		} finally {
			try {
				if (out != null)
					out.close();
				if (in != null)
					in.close();
				if (socket != null)
					socket.close();
			} catch (Exception e) {
				logger.error(e.getMessage()+": Error closing");
			}
		}
	}

	/**
	 * Receive jar file
	 */
	public void receiveJar(int listenPort, String filePath) {

		InputStream in = null;
		OutputStream out = null;
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {
			serverSocket = new ServerSocket(listenPort);
			// always listen connections from sending side
			while (true) {
				try {
					logger.info("Waiting...");
					socket = serverSocket.accept();
					logger.info("Accepted connection : " + socket);
					in = socket.getInputStream();
					out = new FileOutputStream(new File(filePath));
					int count;
					byte[] buffer = new byte[4096];
					while ((count = in.read(buffer)) > 0) {
						out.write(buffer, 0, count);
					}
					out.flush();
				} catch (IOException ex) {
					ex.printStackTrace();
					logger.error(ex.getMessage() + ": An Inbound Connection Was Not Resolved");
				} finally {
					if (out != null)
						out.close();
					if (in != null)
						in.close();
					if (socket != null)
						socket.close();
				}
			}
		} catch (IOException e) {
			logger.error(e.getMessage() + ": Error when receiving Jar file!!!");
		} finally {
			if (serverSocket != null)
				try {
					serverSocket.close();
				} catch (IOException e) {
					logger.error(e.getMessage());
				}
		}
	}
	
	/**
   * Given a service that has been packaged in a jar file, run the service 
   * using the command line.
   *  
   * @requires
   *   <tt>serviceJar</tt> is the proper <tt>jar</tt> file of a SpringBoot service, 
   *   that has been packaged with the command <tt>mvn package spring-boot:repackage</tt> 
   *   
   * @effects 
   *   if the specified service is executed successfully
   *    return true
   *   else
   *    return false
   */
  public boolean runServiceFromJar(File serviceJar) {
    String cmd = "java -jar " + serviceJar.getPath();
    File workDir = null;
    boolean result = ToolkitIO.executeSysCommand(workDir, cmd);
    
    return result;
  }
}
