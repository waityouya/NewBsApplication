package com.example.myapplication.model;

public class Page extends UserToken{
    int page;
    int auditType;
    public Page(int page,String userId,String token){
        super(userId, token);
        this.page = page;

    }

    public Page(int page,String userId,String token,int auditType){
        super(userId, token);
        this.page = page;
        this.auditType = auditType;

    }
    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getAuditType() {
        return auditType;
    }

    public void setAuditType(int auditType) {
        this.auditType = auditType;
    }

}
