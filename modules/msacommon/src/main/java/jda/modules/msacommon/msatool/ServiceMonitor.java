package jda.modules.msacommon.msatool;

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
 * @overview Continuously pings a given service for <tt>status: UP</tt>, within a specified time-out constraint (in secs).
 * <p>
 * It also supports customised content-handler to handle the responses of different types of monitoring requests.
 */
public class ServiceMonitor {
  private HttpClient client;
  private HttpRequest request;
  private final String serviceMonitorUrl;

  private int timeOut;

  /**
   * String: response body,
   * Integer: check result code (0 means OK, > 0 means error)
   */
  private Function<String, Integer> contentHandler;

  public ServiceMonitor(String serviceMonitorUrl, int timeOut, Function<String, Integer> contentHandler)
      throws URISyntaxException {
    this.serviceMonitorUrl = serviceMonitorUrl;
    this.timeOut = timeOut;
    this.contentHandler = contentHandler;
    client = HttpClient.newHttpClient();
    request = HttpRequest.newBuilder()
        .uri(new URI(serviceMonitorUrl))
        .GET()
        .build();
  }

  public Function<Object, Integer> getMonitorFunc() {
    return monitorFunc;
  }

  private final Function<Object, Integer> monitorFunc = v -> {
    System.out.println("Called back: checking service is UP or not...");

    int timeCount = 1;
    int result = -1;
    boolean donePing = false;
    while (!donePing && timeCount < timeOut) {
      try {

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        result = contentHandler.apply(response.body());

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
