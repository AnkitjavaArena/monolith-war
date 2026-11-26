package com.example;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SpaForwardController {

    // Sends Angular routes like /about to index.html (paths with a dot, like main.js, are skipped)
    @GetMapping({"/{path:[^\\.]*}", "/{path:[^\\.]*}/{sub:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}