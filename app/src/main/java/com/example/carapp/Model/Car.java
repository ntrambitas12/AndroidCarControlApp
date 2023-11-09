package com.example.carapp.Model;

import com.example.carapp.R;

public class Car {
    private String BTMacAddress;
    private String VIN;
    private String ColorHEX;
    private String NickName;
    private int imageResource;
    public Car(String BTMacAddress, String VIN) {
        this.BTMacAddress = BTMacAddress;
        this.VIN = VIN;
        imageResource = R.drawable.carplaceholder;
    }

    public void setColor(String ColorHEX) { this.ColorHEX = ColorHEX; }
    public void setNickName(String NickName) { this.NickName = NickName; }
    public void setImageResource (int imageResource) { this.imageResource = imageResource; }
    public String getVIN() { return VIN; }
    public String getBTMacAddress() { return BTMacAddress; }
    public String getColorHEX() { return ColorHEX; }
    public String getNickName() { return  NickName; }
    public int getImageResource() { return imageResource; }
}
