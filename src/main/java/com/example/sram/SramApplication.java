package com.example.sram;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 서버실 출입관리 시스템 진입점.
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.example.sram.mapper")
public class SramApplication {

    public static void main(String[] args) {
        SpringApplication.run(SramApplication.class, args);
    }
}
