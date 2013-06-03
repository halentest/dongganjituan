package cn.halen.web;

import java.io.IOException;
import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import cn.halen.filter.UserFilterChain;

@Configuration
@EnableWebMvc
@ComponentScan(basePackageClasses = {MvcConfig.class})
public class MvcConfig extends WebMvcConfigurerAdapter {
	@Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("*.html");
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        ResourceHandlerRegistration registration = registry.addResourceHandler("/js/**", "/css/**", "/img/**");
        registration.addResourceLocations("/", "/js/", "/css/", "/img/");
    }
    
	@Bean
    public FreeMarkerConfigurer freeMarker() {
        FreeMarkerConfigurer config = new FreeMarkerConfigurer();
        config.setTemplateLoaderPath("/WEB-INF/ftl/");
        Properties setting = new Properties();
        config.setFreemarkerSettings(setting);
        config.setDefaultEncoding("UTF-8");
        return config;
    }

    @Bean
    public ViewResolver freeMarkerViewResolver() throws IOException {
        FreeMarkerViewResolver freeMarker = new FreeMarkerViewResolver();
        freeMarker.setContentType("text/html;charset=UTF-8");
        freeMarker.setExposeRequestAttributes(true);
        freeMarker.setRequestContextAttribute("rc");
        freeMarker.setExposeSpringMacroHelpers(true);
        freeMarker.setOrder(1);
        freeMarker.setSuffix(".ftl");
        return freeMarker;
    }
}
