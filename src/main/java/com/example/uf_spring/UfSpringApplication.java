package com.example.uf_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.uf_spring",
    "com.example.uf_spring.config",
    "com.example.uf_spring.controller",
    "com.example.uf_spring.service",
    "com.example.uf_spring.repository"
})
public class UfSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(UfSpringApplication.class, args);
	}

}
