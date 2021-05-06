package ca.bc.gov.educ.api.gradstatus.model.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.StringUtils;

import lombok.Data;

@Data
@MappedSuperclass
public class BaseEntity {

	@Column(name = "created_by", nullable = false)
    private String createdBy;

	@Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_timestamp", nullable = false)
    private Date createdTimestamp;

    @Column(name = "updated_by", nullable = false)
    private String updatedBy;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_timestamp", nullable = false)
    private Date updatedTimestamp;
    
    @PrePersist
	protected void onCreate() {
		if (StringUtils.isBlank(createdBy)) {
			this.createdBy = "GRADUATION";
		}		
		if (StringUtils.isBlank(updatedBy)) {
			this.updatedBy = "GRADUATION";
		}		
		this.createdTimestamp = new Date(System.currentTimeMillis());
		this.updatedTimestamp = new Date(System.currentTimeMillis());

	}

	@PreUpdate
	protected void onPersist() {
		this.updatedTimestamp = new Date(System.currentTimeMillis());
		if (StringUtils.isBlank(updatedBy)) {
			this.updatedBy = "GRADUATION";
		}
		if (StringUtils.isBlank(createdBy)) {
			this.createdBy = "GRADUATION";
		}
		if (this.createdTimestamp == null) {
			this.createdTimestamp = new Date(System.currentTimeMillis());
		}
	}
}
