package cn.halen.filter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackageClasses = {FilterConfig.class})
public class FilterConfig {
    @Bean
    public UserFilterChain userFilterChain() {
    	return new UserFilterChain();
    }
}
