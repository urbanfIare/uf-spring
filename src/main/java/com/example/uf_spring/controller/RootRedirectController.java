package com.example.uf_spring.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootRedirectController {
    @GetMapping("/")
    public String redirectToBoard() {
        return "redirect:/board.html";
    }
} 