package com.intervueai.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntervueaiApplication {

    public static void main(String[] args) {
        String dbUrl = System.getenv("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = System.getenv("DATABASE_URL");
        }
        if (dbUrl != null && !dbUrl.isBlank()) {
            if (dbUrl.startsWith("postgres://")) {
                dbUrl = "jdbc:postgresql://" + dbUrl.substring("postgres://".length());
                System.setProperty("spring.datasource.url", dbUrl);
            } else if (dbUrl.startsWith("postgresql://") && !dbUrl.startsWith("jdbc:postgresql://")) {
                dbUrl = "jdbc:" + dbUrl;
                System.setProperty("spring.datasource.url", dbUrl);
            }
        }
        SpringApplication.run(IntervueaiApplication.class, args);
    }

}
