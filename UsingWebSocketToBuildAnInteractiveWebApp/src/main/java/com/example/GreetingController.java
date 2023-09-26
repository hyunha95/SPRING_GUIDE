package com.example;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.util.HtmlUtils;

@Controller
public class GreetingController {

    @MessageMapping("/hello/{userId}")
    @SendTo("/topic/greetings/{userId}") // This is the destination of the message
    public Greeting greeting(HelloMessage message) throws Exception {
//        Thread.sleep(1000); // simulated delay
        return new Greeting("Hello, " + HtmlUtils.htmlEscape(message.getName()) + "!");
    }

    @GetMapping("/room/{userId}")
    public String room(@PathVariable Long userId, Model model) {
        model.addAttribute("userId", userId);

        return "index";
    }
}
