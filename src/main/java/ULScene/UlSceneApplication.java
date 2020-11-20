package ULScene;

import ULScene.config.SwaggerConfiguartion;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import(SwaggerConfiguartion.class)
public class UlSceneApplication {

	public static void main(String[] args) {
		SpringApplication.run(UlSceneApplication.class, args);
	}

}
