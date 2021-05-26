package fr.m1miage.weather.model;

import java.util.List;

public class WeatherForecastResult {


    private String cod;
    private Integer message;
    private Integer cnt;
    private java.util.List<WeatherResult> list = null;
    private City city;

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public List<WeatherResult> getList() {
        return list;
    }

    public void setList(List<WeatherResult> list) {
        this.list = list;
    }

    public Integer getMessage() {
        return message;
    }

    public void setMessage(Integer message) {
        this.message = message;
    }
}
