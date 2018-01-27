package com.android.parii.simplycityserverapp.Model;

import java.util.List;

/**
 * Created by parii on 1/21/18.
 */

public class RestrauntMenu {

    String menueName;
    String imageURL;
    String menueID;

    List<Food> foodList;

    public RestrauntMenu(){}

    public RestrauntMenu(String menueName ,String imageURL, String menueID){
        this.menueName = menueName;
        this.imageURL = imageURL;
        this.menueID = menueID;
    }

    public void setMenueName(String menueName){
        this.menueName = menueName;
    }

    public String getMenueName(){
        return this.menueName;
    }

    public void setImageURL(String imageURL){
        this.imageURL = imageURL;
    }

    public String getImageURL(){
        return this.imageURL;
    }

    public void setMenueID(String menueID){
        this.menueID = menueID;
    }

    public String getMenueID(){
        return this.menueID;
    }

    public void setFoodList(List<Food> foodList){
        this.foodList = foodList;
    }

    public List<Food> getFoodList(){
        return this.foodList;
    }
}
