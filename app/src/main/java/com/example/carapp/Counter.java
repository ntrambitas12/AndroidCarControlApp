package com.example.carapp;

import java.io.Serializable;

/* Sample counter class created to showcase object passing between fragments
*  This counter allows for counting numbers 0 - MAX_INT */
public class Counter implements Serializable {
    private int count;

    public Counter() {
        this.count = 0;
    }

    public void increment() {
        this.count++;
    }

    public void decrement() {
        if (this.count > 0) {
            this.count--;
        }
    }

    public int getCount() {
        return this.count;
    }
}
