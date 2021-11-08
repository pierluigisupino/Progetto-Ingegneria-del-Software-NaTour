package com.ingsw2122_n_03.natour.infastructure.implementations;

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
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.infastructure.AuthInterface;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import java.util.Objects;

public final class AmplifyAuthImplementation implements AuthInterface {

    private final AuthController controller;

    public AmplifyAuthImplementation(AuthController controller){
        this.controller = controller;
    }

    @Override
    public boolean configureAuth(BaseActivity callingActivity) {
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.configure(callingActivity.getApplicationContext());
            return true;
        }catch (Amplify.AlreadyConfiguredException ignored){
            return true;
        }catch (AmplifyException e){
            controller.onSetUpFailure();
            return false;
        }
    }

    @Override
    public boolean checkUserLogged() {
        return Amplify.Auth.getCurrentUser() != null;
    }

    @Override
    public void signIn(String email, String password) {

        Amplify.Auth.signIn(
                email,
                password,
                result -> controller.onSignInSuccess(),
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("User not confirmed in the system")) {
                        controller.onLoginAuthentication(email, password);
                    }else if(messageError.contains("Failed since user is not authorized")){
                        controller.onLoginFailure(0);
                    }else if(messageError.contains("User not found in the system")){
                        controller.onLoginFailure(0);
                    }else {
                        controller.onLoginFailure(1);
                    }
                }
        );
    }

    @Override
    public void signUp(String username, String email, String password) {

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.name(), username)
                .build();

        Amplify.Auth.signUp(email, password, options,
                result -> controller.onSignUpSuccess(email, password),
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("Username already exists in the system")) {
                        controller.onSignUpFailure(0);
                    }else {
                        controller.onSignUpFailure(1);
                    }
                }
        );
    }

    @Override
    public void confirmSignUp(String email, String password, String confirmationCode) {
        Amplify.Auth.confirmSignUp(
                email,
                confirmationCode,
                result -> {
                    controller.onConfirmSignUpSuccess();
                    if(result.isSignUpComplete()){
                        signIn(email, password);
                    }
                },
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("Confirmation code entered is not correct")) {
                        controller.onConfirmSignUpFailure(0);
                    }
                }
        );
    }

    @Override
    public void sendVerificationCode(String email) {
        AWSMobileClient.getInstance().resendSignUp(email, new Callback<SignUpResult>() {

            @Override
            public void onResult(SignUpResult signUpResult) {
                controller.onSendVerificationCodeSuccess();
            }

            @Override
            public void onError(Exception e) {

                String messageError = Objects.requireNonNull(e.getMessage());

                if(messageError.contains("Attempt limit exceeded")) {
                    controller.onSendVerificationCodeFailure(0);
                }else {
                    controller.onSendVerificationCodeFailure(1);
                }
            }
        });
    }

    @Override
    public void loginWithGoogle(BaseActivity callingActivity) {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), callingActivity,
                result -> controller.onLoginWithGoogleSuccess(),
                error -> controller.onLoginWithGoogleFailure()
        );
    }

    public void resetPassword(String email){

        Amplify.Auth.resetPassword(
                email,
                result -> {
                    Log.e("NaTour", result.toString());
                    controller.onResetPasswordSuccess(email);
                },
                error -> {

                    Log.e("NaTour", error.toString());

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("User not found in the system")) {
                        controller.onResetPasswordFailure(0);
                    }else if(messageError.contains("Number of allowed operation has exceeded")){
                        controller.onResetPasswordFailure(1);
                    }else {
                        controller.onResetPasswordFailure(2);
                    }
                }
        );
    }

    public void confirmResetPassword(String newPassword, String confirmationCode){
        Amplify.Auth.confirmResetPassword(
                newPassword,
                confirmationCode,
                () -> Log.i("NaTour", "New password confirmed"),
                error -> Log.e("NaTour", error.toString())
        );
    }

    @Override
    public void signOut() {
        Amplify.Auth.signOut(
                controller::onSignOutSuccess,
                error -> controller.onSignOutFailure()
        );
    }

}
