package com.example.myapplication.Model1;

import java.util.ArrayList;

public class WeatherData {

    public   Data value;

    public static class Data {
        public String id;
        public String name;
        public Main main;
        public Coord coord;
        public Wind wind;
        public Sys sys;
        public ArrayList<Weather> weather;
    }
}
