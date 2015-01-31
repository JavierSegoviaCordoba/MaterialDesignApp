package com.example.javier.MaterialDesignApp.RecyclerViewClasses;


public class News {

    String title;
    String content;
    String image;

    public News(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }
    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }
}
