package org.jda.example.coursemanmsa.student.events.handler;

import org.jda.example.coursemanmsa.student.events.model.AddressChangeModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;


public class AddressChangeHandler {

    private static final Logger logger = LoggerFactory.getLogger(AddressChangeHandler.class);

//    @StreamListener("inboundOrgChanges")
//    public void loggerSink(AddressChangeModel address) {
//    	
//        logger.debug("Received a message of type " + address.getType());
//        
//        switch(address.getAction()){
//            case "GET":
//                logger.debug("Received a GET event from the organization service for address id {}", address.getAddressId());
//                break;
//            case "SAVE":
//                logger.debug("Received a SAVE event from the organization service for address id {}", address.getAddressId());
//                break;
//            case "UPDATE":
//                logger.debug("Received a UPDATE event from the organization service for address id {}", address.getAddressId());
//                break;
//            case "DELETE":
//                logger.debug("Received a DELETE event from the organization service for address id {}", address.getAddressId());
//                break;
//            default:
//                logger.error("Received an UNKNOWN event from the organization service of type {}", address.getType());
//                break;
//        }
//    }


}
