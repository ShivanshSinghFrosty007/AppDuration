package com.example.appduration;

public class Model {

    public String Name;
    public String Time;
    public long pernetage;
    public Model(String name, String time) {
        Name = name;
        Time = time;
    }

    public Model(String name, String time, long pernetage) {
        Name = name;
        Time = time;
        this.pernetage = pernetage;
    }
}
