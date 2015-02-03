package com.example.javier.MaterialDesignApp.RecyclerView.RecyclerViewClasses;


public class Design {

    String title;
    String excerpt;
    String image;

    public Design(String title, String excerpt, String image) {
        this.title = title;
        this.excerpt = excerpt;
        this.image = image;
    }
    public String getTitle() {
        return title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public String getImage() {
        return image;
    }
}
