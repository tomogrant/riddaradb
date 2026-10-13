package com.se.riddaradb.motif;

import com.se.riddaradb.sagaversion.SagaVersionMotifDto;
import jakarta.validation.constraints.NotBlank;

import java.util.HashSet;
import java.util.Set;

public class MotifDto {

    private Integer id;
    @NotBlank
    private String motifCode;
    @NotBlank
    private String motifName;
    private String description;
    private Integer parentId;
    private Boolean hasChildren;
    private Set<MotifSagaVersionDto> motifSagas = new HashSet<>();

    public MotifDto(Integer id, String motifCode, String motifName, String description, Boolean hasChildren) {
        this.id = id;
        this.motifCode = motifCode;
        this.motifName = motifName;
        this.description = description;
        this.parentId = null;
        this.hasChildren = hasChildren;
        Set<SagaVersionMotifDto> sagaMotifs = new HashSet<>();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }


    public String getMotifCode() {
        return motifCode;
    }

    public void setMotifCode(String motifCode) {
        this.motifCode = motifCode;
    }

    public String getMotifName() {
        return motifName;
    }

    public void setMotifName(String motifName) {
        this.motifName = motifName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Set<MotifSagaVersionDto> getMotifSagas() {
        return motifSagas;
    }

    public void setMotifSagas(Set<MotifSagaVersionDto> motifSagas) {
        this.motifSagas = motifSagas;
    }
}
