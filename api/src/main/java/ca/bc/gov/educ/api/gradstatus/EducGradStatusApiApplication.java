package ca.bc.gov.educ.api.gradstatus;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import reactor.netty.http.client.HttpClient;

@SpringBootApplication
public class EducGradStatusApiApplication {

	private static Logger logger = LoggerFactory.getLogger(EducGradStatusApiApplication.class);

	public static void main(String[] args) {
		logger.debug("########Starting API");
		SpringApplication.run(EducGradStatusApiApplication.class, args);
		logger.debug("########Started API");
	}

	@Bean
	public ModelMapper modelMapper() {

		ModelMapper modelMapper = new ModelMapper();
		modelMapper.typeMap(GraduationStatusEntity.class, GraduationStatus.class);
		modelMapper.typeMap(GraduationStatus.class, GraduationStatusEntity.class);
		return modelMapper;
	}
	
	@Bean
	public WebClient webClient() {
		HttpClient client = HttpClient.create();
		client.warmup().block();
		return WebClient.builder().build();
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
	
	@Configuration
	static
	class WebSecurityConfiguration extends WebSecurityConfigurerAdapter {
	  /**
	   * Instantiates a new Web security configuration.
	   * This makes sure that security context is propagated to async threads as well.
	   */
	  public WebSecurityConfiguration() {
	    super();
	    SecurityContextHolder.setStrategyName(SecurityContextHolder.MODE_INHERITABLETHREADLOCAL);
	  }
	  @Override
	  public void configure(WebSecurity web) {
	    web.ignoring().antMatchers("/v3/api-docs/**",
	            "/actuator/health","/actuator/prometheus",
	            "/swagger-ui/**", "/health");
	  }
	}
}