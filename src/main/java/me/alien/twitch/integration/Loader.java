package me.alien.twitch.integration;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Loader {
    public static String leadFile(InputStream s) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(s));
            StringBuilder data = new StringBuilder();
            String tmp = "";
            while((tmp=in.readLine())!=null){
                data.append(tmp);
            }
            return data.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
