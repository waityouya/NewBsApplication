package com.example.myapplication.model;

import java.io.Serializable;
import java.util.Date;

public class Case extends JsonRootBean implements Serializable  {

        int caseId;
        String offPlateNumber;
        String offName;
        int offSex;
        String offBirthPlace;
        String offCertificateType;
        String offCertificateNumber;
        String offCertificateValid;
    String offTime;
        String offPlace;
        String offType;
        String offPunishmentType;
        int offMoney;
        int punishmentId;
        int auditorId;
        //token
        String appToken;
        String punishmentName;

        public Case(String offPlateNumber,String offName,int offSex,String offBirthPlace,
        String offCertificateType,String offCertificateNumber,String offCertificateValid,String offTime,
                    String offPlace,String offType,String offPunishmentType,int offMoney,
                    int punishmentId,int auditorId,String appToken){
            this.appToken = appToken;
            this.auditorId = auditorId;
            this.offBirthPlace = offBirthPlace;
            this.offCertificateNumber = offCertificateNumber;
            this.offCertificateType = offCertificateType;
            this.offCertificateValid = offCertificateValid;
            this.offMoney = offMoney;
            this.offName = offName;
            this.offPlace = offPlace;
            this.offPlateNumber = offPlateNumber;
            this.offType = offType;
            this.offPunishmentType = offPunishmentType;
            this.offSex = offSex;
            this.punishmentId = punishmentId;
            this.offTime = offTime;

        }

    public Case(String offPlateNumber,String offName,int offSex,String offBirthPlace,
                String offCertificateType,String offCertificateNumber,String offCertificateValid,String offTime,
                String offPlace,String offType,String offPunishmentType,
                int punishmentId,int auditorId,String appToken){
        this.appToken = appToken;
        this.auditorId = auditorId;
        this.offBirthPlace = offBirthPlace;
        this.offCertificateNumber = offCertificateNumber;
        this.offCertificateType = offCertificateType;
        this.offCertificateValid = offCertificateValid;

        this.offName = offName;
        this.offPlace = offPlace;
        this.offPlateNumber = offPlateNumber;
        this.offType = offType;
        this.offPunishmentType = offPunishmentType;
        this.offSex = offSex;
        this.punishmentId = punishmentId;
        this.offTime = offTime;

    }

        public Case(){

        }

        public void setAppToken(String appToken) {
            this.appToken = appToken;
        }

        public String getAppToken() {
            return appToken;
        }

        public int getAuditorId() {
            return auditorId;
        }

        public int getCaseId() {
            return caseId;
        }

        public String getOffBirthPlace() {
            return offBirthPlace;
        }

        public String getOffCertificateNumber() {
            return offCertificateNumber;
        }

        public String getOffCertificateType() {
            return offCertificateType;
        }

        public String getOffCertificateValid() {
            return offCertificateValid;
        }

        public int getOffMoney() {
            return offMoney;
        }

        public String getOffName() {
            return offName;
        }

        public String getOffPlace() {
            return offPlace;
        }

        public String getOffPlateNumber() {
            return offPlateNumber;
        }

        public int getOffSex() {
            return offSex;
        }

        public String getOffTime() {
            return offTime;
        }

        public String getOffType() {
            return offType;
        }

        public int getPunishmentId() {
            return punishmentId;
        }

        public String getOffPunishmentType() {
            return offPunishmentType;
        }

        public void setAuditorId(int auditorId) {
            this.auditorId = auditorId;
        }

        public void setCaseId(int caseId) {
            this.caseId = caseId;
        }

        public void setOffPunishmentType(String offPunishmentType) {
            this.offPunishmentType = offPunishmentType;
        }

        public void setOffBirthPlace(String offBirthPlace) {
            this.offBirthPlace = offBirthPlace;
        }

        public void setOffCertificateNumber(String offCertificateNumber) {
            this.offCertificateNumber = offCertificateNumber;
        }

        public void setOffCertificateType(String offCertificateType) {
            this.offCertificateType = offCertificateType;
        }

        public void setOffCertificateValid(String offCertificateValid) {
            this.offCertificateValid = offCertificateValid;
        }

        public void setOffMoney(int offMoney) {
            this.offMoney = offMoney;
        }

        public void setOffName(String offName) {
            this.offName = offName;
        }

        public void setOffPlace(String offPlace) {
            this.offPlace = offPlace;
        }

        public void setOffPlateNumber(String offPlateNumber) {
            this.offPlateNumber = offPlateNumber;
        }

        public void setOffSex(int offSex) {
            this.offSex = offSex;
        }

        public void setOffTime(String offTime) {
            this.offTime = offTime;
        }

        public void setOffType(String offType) {
            this.offType = offType;
        }

        public void setPunishmentId(int punishmentId) {
            this.punishmentId = punishmentId;
        }

    public String getPunishmentName() {
        return punishmentName;
    }

    public void setPunishmentName(String punishmentName) {
        this.punishmentName = punishmentName;
    }
}
