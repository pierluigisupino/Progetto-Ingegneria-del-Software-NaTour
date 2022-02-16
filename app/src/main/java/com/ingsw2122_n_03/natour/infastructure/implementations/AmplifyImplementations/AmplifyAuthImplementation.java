package com.ingsw2122_n_03.natour.infastructure.implementations.AmplifyImplementations;

import android.app.Activity;

import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobile.client.Callback;
import com.amazonaws.mobile.client.results.SignUpResult;
import com.amplifyframework.AmplifyException;
import com.amplifyframework.analytics.pinpoint.AWSPinpointAnalyticsPlugin;
import com.amplifyframework.api.aws.AWSApiPlugin;
import com.amplifyframework.api.rest.RestOptions;
import com.amplifyframework.auth.AuthProvider;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.ingsw2122_n_03.natour.application.AuthController;
import com.ingsw2122_n_03.natour.application.IterController;
import com.ingsw2122_n_03.natour.infastructure.interfaces.AuthInterface;
import com.ingsw2122_n_03.natour.model.Admin;
import com.ingsw2122_n_03.natour.model.User;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class AmplifyAuthImplementation implements AuthInterface {

    private final AuthController controller;

    public AmplifyAuthImplementation(AuthController controller){
        this.controller = controller;
    }


    @Override
    public boolean configurePlugins(Activity callingActivity) {
        try {
            Amplify.addPlugin(new AWSApiPlugin());
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSPinpointAnalyticsPlugin(callingActivity.getApplication()));
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
                result -> {
                    Analytics.recordPositiveEvent("SignIn");
                    controller.onSignInSuccess();
                },
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("User not confirmed in the system")) {
                        Analytics.recordNegativeEvent("SignIn", messageError);
                        controller.onSignInAuthentication(email, password);
                    }else if(messageError.contains("Failed since user is not authorized")){
                        Analytics.recordNegativeEvent("SignIn", messageError);
                        controller.onSignInFailure(0);
                    }else if(messageError.contains("User not found in the system")){
                        Analytics.recordNegativeEvent("SignIn", messageError);
                        controller.onSignInFailure(0);
                    }else {
                        Analytics.recordNegativeEvent("SignIn", messageError);
                        controller.onSignInFailure(1);
                    }
                }
        );
    }


    @Override
    public void signUp(String name, String email, String password) {

        AuthSignUpOptions options = AuthSignUpOptions.builder()
                .userAttribute(AuthUserAttributeKey.name(), name)
                .build();

        Amplify.Auth.signUp(email, password, options,
                result -> {
                    Analytics.recordPositiveEvent("SignUp");
                    controller.onSignUpSuccess(email, password);
                },
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("Username already exists in the system")) {
                        Analytics.recordNegativeEvent("SignUp", messageError);
                        controller.onSignUpFailure(0);
                    }else {
                        Analytics.recordNegativeEvent("SignUp", messageError);
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
                    Analytics.recordPositiveEvent("ConfirmAccount");
                    controller.onConfirmSignUpSuccess();
                    if(result.isSignUpComplete()){
                        signIn(email, password);
                    }
                },
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("Confirmation code entered is not correct")) {
                        Analytics.recordNegativeEvent("ConfirmAccount", messageError);
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
                Analytics.recordPositiveEvent("SendVerificationCode");
                controller.onSendVerificationCodeSuccess();
            }

            @Override
            public void onError(Exception e) {

                String messageError = Objects.requireNonNull(e.getMessage());

                if(messageError.contains("Attempt limit exceeded")) {
                    Analytics.recordNegativeEvent("SendVerificationCode", messageError);
                    controller.onSendVerificationCodeFailure(0);
                }else {
                    Analytics.recordNegativeEvent("SendVerificationCode", messageError);
                    controller.onSendVerificationCodeFailure(1);
                }
            }
        });
    }


    @Override
    public void loginWithGoogle(BaseActivity callingActivity) {
        Amplify.Auth.signInWithSocialWebUI(AuthProvider.google(), callingActivity,
                result -> {
                    Analytics.recordPositiveEvent("LoginWithGoogle");
                    controller.onLoginWithGoogleSuccess();
                },
                error -> {
                    Analytics.recordNegativeEvent("LoginWithGoogle", error.getMessage());
                    controller.onLoginWithGoogleFailure();
                }
        );
    }


    public void resetPassword(String email){

        Amplify.Auth.resetPassword(
                email,
                result -> {
                    Analytics.recordPositiveEvent("ResetPassword");
                    controller.onResetPasswordSuccess(email);
                },
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("User not found in the system")) {
                        Analytics.recordNegativeEvent("ResetPassword", messageError);
                        controller.onResetPasswordFailure(0);
                    }else if(messageError.contains("Number of allowed operation has exceeded")){
                        Analytics.recordNegativeEvent("ResetPassword", messageError);
                        controller.onResetPasswordFailure(1);
                    }else {
                        Analytics.recordNegativeEvent("ResetPassword", messageError);
                        controller.onResetPasswordFailure(2);
                    }
                }
        );
    }

    public void confirmResetPassword(String newPassword, String confirmationCode){
        Amplify.Auth.confirmResetPassword(
                newPassword,
                confirmationCode,
                controller::onConfirmResetPasswordSuccess,
                error -> {

                    String messageError = Objects.requireNonNull(error.getMessage());

                    if(messageError.contains("Confirmation code entered is not correct")) {
                        controller.onConfirmResetPasswordFailure(0);
                    }else{
                        controller.onConfirmResetPasswordFailure(1);
                    }
                }
        );
    }

    @Override
    public void signOut() {
        Amplify.Auth.signOut(
                controller::onSignOutSuccess,
                error -> controller.onSignOutFailure()
        );
    }

    @Override
    public void initialize() {

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("uid", Amplify.Auth.getCurrentUser().getUserId());

        RestOptions options = RestOptions.builder()
                .addPath("/items/admin")
                .addQueryParameters(queryParams)
                .build();

        Amplify.API.get(
                options,
                result -> {

                    try {
                        if (result.getData().asJSONObject().getBoolean("isAdmin"))
                            IterController.getInstance().setUp(new Admin(Amplify.Auth.getCurrentUser().getUserId()));
                        else
                            IterController.getInstance().setUp(new User(Amplify.Auth.getCurrentUser().getUserId()));
                    } catch (JSONException e) {
                        IterController.getInstance().onSetUpError(false);
                    }

                },

                error -> IterController.getInstance().onSetUpError(false)
        );

    }

}
