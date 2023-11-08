package com.example.carapp.Model;

public class Car {
    private String BTMacAddress;
    private String VIN;
    private String ColorHEX;
    private String NickName;
    public Car(String BTMacAddress, String VIN) {
        this.BTMacAddress = BTMacAddress;
        this.VIN = VIN;
    }

    public void setColor(String ColorHEX) { this.ColorHEX = ColorHEX; }
    public void setNickName(String NickName) { this.NickName = NickName; }
    public String getVIN() { return VIN; }
    public String getBTMacAddress() { return BTMacAddress; }
    public String getColorHEX() { return ColorHEX; }
    public String getNickName() { return  NickName; }
}
