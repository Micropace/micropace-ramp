package com.micropace.ramp.wechat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class RampApplacation {
    public static void main(String[] args) {
        SpringApplication.run(RampApplacation.class, args);
    }
}

//@EnableScheduling
//@SpringBootApplication
//public class RampApplacation extends SpringBootServletInitializer {
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(RampApplacation.class);
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(RampApplacation.class, args);
//    }
//}