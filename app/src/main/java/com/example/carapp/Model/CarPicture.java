package com.example.carapp.Model;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarPicture {
    private String make;
    private String model;
    private String year;
    private boolean isCurrentYear;
    //current year for car maufacturing
    private final int CURRENT_YEAR = 2024;

    private String baseWebsite = "https://www.thecarconnection.com/";
    private String extension;

    public String carPictureLink;
    public CarPicture(String make, String model, String year)
    {
        //init car attributes
        this.make = make.toLowerCase();
        this.model = model.toLowerCase();
        this.year = year;

        //check if year is current year
        if(Integer.parseInt(year) == CURRENT_YEAR)
        {
            this.extension = "cars/";
            this.isCurrentYear = true;
        }
        else
        {
            this.extension = "overview/";
            this.isCurrentYear = false;
        }

        this.carPictureLink = "";
    }

    public void FindCarPicture()
    {

        //executes a task in the background to get link to car picture
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //get link to website (carconnection.com)
            String link = GetCarPageLink();
            String picLink;

            //connect to website
            try {
                // fetching the target website
                Document doc;
                doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                        .header("Accept-Language", "*") .get();

                Log.i("CarPictureClass","Connected to Car picture Website (carconnection.com)...");

                //get car picture link for glide
                picLink = FindCarPictureInHTML(doc, -1);
            } catch (IOException e) {
                //if couldnt get website try to use repo for picture
                //otherwise use default picture
                Log.e("CarPictureClass", e.toString());
                picLink = "";
            }
            if(picLink == "")
            {
                Log.i("CarPictureClass","Could not find picture of car");
            }
            this.carPictureLink = picLink;

        });
    }

    private String GetCarPageLink()
    {
        String carPageLink = baseWebsite + extension + make + "_" + model;
        if(!isCurrentYear)
        {
            carPageLink += "_" + year;
        }
        return carPageLink;
    }

    private String FindCarPictureInHTML(Document doc, int modifierIndex)
    {
        String[] modifiers = {"h", "l", "m","s","t"};
        String link = "";

        //gets list of image elements in html
        Elements pictureElements = doc.select("img");

        //Log.i("CarPictureClass", pictureElements.toString());

        /*//tries to get images of larger size first if available for first image found
        for(int i = 0; i < modifiers.length; i++) {
            try {
                link = pictureElements.get(0).attr("data-image-" + modifiers[i]);
            }
            catch (Exception e)
            {
                continue;
            }
        }*/

        //gets picture link
        link = pictureElements.get(0).attr("src");

        //strips off arbitrary size modifier if specified
        if(modifierIndex > -1) {
            link = link.substring(0, link.length() - 6);

            //puts on set size modifier
            String fileType = ".jpg";
            if (modifierIndex == modifiers.length - 1) {
                fileType = ".gif";
            }
            link += "_" + modifiers[modifierIndex] + fileType;
        }

        Log.i("CarPictureClass", "picture link scraped: " + link);
        return link;
    }


}
