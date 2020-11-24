package ca.bc.gov.educ.api.gradstatus.model.dto;

import java.sql.Date;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class GraduationStatus {

	public GraduationStatus() {
        studentGradData = new StringBuffer();
    }

    private StringBuffer studentGradData;
    private String pen;
    private String gradProgram;
    private Date graduationDate; 
    private Date sccpGraduationDate;
    private String schoolAtGrad;
    private String gradProgramAtGraduation;
    private String studentGradeAtGraduation;
    private String gpa;
    private String honoursFlag;
    private String certificateType1;
    private String certificateType2;
    private Date certificateType1Date; 
    private Date certificateType2Date; 
    private String frenchProgramParticipation;
    private String advancePlacementParticipation;    
    private String careerProgramParticipation;
    private String recalculateFlag;
    private String dualDogwoodEligibility;
    private String ibParticipationFlag;    
    private Date transcriptDate;
    private Date lastUpdatedDate;    
    private String createdBy;
    private Date createdTimestamp;
    private String updatedBy;
    private Date updatedTimestamp;
	
				
}
