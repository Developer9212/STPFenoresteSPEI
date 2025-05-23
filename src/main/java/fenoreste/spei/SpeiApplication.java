package fenoreste.spei;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.time.ZoneId;
import java.util.Date;

@SpringBootApplication
@ComponentScan({"fenoreste.spei.controller",
	            "fenoreste.spei.service",
	            "fenoreste.spei.util",
	            "fenoreste.spei.stp",
	            "fenoreste.spei.consumo",
	            "fenoreste.spei.security"})
@EntityScan("fenoreste.spei.entity")
@EnableJpaRepositories("fenoreste.spei.dao")
@EnableScheduling
@EnableAsync
public class SpeiApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpeiApplication.class, args);
		new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
	}

}
