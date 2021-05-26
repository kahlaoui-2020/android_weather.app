package fr.m1miage.weather.model;

public class City {
    private String id;
    private String name;
    private Coord coord;
    private String country;

    public String getCountry() {
        return country;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
