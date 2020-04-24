package com.example.myapplication.model;

public class AuxiliryCase extends Case {
    int auxiliaryCaseId;
    String audit;
    int isAudit;
    int auditType;
    String images;

    public int getAuxiliaryCaseId() {
        return auxiliaryCaseId;
    }


    public String getAudit() {
        return audit;
    }

    public int getIsAudit() {
        return isAudit;
    }

    public int getAuditType() {
        return this.auditType;
    }

    public void setAuxiliaryCaseId(int auxiliaryCaseId) {
        this.auxiliaryCaseId = auxiliaryCaseId;
    }

    public void setAudit(String audit) {
        this.audit = audit;
    }
    public void setIsAudit(int isAudit) {
        this.isAudit = isAudit;
    }

    public void setAuditType(int auditType) {
        this.auditType = auditType;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }
}
