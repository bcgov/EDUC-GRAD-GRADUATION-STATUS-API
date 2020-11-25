package ca.bc.gov.educ.api.gradstatus.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
@Entity
@Table(name = "GRAD_STUDENT_GRADUATION_STATUS")
public class GraduationStatusEntity {

    @Id
    @Column(name = "pen", nullable = false)
    private String pen;

    @Lob
    @Column(name = "STUDENT_GRAD_DATA", columnDefinition="CLOB")
    private String studentGradData;

    @Column(name = "GRAD_PROGRAM", nullable = true)
    private String gradProgram;
    
    @Column(name = "GRAD_DT", nullable = true)
    private Date graduationDate; 
    
    @Column(name = "SCCP_COMPLETION_DT", nullable = true)
    private Date sccpGraduationDate;
    
    @Column(name = "SCHOOL_AT_GRAD", nullable = true)
    private String schoolAtGrad;
    
    @Column(name = "GRAD_PROGRAM_AT_GRAD", nullable = true)
    private String gradProgramAtGraduation;
    
    @Column(name = "STUD_GRADE_AT_GRAD", nullable = true)
    private String studentGradeAtGraduation;
    
    @Column(name = "GPA", nullable = true)
    private String gpa;
    
    @Column(name = "HONOURS_STANDING_FLG", nullable = true)
    private String honoursFlag;
    
    @Column(name = "CERTIFICATE_TYPE_1", nullable = true)
    private String certificateType1;
    
    @Column(name = "CERTIFICATE_TYPE_2", nullable = true)
    private String certificateType2;
    
    @Column(name = "CERTIFICATE_TYPE_1_DT", nullable = true)
    private Date certificateType1Date; 
    
    @Column(name = "CERTIFICATE_TYPE_2_DT", nullable = true)
    private Date certificateType2Date; 
    
    @Column(name = "FRN_PGM_PCP_FLG", nullable = true)
    private String frenchProgramParticipation;
    
    @Column(name = "ADV_PLMNT_PCP_FLG", nullable = true)
    private String advancePlacementParticipation;    
    
    @Column(name = "CAREER_PGM_PCP_FLG", nullable = true)
    private String careerProgramParticipation;
    
    @Column(name = "RECALCULATE_FLG", nullable = true)
    private String recalculateFlag;
    
    @Column(name = "DUAL_DOGWOOD_ELIG", nullable = true)
    private String dualDogwoodEligibility;
    
    @Column(name = "IB_PCP_IB_FLG", nullable = true)
    private String ibParticipationFlag;    
    
    @Column(name = "TRANSCRIPT_DT", nullable = true)
    private Date transcriptDate;
    
    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_timestamp", nullable = false)
    private Date createdTimestamp;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "updated_timestamp", nullable = false)
    private Date updatedTimestamp;
    
    @PrePersist
	protected void onCreate() {
		//TODO: RLO . is this the best place to do this?
		this.updatedBy = "GRADUATION";
		this.createdBy = "GRADUATION";
		this.createdTimestamp = new Date();
		this.updatedTimestamp = new Date();

	}

	@PreUpdate
	protected void onPersist() {
		this.updatedTimestamp = new Date();
		this.updatedBy = "GRADUATION";
		if (StringUtils.isBlank(createdBy)) {
			createdBy = "GRADUATION";
		}
		if (createdTimestamp == null) {
			createdTimestamp = new Date();
		}
	}

}