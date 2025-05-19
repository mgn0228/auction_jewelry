package com.nectarsoft.service_platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.automart.jewelry.ServicePlatformApplication;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(classes = ServicePlatformApplication.class)
class ServicePlatformApplicationTests {

	@Test
	void contextLoads() {
		String test = "김영미\n (salt0107)";
		log.info("테스트:::{}", test.substring(0, 3));
	}

}
