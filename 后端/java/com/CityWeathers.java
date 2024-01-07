package com;

public class CityWeathers{
    //星期几
    private String week;
    //日期
    private String date;
    //白天天气
    private String dayWeather;
    //白天风向
    private String dayWindDirection;
    //白天风力
    private String dayWindPower;
    //最高气温
    private String dayHigh;
    //最低气温
    private String dayLow;
    //黑夜天气
    private String nightWeather;
    //黑夜风向
    private String nightWindDirection;
    //黑夜风力
    private String nightWindPower;

    public CityWeathers(){}

    public CityWeathers(String week, String date, String dayWeather, String dayWindDirection, String dayWindPower, String dayHigh, String dayLow, String nightWeather, String nightWindDirection, String nightWindPower) {
        this.week = week;
        this.date = date;
        this.dayWeather = dayWeather;
        this.dayWindDirection = dayWindDirection;
        this.dayWindPower = dayWindPower;
        this.dayHigh = dayHigh;
        this.dayLow = dayLow;
        this.nightWeather = nightWeather;
        this.nightWindDirection = nightWindDirection;
        this.nightWindPower = nightWindPower;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDayWeather() {
        return dayWeather;
    }

    public void setDayWeather(String dayWeather) {
        this.dayWeather = dayWeather;
    }

    public String getDayWindDirection() {
        return dayWindDirection;
    }

    public void setDayWindDirection(String dayWindDirection) {
        this.dayWindDirection = dayWindDirection;
    }

    public String getDayWindPower() {
        return dayWindPower;
    }

    public void setDayWindPower(String dayWindPower) {
        this.dayWindPower = dayWindPower;
    }

    public String getDayHigh() {
        return dayHigh;
    }

    public void setDayHigh(String dayHigh) {
        this.dayHigh = dayHigh;
    }

    public String getDayLow() {
        return dayLow;
    }

    public void setDayLow(String dayLow) {
        this.dayLow = dayLow;
    }

    public String getNightWeather() {
        return nightWeather;
    }

    public void setNightWeather(String nightWeather) {
        this.nightWeather = nightWeather;
    }

    public String getNightWindDirection() {
        return nightWindDirection;
    }

    public void setNightWindDirection(String nightWindDirection) {
        this.nightWindDirection = nightWindDirection;
    }

    public String getNightWindPower() {
        return nightWindPower;
    }

    public void setNightWindPower(String nightWindPower) {
        this.nightWindPower = nightWindPower;
    }

    @Override
    public String toString() {
        return "com.CityWeathers{" +
                "week='" + week + '\'' +
                ", date='" + date + '\'' +
                ", dayWeather='" + dayWeather + '\'' +
                ", dayWindDirection='" + dayWindDirection + '\'' +
                ", dayWindPower='" + dayWindPower + '\'' +
                ", dayHigh='" + dayHigh + '\'' +
                ", dayLow='" + dayLow + '\'' +
                ", nightWeather='" + nightWeather + '\'' +
                ", nightWindDirection='" + nightWindDirection + '\'' +
                ", nightWindPower='" + nightWindPower + '\'' +
                '}';
    }
    public String toMessage(){
        return "'" + week + '\'' +
                ",'" + date + '\'' +
                ",'" + dayWeather + '\'' +
                ",'" + dayWindDirection + '\'' +
                ",'" + dayWindPower + '\'' +
                ",'" + dayHigh + '\'' +
                ",'" + dayLow + '\'' +
                ",'" + nightWeather + '\'' +
                ",'" + nightWindDirection + '\'' +
                ",'" + nightWindPower + '\''
                ;
    }
}
