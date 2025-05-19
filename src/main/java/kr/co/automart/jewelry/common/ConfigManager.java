package kr.co.automart.jewelry.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Component
public class ConfigManager implements ApplicationListener<ApplicationStartedEvent> {
	
	@Value("${service.service_name}")
	private String serviceName;
	
	@Override
	public void onApplicationEvent(ApplicationStartedEvent event) {
		// TODO Auto-generated method stub
		//System.out.println(this.serviceName);
	}
	
	public String getServiceName() {
		return this.serviceName;
	}
	
	
}
