package ca.bc.gov.educ.api.gradstatus.model.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class GradUngradReasons extends BaseModel {

	private String code;	
	private String description;
}
