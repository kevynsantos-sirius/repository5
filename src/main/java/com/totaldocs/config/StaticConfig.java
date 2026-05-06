//package com.totaldocs.config;
//
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//
//@Configuration
//public class StaticConfig implements WebMvcConfigurer {
//
//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//
//        registry
//            .addResourceHandler("/assets/**")
//            .addResourceLocations("file:///C:/SIRIUS/ChecklistOnline/build/assets/");
//
//        registry
//            .addResourceHandler("/", "/index.html")
//            .addResourceLocations("file:///C:/SIRIUS/ChecklistOnline/build/");
//    }
//}