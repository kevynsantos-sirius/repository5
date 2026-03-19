package com.totaldocs.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class SpaConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        // 1 nível
        registry.addViewController("/{path:[^\\.]*}")
                .setViewName("forward:/index.html");

        // múltiplos níveis (ESSENCIAL)
        registry.addViewController("/**/{path:[^\\.]*}")
                .setViewName("forward:/index.html");

        registry.addViewController("/")
                .setViewName("forward:/index.html");
    }
}