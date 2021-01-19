package ca.bc.gov.educ.api.gradstatus.model.transformer;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ca.bc.gov.educ.api.gradstatus.model.dto.GraduationStatus;
import ca.bc.gov.educ.api.gradstatus.model.entity.GraduationStatusEntity;
import ca.bc.gov.educ.api.gradstatus.util.EducGradStatusApiUtils;


@Component
public class GraduationStatusTransformer {

    @Autowired
    ModelMapper modelMapper;

    public GraduationStatus transformToDTO (GraduationStatusEntity gradStatusEntity) {
    	GraduationStatus gradStatus = modelMapper.map(gradStatusEntity, GraduationStatus.class);
    	gradStatus.setProgramCompletionDate(EducGradStatusApiUtils.parseDateFromString(gradStatus.getProgramCompletionDate() != null ? gradStatus.getProgramCompletionDate().toString():null));
    	return gradStatus;
    }

    public GraduationStatus transformToDTO ( Optional<GraduationStatusEntity> gradStatusEntity ) {
    	GraduationStatusEntity cae = new GraduationStatusEntity();
        if (gradStatusEntity.isPresent())
            cae = gradStatusEntity.get();
        	
        GraduationStatus gradStatus = modelMapper.map(cae, GraduationStatus.class);
        gradStatus.setProgramCompletionDate(EducGradStatusApiUtils.parseTraxDate(gradStatus.getProgramCompletionDate() != null ? gradStatus.getProgramCompletionDate().toString():null));
        return gradStatus;
    }

	public List<GraduationStatus> transformToDTO (Iterable<GraduationStatusEntity> gradStatusEntities ) {
		List<GraduationStatus> gradStatusList = new ArrayList<GraduationStatus>();
        for (GraduationStatusEntity gradStatusEntity : gradStatusEntities) {
        	GraduationStatus gradStatus = new GraduationStatus();
        	gradStatus = modelMapper.map(gradStatusEntity, GraduationStatus.class);            
        	gradStatus.setProgramCompletionDate(EducGradStatusApiUtils.parseTraxDate(gradStatus.getProgramCompletionDate() != null ? gradStatus.getProgramCompletionDate().toString():null));
        	gradStatusList.add(gradStatus);
        }
        return gradStatusList;
    }

    public GraduationStatusEntity transformToEntity(GraduationStatus gradStatus) {
        GraduationStatusEntity gradStatusEntity = modelMapper.map(gradStatus, GraduationStatusEntity.class);
        gradStatusEntity.setProgramCompletionDate(gradStatus.getProgramCompletionDate() != null ?Date.valueOf(gradStatus.getProgramCompletionDate()) : null);
        return gradStatusEntity;
    }
}
