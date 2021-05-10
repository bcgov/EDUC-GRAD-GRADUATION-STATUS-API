package ca.bc.gov.educ.api.gradstatus.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ca.bc.gov.educ.api.gradstatus.util.MessageHelper;

@Configuration
@PropertySource(value = "classpath:messages.yaml", factory = YamlPropertySourceFactory.class)
public class GradGraduationStatusConfig implements WebMvcConfigurer {

	@Autowired
	RequestInterceptor requestInterceptor;
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(requestInterceptor);
	}

	@Bean
	public MessageHelper messageHelper() {
		return new MessageHelper();
	}

	
}
