package org.example;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class Controller {

    @RequestMapping("/dropdown")
    public String[] dropdown() {
        return null;
    }

    @RequestMapping("/insert")
    public String insert() {
        return null;
    }

    @RequestMapping("/pdf")
    public String pdf(int id) {
        return null;
    }
}
