package ca.bc.gov.educ.api.gradstatus;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;

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
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}