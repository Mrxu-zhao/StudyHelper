package com.studyhelper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger API文档配置（可选）
 * 如果需要Swagger文档，取消注释相关依赖
 */
// @Configuration
// @EnableOpenApi
// public class SwaggerConfig {
    
//     @Bean
//     public Docket createRestApi() {
//         return new Docket(DocumentationType.OAS_30)
//                 .apiInfo(new ApiInfoBuilder()
//                         .title("StudyHelper API")
//                         .description("高中生助学平台接口文档")
//                         .version("1.0")
//                         .build())
//                 .select()
//                 .apis(RequestHandlerSelectors.basePackage("com.studyhelper.controller"))
//                 .paths(PathSelectors.any())
//                 .build();
//     }
// }
