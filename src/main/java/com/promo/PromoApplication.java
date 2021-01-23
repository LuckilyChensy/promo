package com.promo;

import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.promo"})
@MapperScan("com.promo.dao")
public class PromoApplication {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    public static void main( String[] args ){

        SpringApplication.run(PromoApplication.class,args);
    }

}
