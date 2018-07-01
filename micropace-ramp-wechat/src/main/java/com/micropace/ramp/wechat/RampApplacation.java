package com.micropace.ramp.wechat;

import com.micropace.ramp.core.RampCoreConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@ComponentScan
@Import(RampCoreConfiguration.class)
@EnableScheduling
@SpringBootApplication
public class RampApplacation {
    public static void main(String[] args) {
        SpringApplication.run(RampApplacation.class, args);
    }
}
