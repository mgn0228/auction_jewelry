package kr.co.automart.jewelry;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
//import org.springframework.cache.annotation.EnableCaching;
import org.springframework.boot.builder.SpringApplicationBuilder;

//@EnableCaching
@SpringBootApplication
//public class ServicePlatformApplication //extends SpringBootServletInitializer {
public class ServicePlatformApplication extends SpringBootServletInitializer {

	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(ServicePlatformApplication.class);
//		return builder.sources(ServicePlatformApplication.class);
	}
	
	
	
	public static void main(String[] args) {
		SpringApplication.run(ServicePlatformApplication.class, args);
	}

}


