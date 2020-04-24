package com.example.myapplication.model;

public class ReturnAuxiliryCase extends JsonRootBean {
    private AuxiliryCase data;

    public AuxiliryCase getData() {
        return data;
    }

    public void setData(AuxiliryCase data) {
        this.data = data;
    }
}
