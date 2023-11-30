package com.example.carapp.Model;

import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.carapp.R;

import java.io.Serializable;

public class Car implements Serializable {
    private String BTMacAddress;
    private String VIN;

    private String image;
    private String ColorHEX;
    private String NickName;
    private int imageResource;
    public Car(String BTMacAddress, String VIN) {
        this.BTMacAddress = BTMacAddress;
        this.VIN = VIN;
        imageResource = R.drawable.carplaceholder;
        image = "";
    }

    public void setColor(String ColorHEX) { this.ColorHEX = ColorHEX; }
    public void setNickName(String NickName) { this.NickName = NickName; }

    public void setImage(String imageLink) { this.image = imageLink;}
    public void setImageResource (int imageResource) { this.imageResource = imageResource; }
    public String getVIN() { return VIN; }
    public String getBTMacAddress() { return BTMacAddress; }
    public String getColorHEX() { return ColorHEX; }
    public String getNickName() { return  NickName; }
    public String getImageResource() {
        if(image.equals("") || image == null) {
            return  "" + imageResource;
        }
        else {
            return image;
        }
    }
}
