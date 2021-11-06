package com.ingsw2122_n_03.natour.application;

import android.util.Log;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.infastructure.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.ErrorActivity;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

public final class AmplifyAuthImplementation implements AuthInterface {

    protected AmplifyAuthImplementation() {}

    @Override
    public void configureAuth(BaseActivity callingActivity) {
        AuthController controller = AuthController.getInstance();
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(callingActivity.getApplicationContext());
        }catch (Amplify.AlreadyConfiguredException e){
           Log.i("NaTour", "Amplify already Configured");
        }catch (AmplifyException e){
            Log.e("NaTour", "Amplify Configuration Failed");
            controller.goToActivityAndFinish(callingActivity, ErrorActivity.class);
        }
    }

    @Override
    public boolean checkUserLogged() {
        return Amplify.Auth.getCurrentUser() != null;
    }

    @Override
    public void login(String username, String password) {

        AuthController controller = AuthController.getInstance();
        Amplify.Auth.signIn(
                username,
                password,
                result -> {
                    Log.i("NaTour", result.isSignInComplete() ? "Sign in succeeded" : "Sign in not complete");
                    controller.onLoginSuccess(username);
                },
                error -> {
                    Log.e("NaTour", error.getMessage());
                    controller.onLoginFailure(error.getMessage(), username);
                }
        );
    }

    @Override
    public void signUp(String username, String email, String password) {
        AuthController controller = AuthController.getInstance();
        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.email(), email)
                .build();
        Amplify.Auth.signUp(username, password, options,
                result -> {
                    Log.i("NaTour", "Result: " + result.toString());
                    controller.onSignUpSuccess(username, password);
                },
                error -> {
                    Log.e("NaTour", "Sign up failed", error);
                    controller.onSignUpFailure();
                }
        );
    }

    @Override
    public void confirmSignUp(String username, String password, String confirmationCode) {
        Amplify.Auth.confirmSignUp(
                username,
                confirmationCode,
                result -> {
                    Log.i("NaTour", result.isSignUpComplete() ? "Confirm signUp succeeded" : "Confirm sign up not complete");

                    if(result.isSignUpComplete()){
                        login(username, password);
                    }
                },
                error -> Log.e("NaTour", error.toString())
        );
    }

    @Override
    public void sendVerificationCode(String username) {
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

    @Override
    public void loginWithGoogle(BaseActivity callingActivity) {
        AuthController controller = AuthController.getInstance();
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), callingActivity,
                result -> Log.i("NaTour", result.toString()),
                error -> {
                    Log.e("NaTour", error.toString());
                    controller.onLoginWithGoogleFailure();
                }
        );
    }

    @Override
    public void signOut() {
        Amplify.Auth.signOut(
                () -> {
                    Log.i("NaTour", "Signed out successfully");
                },
                error -> Log.e("NaTour", error.toString())
        );
    }

}
