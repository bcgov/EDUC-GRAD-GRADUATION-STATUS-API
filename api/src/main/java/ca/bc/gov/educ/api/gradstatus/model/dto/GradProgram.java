package ca.bc.gov.educ.api.gradstatus.model.dto;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Component
public class GradProgram extends BaseModel {

	private String programCode; 
	private String programName; 
	private String programType;	
	
	@Override
	public String toString() {
		return "GradProgram [programCode=" + programCode + ", programName=" + programName + ", programType="
				+ programType + "]";
	}
	
	
			
}
