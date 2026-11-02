package org.example.route;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ComponentScan(basePackages = "org.route")
@PropertySource("classpath:application.properties")
public class AppConfig {
}
