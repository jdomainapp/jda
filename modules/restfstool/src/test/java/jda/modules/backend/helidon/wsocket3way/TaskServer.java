/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package jda.modules.backend.helidon.wsocket3way;

import java.util.logging.Logger;

import javax.websocket.CloseReason;
import javax.websocket.Encoder;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import com.google.gson.Gson;

import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import jda.modules.backend.helidon.wsocket3way.Message.Action;

/**
 * Class MessageBoardEndpoint.
 */
public class TaskServer extends Endpoint implements Service {
  private static final Logger LOGGER = Logger
      .getLogger(TaskServer.class.getName());

  private final TaskQueue taskQueue = TaskQueue.instance();
  private static final Gson gson = new Gson();

  @Override // Service
  public void update(Routing.Rules routingRules) {
    routingRules.post("/board", this::handlePost);
  }

  private void handlePost(ServerRequest request, ServerResponse response) {
      request.content()
              .as(String.class)
              .thenAccept(it -> {
                  taskQueue.push(it);
                  response.status(204).send();
              });
  }
  
  @Override // EndPoint
  public void onOpen(Session session, EndpointConfig endpointConfig) {
    session.addMessageHandler(new MessageHandler.Whole<String>() {
      @Override
      public void onMessage(String message) {
        try {
          Message mesg = gson.fromJson(message, Message.class);
          if (mesg.isAction(Action.Get)) {
            // Send all messages in the queue
            while (!taskQueue.isEmpty()) {
              session.getBasicRemote().sendObject(taskQueue.pop());
            }
          }
        } catch (Exception e) {
          LOGGER.info(e.getMessage());
        }
      }
    });
  }

  @Override // EndPoint
  public void onClose(Session session, CloseReason closeReason) {
    super.onClose(session, closeReason);
  }

  @Override // EndPoint
  public void onError(Session session, Throwable thr) {
    super.onError(session, thr);
  }

  /**
   * Uppercase encoder.
   */
  public static class UppercaseEncoder implements Encoder.Text<String> {

    @Override
    public String encode(String s) {
      LOGGER.info("UppercaseEncoder encode called");
      return s.toUpperCase();
    }

    @Override
    public void init(EndpointConfig config) {
    }

    @Override
    public void destroy() {
    }
  }
}
