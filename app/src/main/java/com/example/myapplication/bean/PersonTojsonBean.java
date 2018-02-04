package com.example.myapplication.bean;

/**
 * Created by CXK on 2018/2/4.
 */

public class PersonTojsonBean {

    public class data{}

    public String merNo;
    public String subMerNo;
    public String netway;
    public String amount;
    public String deviceCode;
    public String sign;

    public String getMerNo() {
        return merNo;
    }

    public void setMerNo(String merNo) {
        this.merNo = merNo;
    }

    public String getSubMerNo() {
        return subMerNo;
    }

    public void setSubMerNo(String subMerNo) {
        this.subMerNo = subMerNo;
    }

    public String getNetway() {
        return netway;
    }

    public void setNetway(String netway) {
        this.netway = netway;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
