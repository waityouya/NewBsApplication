package com.example.myapplication.model;

public class AuxiliaryParams extends UserToken {
    int auxiliaryId;
    public AuxiliaryParams(int auxiliaryId,String userId,String token) {

        super(userId, token);
        this.auxiliaryId = auxiliaryId;
    }
}
