package ca.bc.gov.educ.api.gradstatus.model.dto;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class GradStudentUngradReasons extends BaseModel {

	private UUID id;
	private String pen;
	private String ungradReasonCode;
	private String ungradReasonName;
	private UUID studentID;
}
