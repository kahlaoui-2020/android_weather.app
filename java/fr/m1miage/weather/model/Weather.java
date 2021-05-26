package fr.m1miage.weather.model;

public class Weather {


    private String id;
    private String main;
    private String description;
    private String icon;

    public String getTemp() {
        return temp;
    }

    private String temp;
    public Weather(String id, String temp, String description, String icon) {
        this.id = id;
        this.description = description;
        this.icon = icon;
        this.temp = temp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
}
