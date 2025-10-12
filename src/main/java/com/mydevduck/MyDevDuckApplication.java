package com.mydevduck;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class MyDevDuckApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyDevDuckApplication.class, args);
    }

}
