package ca.bc.gov.educ.api.gradstatus.model.entity;

import java.sql.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "GRAD_STUDENT")
public class GraduationStatusEntity extends BaseEntity {

    @Id
    @Column(name = "pen", nullable = false)
    private String pen;

    @Lob
    @Column(name = "STUDENT_GRAD_DATA", columnDefinition="CLOB")
    private String studentGradData;

    @Column(name = "FK_GRAD_PROGRAM_CODE", nullable = true)
    private String program;
    
    @Column(name = "PROGRAM_COMPLETION_DT", nullable = true)
    private Date programCompletionDate; 
    
    @Column(name = "GPA", nullable = true)
    private String gpa;
    
    @Column(name = "HONOURS_STANDING", nullable = true)
    private String honoursStanding;        
    
    @Column(name = "RECALCULATE_GRAD_STATUS", nullable = true)
    private String recalculateGradStatus;
    
    @Column(name = "SCHOOL_OF_RECORD", nullable = true)
    private String schoolOfRecord;
    
    @Column(name = "STUD_GRADE", nullable = true)
    private String studentGrade;
    
    @Column(name = "GRAD_STUDENT_STUDENT_STATUS_FK", nullable = false)
    private String studentStatus;
    
    @Column(name = "STUDENT_ID", nullable = false)
    private UUID studentID;
    
    @Column(name = "SCHOOL_AT_GRAD", nullable = true)
    private String schoolAtGrad;
    
    
    
}