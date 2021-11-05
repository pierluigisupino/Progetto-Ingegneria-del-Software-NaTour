package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.util.Log;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ingsw2122_n_03.natour.infastructure.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.LoginActivity;
import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.RegisterActivity;
import com.ingsw2122_n_03.natour.presentation.VerifyAccount;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.HashMap;
import java.util.Objects;

public class AuthController extends Controller {

    private static AuthController instance = null;
    private AuthInterface authInterface;
    private BaseActivity callingActivity;

    private AuthController() {
        authInterface = new AmplifyAuthImplementation();
    }

    public static AuthController getInstance() {
        if(instance == null){
            instance = new AuthController();
        }
        return instance;
    }


    public void setUp(BaseActivity callingActivity) {
        authInterface.configureAuth(callingActivity);
        if(authInterface.checkUserLogged()) {
            goToActivityAndFinish(callingActivity, MainActivity.class);
        }else{
            goToActivityAndFinish(callingActivity, WelcomeActivity.class);
        }
    }


    public void onLoginButtonPressed(BaseActivity callingActivity) {
        goToActivity(callingActivity, LoginActivity.class);
    }

    public void login(BaseActivity callingActivity, String username, String password) {
        this.callingActivity = callingActivity;
        authInterface.login(username, password);
    }

    public void onLoginSuccess(String username) {
        callingActivity.onSuccess("Logged");
        goToActivityAndFinish(callingActivity, MainActivity.class);
    }

    public void onLoginFailure(String msg, String username) {
        if(Objects.requireNonNull(msg).contains("User not confirmed in the system")) {

            HashMap<String, String> extras = new HashMap<String, String>() {{
                put("username", username);
            }};

            goToActivity(callingActivity, VerifyAccount.class, extras);
        }else {
            callingActivity.onFail("Error while login");
        }
    }


    public void onRegisterButtonPressed(BaseActivity callingActivity) {
        goToActivity(callingActivity, RegisterActivity.class);
    }

    public void signUp(BaseActivity callingActivity, String username, String email, String password) {
        this.callingActivity = callingActivity;
        authInterface.signUp(username, email, password);
    }

    public void onSignUpSuccess(String username) {
        callingActivity.onSuccess("Signup success");

        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("username", username);
        }};

        goToActivity(callingActivity, VerifyAccount.class, extras);
    }

    public void onSignUpFailure() {
        callingActivity.onFail("Error while signup");
    }


    public void confirmSignUp(BaseActivity callingActivity, String username, String confirmationCode){


    }

    public void sendVerificationCode(String username){

    }

    public void loginWithGoogle(BaseActivity callingActivity){
        this.callingActivity = callingActivity;
        authInterface.loginWithGoogle(callingActivity);
    }

    public void onLoginWithGoogleFailure() {
        callingActivity.onFail("Error while signup");
    }


    public void signOut(Activity callingActivity){

    }

}
