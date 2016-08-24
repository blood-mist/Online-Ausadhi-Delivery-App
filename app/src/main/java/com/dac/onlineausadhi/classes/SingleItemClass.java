package com.dac.onlineausadhi.classes;

/**
 * Created by blood-mist on 6/30/16.
 */
public class SingleItemClass {
    private String name;
    private String url;
    private String description;

    public SingleItemClass() {
    }

    public SingleItemClass(String name, String url) {
        this.name = name;
        this.url = url;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
