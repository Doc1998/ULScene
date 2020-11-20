package ULScene.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguartion {
    @Bean
    public Docket ulSceneApi(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
                .build()
                .apiInfo(getApiInfo());

        //docket = brief statement of the contents of the document
    }
    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("ULScene FYP Api")
                .version("0.1")
                .description("API for ULScene FYP application")
                .contact(new Contact("DOC","https://www.youtube.com/watch?v=dQw4w9WgXcQ&ab_channel=RickAstleyVEVO","17241499@studentmail.ul.ie"))
                .build();
    }

}