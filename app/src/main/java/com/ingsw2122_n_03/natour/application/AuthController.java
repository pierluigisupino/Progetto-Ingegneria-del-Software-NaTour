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

public final class AuthController extends Controller {

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
        authInterface = new AmplifyAuthImplementation(this);
    }

    public static AuthController getInstance() {
        if(instance == null){
            instance = new AuthController();
        }
        return instance;
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

    public void login(String email, String password) {
        authInterface.signIn(email, password);
    }

    public void onLoginSuccess() {
        if(loginActivity != null){
            loginActivity.onSuccess(loginActivity.getResources().getString(R.string.login_success));
            goToActivityAndFinish(loginActivity, MainActivity.class);
        }

        if(verifyAccountActivity != null) {
            goToActivityAndFinish(verifyAccountActivity, MainActivity.class);
        }

        if(welcomeActivity != null) welcomeActivity.finish();
    }

    public void onLoginFailure(int errorCode) {
        if(errorCode == 0) {
            loginActivity.onFail(loginActivity.getResources().getString(R.string.wrong_credential_error));
        }else {
            loginActivity.onFail(loginActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void onLoginAuthentication(String email, String password) {
        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }};
        goToActivity(loginActivity, VerifyAccountActivity.class, extras);
    }


    public void onRegisterButtonPressed() {
        goToActivity(welcomeActivity, RegisterActivity.class);
    }

    public void signUp(String username, String email, String password) {
        authInterface.signUp(username, email, password);
    }

    public void onSignUpSuccess(String email, String password) {
        registerActivity.onSuccess(registerActivity.getResources().getString(R.string.signup_success));

        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }};

        goToActivityAndFinish(registerActivity, VerifyAccountActivity.class, extras);
    }

    public void onSignUpFailure(int errorCode) {
        if(errorCode == 0) {
            registerActivity.onFail(registerActivity.getResources().getString(R.string.email_taken_error));
        }else {
            registerActivity.onFail(registerActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void confirmSignUp(String email, String password, String confirmationCode){
        authInterface.confirmSignUp(email, password, confirmationCode);
    }

    public void onConfirmSignUpSuccess(){
        verifyAccountActivity.onSuccess(verifyAccountActivity.getResources().getString(R.string.verify_success));
    }

    public void onConfirmSignUpFailure(int errorCode){
        if(errorCode == 0) {
            verifyAccountActivity.onFail(registerActivity.getResources().getString(R.string.wrong_verification_code_error));
        }else{
            verifyAccountActivity.onFail(registerActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void sendVerificationCode(String email){
        authInterface.sendVerificationCode(email);
    }

    public void onSendVerificationCodeSuccess(){
        verifyAccountActivity.onSuccess(verifyAccountActivity.getResources().getString(R.string.verification_code_sent));
    }

    public void onSendVerificationCodeFailure(int errorCode){
        if(errorCode == 0){
            verifyAccountActivity.onFail(verifyAccountActivity.getResources().getString(R.string.too_many_requests_error));
        }else {
            verifyAccountActivity.onFail(registerActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void loginWithGoogle(){
        authInterface.loginWithGoogle(welcomeActivity);
    }

    public void onLoginWithGoogleSuccess(){
        welcomeActivity.onFail(welcomeActivity.getResources().getString(R.string.login_success));
    }

    public void onLoginWithGoogleFailure() {
        welcomeActivity.onFail(welcomeActivity.getResources().getString(R.string.generic_error));
    }


    public void signOut(Activity callingActivity){
        authInterface.signOut();
        goToActivityAndFinish(callingActivity, WelcomeActivity.class);
    }

    public void onSignOutSuccess(){
        mainActivity.onFail(welcomeActivity.getResources().getString(R.string.logout_success));
    }

    public void onSignOutFailure() {
        mainActivity.onFail(welcomeActivity.getResources().getString(R.string.generic_error));
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
}
