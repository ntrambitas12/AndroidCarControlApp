package com.example.carapp;

import org.junit.Test;

import static org.junit.Assert.*;

import com.example.carapp.Model.Car;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class CarTest {
    String VIN = "VINTEST123";
    String BTAddress = "12:12:12:12:12";
    @Test
    public void testCreatingCar() {
        Car car = new Car(BTAddress, VIN);
        // Check VIN is set
        assertEquals(VIN, car.getVIN());
        // Check BT is set
        assertEquals(BTAddress, car.getBTMacAddress());
    }

    @Test
    public void testSetColor() {
        String color = "#FFFFFF";
        Car car = new Car(BTAddress, VIN);
        car.setColor(color);
        // Check that color was set
        assertEquals(color, car.getColorHEX());
    }

    @Test
    public void testSetNickName() {
        String nickname = "TEST";
        Car car = new Car(BTAddress, VIN);
        car.setNickName(nickname);
        // Check nickname was set
        assertEquals(nickname,car.getNickName());
    }
}