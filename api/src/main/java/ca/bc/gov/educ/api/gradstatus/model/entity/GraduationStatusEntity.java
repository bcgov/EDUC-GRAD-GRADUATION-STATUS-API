package ca.bc.gov.educ.api.gradstatus.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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

    @Column(name = "GRAD_PROGRAM", nullable = false)
    private String gradProgram;
    
    @Column(name = "GRAD_DT", nullable = false)
    private Date graduationDate; 
    
    @Column(name = "SCCP_COMPLETION_DT", nullable = false)
    private Date sccpGraduationDate;
    
    @Column(name = "SCHOOL_AT_GRAD", nullable = false)
    private String schoolAtGrad;
    
    @Column(name = "GRAD_PROGRAM_AT_GRAD", nullable = false)
    private String gradProgramAtGraduation;
    
    @Column(name = "STUD_GRADE_AT_GRAD", nullable = false)
    private String studentGradeAtGraduation;
    
    @Column(name = "GPA", nullable = false)
    private String gpa;
    
    @Column(name = "HONOURS_STANDING_FLG", nullable = false)
    private String honoursFlag;
    
    @Column(name = "CERTIFICATE_TYPE_1", nullable = false)
    private String certificateType1;
    
    @Column(name = "CERTIFICATE_TYPE_2", nullable = false)
    private String certificateType2;
    
    @Column(name = "CERTIFICATE_TYPE_1_DT", nullable = false)
    private Date certificateType1Date; 
    
    @Column(name = "CERTIFICATE_TYPE_2_DT", nullable = false)
    private Date certificateType2Date; 
    
    @Column(name = "FRN_PGM_PCP_FLG", nullable = false)
    private String frenchProgramParticipation;
    
    @Column(name = "ADV_PLMNT_PCP_FLG", nullable = false)
    private String advancePlacementParticipation;    
    
    @Column(name = "CAREER_PGM_PCP_FLG", nullable = false)
    private String careerProgramParticipation;
    
    @Column(name = "RECALCULATE_FLG", nullable = false)
    private String recalculateFlag;
    
    @Column(name = "DUAL_DOGWOOD_ELIG", nullable = false)
    private String dualDogwoodEligibility;
    
    @Column(name = "IB_PCP_IB_FLG", nullable = false)
    private String ibParticipationFlag;    
    
    @Column(name = "TRANSCRIPT_DT", nullable = false)
    private Date transcriptDate;
    
    @Column(name = "LAST_UPDATE_DT", nullable = false)
    private Date lastUpdatedDate;    

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_timestamp", nullable = false)
    private Date createdTimestamp;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Column(name = "updated_timestamp", nullable = false)
    private Date updatedTimestamp;

}