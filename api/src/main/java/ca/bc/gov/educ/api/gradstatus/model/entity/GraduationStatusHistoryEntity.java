package ca.bc.gov.educ.api.gradstatus.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "GRAD_STUDENT_HISTORY")
public class GraduationStatusHistoryEntity extends BaseEntity {

    @Id
    @Column(name = "ID", nullable = false)
    private UUID historyID;

    @Column(name = "STUDENT_ID", nullable = false)
    private UUID studentID;

    @Column(name = "pen", nullable = false)
    private String pen;

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
    
    @Column(name = "SCHOOL_AT_GRAD", nullable = true)
    private String schoolAtGrad;
    
}