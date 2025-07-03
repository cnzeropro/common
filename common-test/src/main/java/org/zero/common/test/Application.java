package org.zero.common.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.zero.common.core.extension.spring.beans.factory.support.CustomBeanNameGenerator;

@EnableFeignClients(basePackages = "org.zero.common.api")
@SpringBootApplication(nameGenerator = CustomBeanNameGenerator.class)
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
