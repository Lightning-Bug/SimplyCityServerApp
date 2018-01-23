package com.android.parii.simplycityserverapp.Model;

import java.util.List;

/**
 * Created by parii on 1/6/18.
 */

public class Restaurant {

    private String Name;
    private String resID;
    private String ImageURL;

    List<RestrauntMenu> restrauntMenuList;

    public Restaurant() {
    }

    public Restaurant(String name, String resID, String ImageURL,List<RestrauntMenu> RestrauntMenuList) {
        Name = name;
        this.resID = resID;
        this.ImageURL = ImageURL;
        this.restrauntMenuList = RestrauntMenuList;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImageURL() {
        return ImageURL;
    }

    public void setImageURL(String ImageURL) {
        this.ImageURL = ImageURL;
    }

    public String getResID(){
        return this.resID;
    }

    public void setResID(String resID){
        this.resID = resID;
    }

    public void setRestrauntMenuList(List<RestrauntMenu> RestrauntMenuList){
        this.restrauntMenuList = RestrauntMenuList;
    }

    public List<RestrauntMenu> getRestrauntMenuList(){
        return this.restrauntMenuList;
    }
}
