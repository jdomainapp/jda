package jda.modules.msacommon.msatool;

import jda.modules.common.io.ToolkitIO;
import org.eclipse.jetty.http.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import javax.json.JsonObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.Properties;

public class ServiceReconfigurer {

	private static final Logger logger = LoggerFactory.getLogger(ServiceReconfigurer.class);
	/**
	 * Sending jar file over network
	 * @deprecated use sendFile instead
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
				logger.error(e.getMessage() + ": Error closing");
			}
		}
	}
	

	/**
	 * Receive jar file
	 * @deprecated use sendFile instead
	 */
	public void receiveJar(int listenPort, String receiverLocation, String fileName) {

		InputStream in = null;
		OutputStream out = null;
		ServerSocket serverSocket = null;
		Socket socket = null;

		try {

			File fileFolder = new File(receiverLocation);
			if (!fileFolder.exists()) {
				fileFolder.mkdir();
			}

			serverSocket = new ServerSocket(listenPort);
			// always listen connections from sending side
			while (true) {
				try {
					logger.info("Waiting...");
					socket = serverSocket.accept();
					logger.info("Accepted connection : " + socket);
					in = socket.getInputStream();
					out = new FileOutputStream(new File(receiverLocation + File.separator + fileName));
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
	 * Transform a module to a service
	 */
	public static String transformToService(String contextFile, MultipartFile databaseSchema) {
		JsonObject context = ToolkitIO.readJSonObjectFile(contextFile);
		String serviceDir = context.getString("serviceDir");
		String serviceResoureDir = context.getString("serviceResourceDir");
		String deployedAtParent = context.getString("deployedAtParent");
		String appPropertiesPath = serviceResoureDir+File.separator+"application.properties";
		OutputStream os = null;
		try {
			os = new FileOutputStream(appPropertiesPath);
			Properties appProps = new Properties();
			appProps.setProperty("deployedAtParent", deployedAtParent);
			appProps.store(os, "For calling parent service");
			databaseSchema.transferTo(new File (serviceResoureDir+File.separator+"schema.sql"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			serviceDir=null;
		} finally {
			if (os != null)
				try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return serviceDir;
	}

	/**
	 * Package a service to jar file
	 */
	public static boolean packageServiceToJar(File workDir) {
		// TODO: can't run???
		String cmd = "mvn clean package spring-boot:repackage";
		boolean result = ToolkitIO.executeSysCommand(workDir, cmd);

		return result;
	}

	/**
	 * Given a service that has been packaged in a jar file, run the service using
	 * the command line.
	 * 
	 * @requires <tt>serviceJar</tt> is the proper <tt>jar</tt> file of a SpringBoot
	 *           service, that has been packaged with the command
	 *           <tt>mvn package spring-boot:repackage</tt>
	 * 
	 * @effects if the specified service is executed successfully return true else
	 *          return false
	 */
	public static boolean runServiceFromJar(File serviceJar) {
		String cmd = "java -jar " + serviceJar.getPath();
		File workDir = null;
		boolean result = ToolkitIO.executeSysCommand(workDir, cmd);

		return result;
	}

	/**
	 * 
	 */
	public void onRegistrationCompleted() {

	}

	/**
	 * Call moduleParent to remove Module
	 */
	public boolean onDeploymentCompleted(String removeChildPath) {
		DataOutputStream out = null;
		try {
			URL url = new URL(removeChildPath);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod(HttpMethod.POST.toString());
			con.setDoOutput(true);
			out = new DataOutputStream(con.getOutputStream());
			out.flush();
			int status = con.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				con.disconnect();
				logger.info("Deploy module to service successfully!!!");
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		} finally {
			if (out != null)
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return false;
	}
}
