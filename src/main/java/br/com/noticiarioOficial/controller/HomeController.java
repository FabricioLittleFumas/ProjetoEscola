package br.com.noticiarioOficial.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Bem-vindo ao Noticiário Oficial!";
    }

    @GetMapping("/welcome")
    public String welcome() {
        return "Bem-vindo à área autenticada!";
    }
}