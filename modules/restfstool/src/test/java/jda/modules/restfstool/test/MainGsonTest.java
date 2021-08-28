package jda.modules.restfstool.test;

import org.junit.Test;

import com.google.gson.Gson;

import jda.modules.backend.helidon.wsocket3way.Message;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class MainGsonTest {
  @Test
  public void main() {
    Gson gson = new Gson();
    Message mesg = new Message(Message.Action.Get.name(),"1");
    
    System.out.println("Object: " + mesg);
    
    String json = gson.toJson(mesg);
    System.out.println("serialised: " + json);
    
    Message der = gson.fromJson(json, Message.class);
    
    System.out.println("deserialised: " + der);
  }
}
