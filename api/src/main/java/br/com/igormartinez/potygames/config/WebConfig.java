package br.com.igormartinez.potygames.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
 
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors.originPatterns:default}")
    private String corsOriginPatterns = "";

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        
        // Content Negotiation using HEADER
        configurer.favorParameter(true)
            .ignoreAcceptHeader(false)
            .useRegisteredExtensionsOnly(false)
            .defaultContentType(MediaType.APPLICATION_JSON);
        
        WebMvcConfigurer.super.configureContentNegotiation(configurer);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        String[] allowedOrigins = corsOriginPatterns.split(",");
        registry.addMapping("/**")
            //.allowedMethods("GET","POST","PUT")
            .allowedMethods("*")
            .allowedOriginPatterns(allowedOrigins)
            .allowCredentials(true);
        WebMvcConfigurer.super.addCorsMappings(registry);
    }
}
