package com.bitongchong.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author liuyuehe
 * @date 2020/3/25 21:43
 * '@ComponentScan'：扫描@Component在哪儿注解的bean
 */
@Configuration
@ComponentScan(basePackages = "com.bitongchong")
public class SpringConfig {
}
