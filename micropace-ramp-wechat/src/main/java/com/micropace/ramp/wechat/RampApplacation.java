package com.micropace.ramp.wechat;

import com.micropace.ramp.RampCoreConfigure;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;



@Import(RampCoreConfigure.class)
@EnableScheduling
@SpringBootApplication
public class RampApplacation {
    public static void main(String[] args) {
        SpringApplication.run(RampApplacation.class, args);
    }
}

//
//@Import(com.micropace.ramp.DaoConfigure.class)
//@EnableScheduling
//@SpringBootApplication
//public class RampApplacation extends SpringBootServletInitializer {
//    public static void main(String[] args) {
//        SpringApplication.run(RampApplacation.class, args);
//    }
//
//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(RampApplacation.class);
//    }
//}