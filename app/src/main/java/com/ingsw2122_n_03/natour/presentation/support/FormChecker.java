package com.ingsw2122_n_03.natour.presentation.support;

import android.util.Patterns;

public class FormChecker {


    public boolean isUsernameValid(String username) {
        return username.length() >= 4 && username.length() <= 20 && !username.matches("\\s+.*");
    }


    public boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public boolean isPasswordValid(String password) {

        if(password.length() < 8 || password.length() > 20)
            return false;

        if(password.matches(".*\\s+.*"))
            return false;

        return password.matches(".*(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).*");

    }

}
