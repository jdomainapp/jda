package jda.modules.backend.helidon;

import io.helidon.common.http.Http;
import io.helidon.config.Config;
import io.helidon.webserver.Routing;
import io.helidon.webserver.StaticContentSupport;
import io.helidon.webserver.WebServer;

/**
 * @overview 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class Main {
  public static void main(String[] args) {
    System.out.println("Welcome to my application!");
    
    // create routing with all the paths and handlers 
    Routing routing = Routing.builder()
        .any("/", (req, res) -> {
          // showing the capability to run on any path, and redirecting from root
          res.status(Http.Status.MOVED_PERMANENTLY_301);
          res.headers().put(Http.Header.LOCATION, "/app");
          res.send();
      })
        // WEB: located at main/resources/WEB
      .register("/app", StaticContentSupport.builder("WEB")
              .welcomeFileName("index.html")
              .build())
      .build();
    
    // start the web server with the routing
    Config config = Config.create();
    WebServer ws = WebServer.create(routing, config.get("server"));
    ws.start();
  }
}
