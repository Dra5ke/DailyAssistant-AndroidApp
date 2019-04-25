package com.example.dailyassistant;

public class Plan {

    private String title;
    private String description;
    private int Year;
    private int Month;
    private int Day;

    public Plan(String title, String description, int year, int month, int day) {
        this.title = title;
        this.description = description;
        Year = year;
        Month = month;
        Day = day;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getYear() {
        return Year;
    }

    public void setYear(int year) {
        Year = year;
    }

    public int getMonth() {
        return Month;
    }

    public void setMonth(int month) {
        Month = month;
    }

    public int getDay() {
        return Day;
    }

    public void setDay(int day) {
        Day = day;
    }
}
