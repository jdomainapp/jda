package jda.modules.msacommon.msatool;

import org.springframework.http.HttpMethod;

import javax.json.Json;
import javax.json.JsonException;
import javax.json.JsonObject;
import javax.json.JsonReader;
import java.io.StringReader;
import java.net.ConnectException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.function.Function;

/**
 * @overview Represents a service monitoring service, which queries and performs actions on the associated actuators of the services.
 *
 * <p>For example, it continuously pings a given service for <tt>status: UP</tt>, within a specified time-out constraint (in secs).
 * <p>
 * It also supports customised content-handler to handle the responses of different types of monitoring requests.
 */
public class ServiceMonitor {
  public static final int DEFAULT_TIME_OUT = 60;  // secs

  private static HttpClient client;
  private HttpRequest request;
  private final String serviceMonitorUrl;

  private int timeOut;

  /**
   * String: response body,
   * Integer: check result code (0 means OK, > 0 means error)
   */
  private Function<String, Integer> contentHandler;

  /**
   * Uses this to create an asynchronous monitoring function that is only invoked when a certain event occurs.
   *
   * @effects
   *  creates a new {@link ServiceMonitor} instance and uses it to obtain a monitor function
   *  through {@link #getMonitorFunc()}. This function can then be passed as call-back function
   *  to be invoked when a certain event occurs.
   *
   * @version 1.0
    */
  private ServiceMonitor(String serviceMonitorUrl, int timeOut, Function<String, Integer> contentHandler)
      throws URISyntaxException {
    /*this.serviceMonitorUrl = serviceMonitorUrl;
    this.timeOut = timeOut;
    this.contentHandler = contentHandler;
    if (client == null)
      client = HttpClient.newHttpClient();

    request = HttpRequest.newBuilder()
        .uri(new URI(serviceMonitorUrl))
        .GET()
        .build();*/
    this(getRequest(serviceMonitorUrl, HttpMethod.GET), timeOut, contentHandler);
  }

  /**
   * Uses this to create an asynchronous monitoring function that is only invoked when a certain event occurs.
   *
   * @effects
   *  creates a new {@link ServiceMonitor} instance and uses it to obtain a monitor function
   *  through {@link #getMonitorFunc()}. This function can then be passed as call-back function
   *  to be invoked when a certain event occurs.
   *
   * @version 1.0
   */
  private ServiceMonitor(HttpRequest request, int timeOut, Function<String, Integer> contentHandler)
      throws URISyntaxException {
    this.serviceMonitorUrl = request.uri().toString();
    this.timeOut = timeOut;
    this.contentHandler = contentHandler;
    if (client == null)
      client = HttpClient.newHttpClient();

    this.request = request;
  }

  public ServiceMonitor(String serviceUrl, MonitorAction action, Function<String, Integer> contentHandler)
      throws URISyntaxException {
    this(getRequest(serviceUrl, action), DEFAULT_TIME_OUT, contentHandler);
  }

  private static HttpRequest getRequest(String serviceMonitorUrl, HttpMethod method) throws URISyntaxException {
    return HttpRequest.newBuilder()
        .uri(new URI(serviceMonitorUrl))
        .method(method.name(), HttpRequest.BodyPublishers.noBody())
        .build();
  }

  static HttpRequest getRequest(String serviceMonitorUrl, MonitorAction action) throws URISyntaxException {
    return HttpRequest.newBuilder()
        .uri(new URI(getRequestUrl(serviceMonitorUrl, action)))
        .method(action.httpMethod(), HttpRequest.BodyPublishers.noBody())
        .build();
  }

  public static String getRequestUrl(String serviceUrl, MonitorAction action) {
    return serviceUrl + "/actuator/"+ action.name();
  }

  /**
   * Uses this to create a synchronous monitoring function that is invoked immediately.
   * @effects
   *  executes a request specified by the parameters and returns the result immediately.
   *  Result is <tt>true</tt> if execution succeeded or <tt>false</tt> if otherwise.
   * @version 1.0
   */
  public static boolean executeRequest(String serviceUrl, MonitorAction action, int timeOut, Function<String, Integer> contentHandler) {
    int result = 0;
    try {
      result = new ServiceMonitor(getRequest(serviceUrl, action), timeOut, contentHandler)
           .getMonitorFunc().apply(null);
    } catch (URISyntaxException e) {
      result = 1;
    }

    return result == 0;
  }

  /**
   * Uses this to create a synchronous monitoring function that is invoked immediately.
   * @effects
   *  executes a request specified by the parameters and returns the result immediately.
   *  Result is <tt>true</tt> if execution succeeded or <tt>false</tt> if otherwise.
   * @version 1.0
   */
  public static boolean executeRequest(String serviceUrl, MonitorAction action, Function<String, Integer> contentHandler) {
    return executeRequest(serviceUrl, action, DEFAULT_TIME_OUT, contentHandler);
  }

  public Function<Object, Integer> getMonitorFunc() {
    return monitorFunc;
  }

  /**
   * @effect
   *  sends {@link #request} to target and process the response (optionally) using {@link #contentHandler} if it is specified.
   *
   *  Returns 0 if ok or a positive value (e.g. 1) if an error occured.
   */
  private final Function<Object, Integer> monitorFunc = v -> {
    System.out.println("Called back: checking service status...");

    int timeCount = 1;
    int result = -1;
    boolean donePing = false;
    while (!donePing && timeCount < timeOut) {
      try {

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (contentHandler != null)
          result = contentHandler.apply(response.body());
        else
          result = 0;

        donePing = true;
      } catch (ConnectException e) {
        result = 1;
        System.out.print("!->");
        // retry until timeout
      } catch (Exception e) {
        result = 1;
        System.out.print("!->");
      }

      if (!donePing) {
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
      }
    }
    System.out.println("!");
    return result;
  };

  public static Function<String,Integer> healthContentHandler = s -> {
    int result;
    try {
      JsonReader jsonReader = Json.createReader(new StringReader(s));
      JsonObject json = jsonReader.readObject();
      jsonReader.close();
      String status = json.getString("status");

      if (status.equalsIgnoreCase("UP")) {
        result = 0;
      } else {
        result = 1;
      }
    } catch (JsonException e) {
      result = 1;
      e.printStackTrace();
      //throw new RuntimeException(e);
    }

    return result;
  };
}
