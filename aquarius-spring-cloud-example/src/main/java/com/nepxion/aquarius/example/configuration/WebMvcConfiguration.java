package com.nepxion.aquarius.example.configuration;

/**
 * <p>Title: Nepxion Aquarius</p>
 * <p>Description: Nepxion Aquarius</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Configuration
@EnableSwagger2
public class WebMvcConfiguration extends WebMvcConfigurerAdapter {
    @Value("${spring.application.name}")
    private String serviceName;

    @Value("${swagger.service.base.package}")
    private String basePackage;

    @Value("${swagger.service.description}")
    private String description;

    @Value("${swagger.service.version}")
    private String version;

    @Value("${swagger.service.license.name}")
    private String license;

    @Value("${swagger.service.license.url}")
    private String licenseUrl;

    @Value("${swagger.service.contact.name}")
    private String contactName;

    @Value("${swagger.service.contact.url}")
    private String contactUrl;

    @Value("${swagger.service.contact.email}")
    private String contactEmail;

    @Value("${swagger.service.termsOfServiceUrl}")
    private String termsOfServiceUrl;

    @Bean("aquariusDocket")
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("aquarius")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage(basePackage)) // 扫描该包下的所有需要在Swagger中展示的API，@ApiIgnore注解标注的除外
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title(serviceName)
                .description(description)
                .version(version)
                .license(license)
                .licenseUrl(licenseUrl)
                .contact(new Contact(contactName, contactUrl, contactEmail))
                .termsOfServiceUrl(termsOfServiceUrl)
                .build();
    }

    // 解决跨域问题
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowedOrigins("*");
    }
}