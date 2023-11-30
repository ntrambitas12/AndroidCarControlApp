package com.example.carapp.Model;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Currency;
import java.util.HashMap;

import androidx.core.os.HandlerCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CarPicture {
    private String make;
    private String model;
    private String year;

    private String VIN;

    private String uid;

    private FirebaseRepository repository;

    private MutableLiveData<Boolean> didImageChange;
    private boolean isCurrentYear;
    //current year for car maufacturing
    private final int CURRENT_YEAR = 2024;

    private final String baseWebsite = "https://www.thecarconnection.com/";

    private final String baseVinAPIWebsite = "https://vpic.nhtsa.dot.gov/api/";
    private final String decodeVinService = "vehicles/DecodeVin/";
    private String extension;

    public String carPictureLink;
    public CarPicture(String make, String model, String year)
    {
        this.UpdateParamsWithMakeModelYear(make,model,year);

        this.uid = null;
    }

    public CarPicture(FirebaseRepository repo, String uid, String VIN)
    {
        this.uid = uid;
        this.repository = repo;
        this.VIN = VIN;

        didImageChange = new MutableLiveData<>();
        didImageChange.setValue(false);

        didImageChange.observeForever(new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean imageChanged) {
                Log.i("CarPictureClass","image changed");
                if (imageChanged.booleanValue()) {
                    repository.updateCarImage(uid, carPictureLink, VIN);
                    didImageChange.removeObserver(this);
                }
            }
        });
    }

    private void UpdateParamsWithMakeModelYear (String make, String model, String year)
    {
        //init car attributes
        this.make = make.toLowerCase();
        this.model = model.toLowerCase();
        if(year == "") {
            year = "" + CURRENT_YEAR;
        }
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

    private void GetCarSpecsFromVIN()
    {
        //connect to website
        try {
            // fetching the target website
            String link = "";
            if(VIN.length() < 17)
            {
                link = baseVinAPIWebsite + decodeVinService + VIN + "*?";
            }
            else {
                link = baseVinAPIWebsite + decodeVinService + VIN + "?";
            }
            Document doc;
            doc = Jsoup.connect(link).userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/108.0.0.0 Safari/537.36")
                    .header("Accept-Language", "*") .get();

            Log.i("CarPictureClass","Connected to Car Vin lookup API");

            //get car picture link for glide
            FindMakeModelYearInHTML(doc);
        } catch (IOException e) {
            //coundnt connect to website for lookup, maybe just use default values and say to database it needs to look up next time??
            Log.e("CarPictureClass", e.toString());
        }
    }

    private void FindMakeModelYearInHTML(Document doc)
    {
        Elements carElements = doc.select("DecodedVariable");
        String make = "", model = "", year = "";
        for(Element e : carElements)
        {
            switch(e.child(0).text())
            {
                case "26":
                {
                    //Make
                    Elements value = e.select("Value");
                    make = value.get(0).text();
                    break;
                }
                case "28":
                {
                    //Model
                    Elements value = e.select("Value");
                    model = value.get(0).text();
                    break;
                }
                case "29":
                {
                    //Year
                    Elements value = e.select("Value");
                    if(value.size() < 1)
                    {
                        year = "" + CURRENT_YEAR;
                    }
                    else
                    {
                        year = value.get(0).text();
                    }
                    break;
                }
            }
        }
        Log.i("CarPictureClass","Make, Model, and Year scraped from api");
        Log.i("CarPictureClass","Make: " + make + " | Model: " + model + " | Year: " + year);
        UpdateParamsWithMakeModelYear(make, model, year);
    }

    public void FindCarPicture()
    {
        Handler mainThreadHandler = HandlerCompat.createAsync(Looper.getMainLooper());
        //executes a task in the background to get link to car picture
        ExecutorService executor = Executors.newSingleThreadExecutor();

        executor.execute(() -> {
            //checks if Make, Model, and Year need to be looked up with the api call
            if(VIN != null && (this.make == null || this.model == null || this.year == null))
            {
                GetCarSpecsFromVIN();
            }
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

            //if this object was initialized with the second constructor (i.e. for the database)
            //then update the database
            if(repository != null && uid != null)
            {
                //didImageChange.setValue(true);
                mainThreadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        repository.updateCarImage(uid, carPictureLink, VIN);
                    }
                });
            }

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
