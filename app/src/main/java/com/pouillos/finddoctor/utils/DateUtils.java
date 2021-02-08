package com.pouillos.finddoctor.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DateUtils {

    public static String ecrireDateLettre(Date date) {
        Map<Integer, String> mapJours = new HashMap<>();
        mapJours.put(1,"Dimanche");
        mapJours.put(2,"Lundi");
        mapJours.put(3,"Mardi");
        mapJours.put(4,"Mercredi");
        mapJours.put(5,"Jeudi");
        mapJours.put(6,"Vendredi");
        mapJours.put(7,"Samedi");

        Map<Integer, String> mapMois = new HashMap<>();
        mapMois.put(0,"Janvier");
        mapMois.put(1,"Fevrier");
        mapMois.put(2,"Mars");
        mapMois.put(3,"Avril");
        mapMois.put(4,"Mai");
        mapMois.put(5,"Juin");
        mapMois.put(6,"Juillet");
        mapMois.put(7,"Aout");
        mapMois.put(8,"Septembre");
        mapMois.put(9,"Octobre");
        mapMois.put(10,"Novembre");
        mapMois.put(11,"Decembre");

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);


        String dateMaj = "";
        dateMaj += mapJours.get(dayOfWeek)+" ";
        dateMaj += day+" ";
        dateMaj += mapMois.get(month)+" ";
        dateMaj += date.getYear()+1900;

        return dateMaj;
    }

    public static String ecrireDateHeure(Date date) {
        String pattern = "dd/MM/yyyy HH:mm";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        String dateMaj = simpleDateFormat.format(date);
        return dateMaj;
    }
}
