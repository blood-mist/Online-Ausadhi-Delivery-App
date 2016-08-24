package com.dac.onlineausadhi.classes;

import java.util.ArrayList;

/**
 * Created by blood-mist on 6/30/16.
 */
public class SectionDataClass {
    private String headerTitle;
    private ArrayList<SingleItemClass> allItemsInSection;


    public SectionDataClass() {

    }
    public SectionDataClass(String headerTitle, ArrayList<SingleItemClass> allItemsInSection) {
        this.headerTitle = headerTitle;
        this.allItemsInSection = allItemsInSection;
    }



    public String getHeaderTitle() {
        return headerTitle;
    }

    public void setHeaderTitle(String headerTitle) {
        this.headerTitle = headerTitle;
    }

    public ArrayList<SingleItemClass> getAllItemsInSection() {
        return allItemsInSection;
    }

    public void setAllItemsInSection(ArrayList<SingleItemClass> allItemsInSection) {
        this.allItemsInSection = allItemsInSection;
    }
}
