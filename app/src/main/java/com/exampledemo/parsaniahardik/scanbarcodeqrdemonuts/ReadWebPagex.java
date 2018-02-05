package com.exampledemo.parsaniahardik.scanbarcodeqrdemonuts;

/**
 * Created by XTO on 29/01/2018.
 */

import java.io.IOException;
import java.net.MalformedURLException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ReadWebPagex
{
    public static List<String> readURL(List<String> resultList,String strURL) throws MalformedURLException, IOException
    {
        BufferedReader br = null;
        System.out.println("Reading website: " + strURL);
        try
        {
            URL url = new URL(strURL);
            br = new BufferedReader(new InputStreamReader(url.openStream()));

            String line;

            while ((line = br.readLine()) != null)
            {
                resultList.add(line);
            }
            System.out.println(line);
        }
        finally
        {
            if (br != null)
            {
                br.close();
            }
        }
        return  resultList;
    }
}