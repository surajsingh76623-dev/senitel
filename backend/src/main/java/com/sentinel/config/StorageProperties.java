package com.sentinel.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "sentinel.storage")
public class StorageProperties {

    private String location = "./uploads";
    private int maxFileSizeMb = 15;
    private List<String> allowedExtensions = List.of("txt", "log", "json", "csv", "md", "pdf", "png", "jpg", "jpeg", "webp");

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMaxFileSizeMb() { return maxFileSizeMb; }
    public void setMaxFileSizeMb(int maxFileSizeMb) { this.maxFileSizeMb = maxFileSizeMb; }

    public List<String> getAllowedExtensions() { return allowedExtensions; }
    public void setAllowedExtensions(List<String> allowedExtensions) { this.allowedExtensions = allowedExtensions; }
}
