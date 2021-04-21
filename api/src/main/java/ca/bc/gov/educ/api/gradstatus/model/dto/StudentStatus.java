package ca.bc.gov.educ.api.gradstatus.model.dto;

import java.util.Date;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class StudentStatus {

	private String code; 
	private String description;	
	private String createdBy;
	private Date createdTimestamp;	
	private String updatedBy;	
	private Date updatedTimestamp;
}
