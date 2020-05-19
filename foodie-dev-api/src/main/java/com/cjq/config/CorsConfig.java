package com.cjq.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * @author 被狗追过的夏天
 * date 2020-05-20
 */
@Configuration
public class CorsConfig {

    public CorsConfig() {
    }

    @Bean
    public CorsFilter corsFilter(){
        //添加cors配置信息
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("http://localhost:8080");

        //设置是否可以发送cookie
        corsConfiguration.setAllowCredentials(true);
        //设置允许请求的方式
        corsConfiguration.addAllowedMethod("*");
        //设置允许请求header
        corsConfiguration.addAllowedHeader("*");

        //为url添加映射路径
        UrlBasedCorsConfigurationSource urlSource = new UrlBasedCorsConfigurationSource();
        urlSource.registerCorsConfiguration("/**",corsConfiguration);

        //返回重新定义好的corsSource
        return new CorsFilter(urlSource);
    }
}
