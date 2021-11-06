package com.ingsw2122_n_03.natour.application;

import android.app.Activity;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.LoginActivity;
import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.RegisterActivity;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.VerifyAccountActivity;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;

import java.util.HashMap;

public class AuthController extends Controller {

    private static AuthController instance = null;
    private final AuthInterface authInterface;

    private ErrorActivity errorActivity;
    private LoginActivity loginActivity;
    private MainActivity mainActivity;
    private RegisterActivity registerActivity;
    private SplashActivity splashActivity;
    private VerifyAccountActivity verifyAccountActivity;
    private WelcomeActivity welcomeActivity;

    private AuthController() {
        authInterface = new AmplifyAuthImplementation();
    }

    public static AuthController getInstance() {
        if(instance == null){
            instance = new AuthController();
        }
        return instance;
    }


    public void setErrorActivity(ErrorActivity errorActivity) {
        this.errorActivity = errorActivity;
    }

    public void setLoginActivity(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    public void setRegisterActivity(RegisterActivity registerActivity) {
        this.registerActivity = registerActivity;
    }

    public void setSplashActivity(SplashActivity splashActivity) {
        this.splashActivity = splashActivity;
    }

    public void setVerifyAccountActivity(VerifyAccountActivity verifyAccountActivity) {
        this.verifyAccountActivity = verifyAccountActivity;
    }

    public void setWelcomeActivity(WelcomeActivity welcomeActivity) {
        this.welcomeActivity = welcomeActivity;
    }


    public void setUp() {
        authInterface.configureAuth(splashActivity);
        if(authInterface.checkUserLogged()) {
            goToActivityAndFinish(splashActivity, MainActivity.class);
        }else{
            goToActivityAndFinish(splashActivity, WelcomeActivity.class);
        }
    }

    public void onSetUpFailure() {
        goToActivityAndFinish(splashActivity, ErrorActivity.class);
    }


    public void onLoginButtonPressed() {
        goToActivity(welcomeActivity, LoginActivity.class);
    }

    public void login(String username, String password) {
        authInterface.login(username, password);
    }

    public void onLoginSuccess(String username) {
        loginActivity.onSuccess(loginActivity.getResources().getString(R.string.login_success));
        welcomeActivity.finish();
        if(verifyAccountActivity != null) { verifyAccountActivity.finish(); }
        goToActivityAndFinish(loginActivity, MainActivity.class);
    }

    public void onLoginFailure(int i) {
        if(i == 0) {
            loginActivity.onFail(loginActivity.getResources().getString(R.string.wrong_credential));
        }else {
            loginActivity.onFail(loginActivity.getResources().getString(R.string.login_generic_error));
        }
    }

    public void onLoginAuthentication(String username) {
        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("username", username);
        }};
        goToActivity(loginActivity, VerifyAccountActivity.class, extras);
    }


    public void onRegisterButtonPressed() {
        goToActivity(welcomeActivity, RegisterActivity.class);
    }

    public void signUp(String username, String email, String password) {
        authInterface.signUp(username, email, password);
    }

    public void onSignUpSuccess(String username, String password) {
        registerActivity.onSuccess("Signup success");

        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("username", username);
            put("password", password);
        }};

        goToActivityAndFinish(registerActivity, VerifyAccountActivity.class, extras);
    }

    public void onSignUpFailure(int i) {
        if(i == 0) {
            registerActivity.onFail(registerActivity.getResources().getString(R.string.username_already_used));
        }else {
            registerActivity.onFail(registerActivity.getResources().getString(R.string.signup_generic_error));
        }
    }

    public void confirmSignUp(String username, String password, String confirmationCode){
        authInterface.confirmSignUp(username, password, confirmationCode);
    }

    public void sendVerificationCode(String username){
        authInterface.sendVerificationCode(username);
    }


    public void loginWithGoogle(){
        authInterface.loginWithGoogle(welcomeActivity);
    }

    public void onLoginWithGoogleFailure() {
        welcomeActivity.onFail("Error while signup");
    }


    public void signOut(Activity callingActivity){
        authInterface.signOut();
        goToActivityAndFinish(callingActivity, WelcomeActivity.class);
    }

}
