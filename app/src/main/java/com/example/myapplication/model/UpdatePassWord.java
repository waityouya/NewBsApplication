package com.example.myapplication.model;

public class UpdatePassWord extends UserToken{
    private String beforePassWord;
    private String newPassWord;

    public UpdatePassWord(String beforePassWord,String newPassWord,String userId,String token){
        super(userId,token);
        this.beforePassWord = beforePassWord;
        this.newPassWord = newPassWord;
    }

    public String getBeforePassWord() {
        return beforePassWord;
    }

    public void setBeforePassWord(String beforePassWord) {
        this.beforePassWord = beforePassWord;
    }

    public String getNewPassWord() {
        return newPassWord;
    }

    public void setNewPassWord(String newPassWord) {
        this.newPassWord = newPassWord;
    }
}
