package com.example.hotalproject.media;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class UploadsWebConfig implements WebMvcConfigurer {

    private final Path uploadBasePath;

    public UploadsWebConfig(@Value("${app.upload.base-dir:uploads}") String baseDir) {
        this.uploadBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadBasePath.toUri().toString());
    }
}
