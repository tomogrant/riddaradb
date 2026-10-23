package com.se.riddaradb.motif.boberg;

public class BobergMotif {

    public BobergMotif(){

    }

    public BobergMotif(String motifCode, String motifName, String description, String parentCode) {
        this.motifCode = motifCode;
        this.motifName = motifName;
        this.description = description;
        this.parentCode = parentCode;
    }

    private String motifCode;
    private String motifName;
    private String description;
    private String parentCode;

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

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }
}
