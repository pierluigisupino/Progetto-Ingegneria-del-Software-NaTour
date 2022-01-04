package com.ingsw2122_n_03.natour.application;

import android.app.Activity;

import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.infastructure.implementations.AmplifyAuthImplementation;
import com.ingsw2122_n_03.natour.infastructure.interfaces.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.ForgotPasswordActivity;
import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.ResetPasswordActivity;
import com.ingsw2122_n_03.natour.presentation.SignInActivity;
import com.ingsw2122_n_03.natour.presentation.SignUpActivity;
import com.ingsw2122_n_03.natour.presentation.SplashActivity;
import com.ingsw2122_n_03.natour.presentation.VerifyAccountActivity;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;

import java.util.HashMap;

public final class AuthController extends Controller {

    private static AuthController instance = null;
    private final AuthInterface authInterface;

    private WelcomeActivity welcomeActivity;
    private SplashActivity splashActivity;
    private SignUpActivity signUpActivity;
    private SignInActivity signInActivity;
    private VerifyAccountActivity verifyAccountActivity;
    private ForgotPasswordActivity forgotPasswordActivity;
    private ResetPasswordActivity resetPasswordActivity;
    private MainActivity mainActivity;

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
        if(authInterface.configurePlugins(splashActivity)) {
            if (authInterface.checkUserLogged()) {
                IterController iterController = IterController.getInstance();
                iterController.setUpItineraries(splashActivity);
            } else {
                goToActivityAndFinish(splashActivity, WelcomeActivity.class);
            }
        }
    }

    public void onSetUpFailure() {
        goToActivityAndFinish(splashActivity, ErrorActivity.class);
    }


    public void signIn(String email, String password) {
        authInterface.signIn(email, password);
    }

    public void onSignInSuccess() {

        Activity callingActivity = null;

        if(signInActivity != null){
            signInActivity.onSuccess(signInActivity.getResources().getString(R.string.login_success));
            callingActivity = signInActivity;
        }

        if(verifyAccountActivity != null) {
            if(callingActivity != null) callingActivity.finish();
            callingActivity = verifyAccountActivity;
        }

        if(welcomeActivity != null) welcomeActivity.finish();

        IterController iterController = IterController.getInstance();
        iterController.setUpItineraries(callingActivity);

    }

    public void onLoginFailure(int errorCode) {
        if(errorCode == 0) {
            signInActivity.onFail(signInActivity.getResources().getString(R.string.wrong_credential_error));
        }else {
            signInActivity.onFail(signInActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void onLoginAuthentication(String email, String password) {
        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }};
        goToActivity(signInActivity, VerifyAccountActivity.class, extras);
    }


    public void signUp(String name, String email, String password) {
        authInterface.signUp(name, email, password);
    }

    public void onSignUpSuccess(String email, String password) {

        signUpActivity.onSuccess(signUpActivity.getResources().getString(R.string.signup_success));

        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("email", email);
            put("password", password);
        }};

        goToActivityAndFinish(signUpActivity, VerifyAccountActivity.class, extras);

    }

    public void onSignUpFailure(int errorCode) {
        if(errorCode == 0) {
            signUpActivity.onFail(signUpActivity.getResources().getString(R.string.email_taken_error));
        }else {
            signUpActivity.onFail(signUpActivity.getResources().getString(R.string.generic_error));
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
            verifyAccountActivity.onFail(signUpActivity.getResources().getString(R.string.wrong_verification_code_error));
        }else{
            verifyAccountActivity.onFail(signUpActivity.getResources().getString(R.string.generic_error));
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
            verifyAccountActivity.onFail(signUpActivity.getResources().getString(R.string.generic_error));
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

    public void resetPassword(String username){
        authInterface.resetPassword(username);
    }

    public void onResetPasswordSuccess(String email){
        forgotPasswordActivity.onSuccess(forgotPasswordActivity.getResources().getString(R.string.verification_code_sent));

        HashMap<String, String> extras = new HashMap<String, String>() {{
            put("email", email);
        }};

        goToActivityAndFinish(forgotPasswordActivity, ResetPasswordActivity.class, extras);
    }

    public void onResetPasswordFailure(int errorCode){
        if(errorCode == 0){
            forgotPasswordActivity.onFail(forgotPasswordActivity.getResources().getString(R.string.account_error));
        }else if(errorCode == 1){
            forgotPasswordActivity.onFail(forgotPasswordActivity.getResources().getString(R.string.too_many_requests_error));
        }else{
            forgotPasswordActivity.onFail(forgotPasswordActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void confirmResetPassword(String newPassword, String confirmationCode){
        authInterface.confirmResetPassword(newPassword, confirmationCode);
    }

    public void onConfirmResetPasswordSuccess(){
        resetPasswordActivity.onSuccess(resetPasswordActivity.getResources().getString(R.string.password_changed_success));

        if(forgotPasswordActivity != null) forgotPasswordActivity.finish();
        if(signInActivity != null) signInActivity.finish();

        goToActivityAndFinish(resetPasswordActivity, SignInActivity.class);
    }

    public void onConfirmResetPasswordFailure(int errorCode){
        if(errorCode == 0) {
            resetPasswordActivity.onFail(resetPasswordActivity.getResources().getString(R.string.wrong_verification_code_error));
        }else{
            resetPasswordActivity.onFail(resetPasswordActivity.getResources().getString(R.string.generic_error));
        }
    }

    public void signOut(){ authInterface.signOut(); }

    public void onSignOutSuccess(){
        mainActivity.onSuccess(welcomeActivity.getResources().getString(R.string.logout_success));
        goToActivityAndFinish(mainActivity, WelcomeActivity.class);
    }

    public void onSignOutFailure() {
        mainActivity.onFail(welcomeActivity.getResources().getString(R.string.generic_error));
    }


    /**********
     * SETTERS
     **********/

    public void setSignInActivity(SignInActivity signInActivity) {
        this.signInActivity = signInActivity;
    }

    public void setRegisterActivity(SignUpActivity signUpActivity) {
        this.signUpActivity = signUpActivity;
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

    public void setForgotPasswordActivity(ForgotPasswordActivity forgotPasswordActivity){
        this.forgotPasswordActivity = forgotPasswordActivity;
    }

    public void setResetPasswordActivity(ResetPasswordActivity resetPasswordActivity){
        this.resetPasswordActivity = resetPasswordActivity;
    }

    public void setMainActivity(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }

}
