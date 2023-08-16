package org.jda.eg.coursemanmsa.hello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping(value="/")
public class HelloController {
  @Autowired
  RestTemplate restTemplate;
  @RequestMapping("/")
  public ResponseEntity<?> main(HttpServletRequest req, HttpServletResponse res) throws Exception {
    return ResponseEntity.ok(HelloController.class.getSimpleName() + ": main: Hello");
  }
}
