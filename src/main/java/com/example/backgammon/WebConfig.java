package com.example.backgammon;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Allow all paths to be accessed from your React app running on localhost:5173
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "adameheaney.github.io")  // Allow requests from React
                .allowedMethods("GET", "POST", "OPTIONS", "PUT", "DELETE")
                .allowedHeaders("*"); // Allow all headers
    }
}
