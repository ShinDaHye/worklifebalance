package com.example.dahye.wlb;

public class CategoryItem {
    private String category;
    private String score;
    private String unit;

    public CategoryItem() {

    }

    public CategoryItem(String category, String score) {
        this.category = category;
        this.score = score;
    }

    public CategoryItem(String category, String score, String unit) {
        this.category = category;
        this.score = score;
        this.unit = unit;
    }

    public String getCategory() {
        return category;
    }

    public String getScore() {
        return score;
    }

    public String getUnit() {
        return unit;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
