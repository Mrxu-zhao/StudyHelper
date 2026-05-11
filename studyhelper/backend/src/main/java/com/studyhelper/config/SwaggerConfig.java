package com.studyhelper.config;

import org.springframework.context.annotation.Configuration;

/**
 * Swagger API文档配置（可选）
 * 如果需要Swagger文档，添加springdoc-openapi依赖并取消注释
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
