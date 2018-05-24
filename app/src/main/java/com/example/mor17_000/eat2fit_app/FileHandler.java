package com.example.mor17_000.eat2fit_app;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by mor17_000 on 20-May-18.
 */

public class FileHandler {
    String filename = "Eat2Fit_UnratedDishes";



    public Boolean writeToFile(Context context, String fileContents){
        try {
            fileContents = fileContents +"\n";
            FileOutputStream outputStream = context.getApplicationContext().openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(fileContents.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public String readFromFile(Context context) throws FileNotFoundException {
        FileInputStream inputStream = context.getApplicationContext().openFileInput(filename);
        int content;
        String file = "";
        try {
            while ((content = inputStream.read()) != -1) {
                // convert to char and display it
                file = file + ((char) content);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
    }
}
