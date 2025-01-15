package com.example.backgammon;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // Allow all endpoints
                .allowedOrigins("http://localhost:5173", "adameheaney.github.io")  // Allow React frontend
                .allowedMethods("GET", "POST", "OPTIONS")  // Allow methods
                .allowedHeaders("Content-Type", "Authorization", "X-Requested-With", "Accept")
                .allowCredentials(true); // Allow cookies if needed
    }
}