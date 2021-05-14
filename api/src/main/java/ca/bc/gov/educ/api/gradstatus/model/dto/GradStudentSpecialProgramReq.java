package ca.bc.gov.educ.api.gradstatus.model.dto;

import java.util.UUID;

import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Component
public class GradStudentSpecialProgramReq extends BaseModel{

	private UUID id;
    private String pen;
    private String specialProgramCompletionDate;
    private String specialProgramCode;
    private String mainProgramCode;
    private UUID studentID;
				
}
