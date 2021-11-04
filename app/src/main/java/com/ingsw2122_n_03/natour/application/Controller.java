package com.ingsw2122_n_03.natour.application;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.SignInUIOptions;
import com.amazonaws.mobile.client.UserStateDetails;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;

import com.ingsw2122_n_03.natour.presentation.MainActivity;
import com.ingsw2122_n_03.natour.presentation.VerifyAccount;
import com.ingsw2122_n_03.natour.presentation.WelcomeActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.Objects;

public class Controller {

    private static Controller instance = null;

    private Controller() {}

    public static Controller getInstance(){
        if(instance == null){
            instance = new Controller();
        }
        return instance;
    }

    public void goToActivity(BaseActivity source, Class<?> destination){
        Intent intent = new Intent(source, destination);
        source.startActivity(intent);
    }

    public void goToActivityAndFinish(BaseActivity source, Class<?> destination){
        Intent intent = new Intent(source, destination);
        source.startActivity(intent);
        source.finish();
    }

    public void configureAmplify(BaseActivity callingActivity){
        try {

            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(callingActivity.getApplicationContext());
            start(callingActivity);

        }catch (Amplify.AlreadyConfiguredException e){
            start(callingActivity);
        }catch ( AmplifyException e){
            goToActivityAndFinish(callingActivity, ErrorActivity.class);
        }
    }

    public void start(BaseActivity callingActivity){

        if(getUser(callingActivity)){
            callingActivity.finish();
            Intent intent = new Intent(callingActivity, MainActivity.class);
            callingActivity.startActivity(intent);
        }else{
            goToActivityAndFinish(callingActivity, WelcomeActivity.class);
        }
    }

    public boolean getUser(BaseActivity callingActivity) {
        return Amplify.Auth.getCurrentUser() != null;
    }

    public void login(BaseActivity callingActivity, String username, String password){

        Amplify.Auth.signIn(
                username,
                password,
                result -> {
                    Log.i("NaTour", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    goToActivityAndFinish(callingActivity, MainActivity.class);
                    callingActivity.onSuccess("Logged");
                },
                error -> {
                    Log.e("NaTour", error.getMessage());

                    if(Objects.requireNonNull(error.getMessage()).contains("User not confirmed in the system")){
                        goToActivity(callingActivity, VerifyAccount.class);
                    }

                    callingActivity.onFail("Error while login");
                }
        );
    }

    public void signUp(BaseActivity callingActivity, String username, String email, String password){

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build();

        Amplify.Auth.signUp(username, password, options,
                result -> {
                    Log.i("NaTour", "Result: " + result.toString());
                    callingActivity.onSuccess("Signup success");
                    goToActivity(callingActivity, VerifyAccount.class);
                },
                error -> {
                    Log.e("NaTour", "Sign up failed", error);
                    callingActivity.onFail("Error whle signup");
                }
        );
    }

    public void confirmSignUp(BaseActivity callingActivity, String username, String confirmationCode){
        Amplify.Auth.confirmSignUp(
                username,
                confirmationCode,
                result -> Log.i("NaTour", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete"),
                error -> Log.e("NaTour", error.toString())
        );

    }

    public void  sendVerificationCode(String username){
        AWSMobileClient.getInstance().resendSignUp(username, new Callback<SignUpResult>() {
            @Override
            public void onResult(SignUpResult signUpResult) {
                Log.i("NaTour", "A verification code has been sent via" +
                        signUpResult.getUserCodeDeliveryDetails().getDeliveryMedium()
                        + " at " +
                        signUpResult.getUserCodeDeliveryDetails().getDestination());
            }

            @Override
            public void onError(Exception e) {
                Log.e("NaTour", e.toString());
            }
        });
    }

    public void loginWithGoogle(BaseActivity callingActivity){
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), callingActivity,
                result -> Log.i("NaTour", result.toString()),
                error -> {
                    Log.e("NaTour", error.toString());
                    goToActivity(callingActivity, ErrorActivity.class);
                }
        );
    }

    public void signOut(Activity callingActivity){
        Amplify.Auth.signOut(
                () -> {
                    Log.i("NaTour", "Signed out successfully");
                },
                error -> Log.e("NaTour", error.toString())
        );
    }
}
