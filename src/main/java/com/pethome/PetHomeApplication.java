package com.pethome;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 宠物寄养洗护预约管理平台 · 启动类
 */
@SpringBootApplication
@ConfigurationPropertiesScan
@MapperScan("com.pethome.mapper")
@EnableScheduling
public class PetHomeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetHomeApplication.class, args);
    }
}
