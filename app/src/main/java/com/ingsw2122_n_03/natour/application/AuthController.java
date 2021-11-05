package com.ingsw2122_n_03.natour.application;

import android.app.Activity;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.ingsw2122_n_03.natour.infastructure.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public class AuthController extends Controller {

    private static AuthController instance = null;
    private AuthInterface authInterface = AmplifyAuthImplementation.getInstance();

    private AuthController() {}

    public static AuthController getInstance(){
        if(instance == null){
            instance = new AuthController();
        }
        return instance;
    }

    public void setUp(BaseActivity callingActivity){
        authInterface.configure(callingActivity);
        if(authInterface.checkUserLogged()) {
            goToActivityAndFinish(callingActivity, MainActivity.class);
        }else{
            goToActivityAndFinish(callingActivity, WelcomeActivity.class);
        }
    }


    public void login(BaseActivity callingActivity, String username, String password, LinearProgressIndicator progressBar){
        authInterface.login(username, password);

    }

    public void signUp(BaseActivity callingActivity, String username, String email, String password, LinearProgressIndicator progressBar){


    }

    public void confirmSignUp(BaseActivity callingActivity, String username, String confirmationCode){


    }

    public void sendVerificationCode(String username){

    }

    public void loginWithGoogle(BaseActivity callingActivity){

    }

    public void signOut(Activity callingActivity){

    }

}
