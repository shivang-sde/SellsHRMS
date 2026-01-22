package com.sellspark.SellsHRMS.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    
    @Value("${app.upload.base-dir}")
    private String uploadBaseDir;

    @Value("${app.upload.url-path:/uploads}")
    private String uploadUrlPath;


     @Override public void addResourceHandlers(ResourceHandlerRegistry registry) { 
        String location = "file:" + (uploadBaseDir.endsWith("/") ? uploadBaseDir : uploadBaseDir + "/"); 
        registry.addResourceHandler(uploadUrlPath + "/**") 
        .addResourceLocations(location);
     }

}
