package com.example.dahye.wlb;

public class CategoryItem {
    private String category;
    private String score;

    public CategoryItem() {

    }

    public CategoryItem(String category, String score) {
        this.category = category;
        this.score = score;
    }

    public String getCategory() {
        return category;
    }

    public String getScore() {
        return score;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
