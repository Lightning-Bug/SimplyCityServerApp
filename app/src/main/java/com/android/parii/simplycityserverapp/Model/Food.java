package com.android.parii.simplycityserverapp.Model;

/**
 * Created by parii on 1/14/18.
 */

public class Food {


    private String Name,Image,Description,Price,Discount,MenuId;

    public Food(){

    }

    public Food(String name, String image, String description, String price, String discount, String menuId) {
        Description = description;
        Discount = discount;
        Image = image;
        MenuId = menuId;
        Name = name;
        Price = price;

    }

    public void setName(String name) {
        Name = name;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getName() {
        return Name;
    }

    public String getImage() {
        return Image;
    }

    public String getDescription() {
        return Description;
    }

    public String getPrice() {
        return Price;
    }

    public String getDiscount() {
        return Discount;
    }

    public String getMenuId() {
        return MenuId;
    }




}
