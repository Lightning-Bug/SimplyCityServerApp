package com.android.parii.simplycityserverapp.Common;

import com.android.parii.simplycityserverapp.Model.User;

/**
 * Created by parii on 1/2/18.
 */

public class Common {

    public static User currentUser;

    public static final String UPDATE = "Upadate";
    public static final String DELETE = "Delete";

    public static final int PICK_IMAGE_REQUEST=71;


    public static String convertCodeTOStatus(String code){

        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my Way";
        else
            return "Shipped";

    }
}
