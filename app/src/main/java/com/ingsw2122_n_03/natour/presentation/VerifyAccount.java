package com.ingsw2122_n_03.natour.presentation;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.ingsw2122_n_03.natour.R;
import com.ingsw2122_n_03.natour.application.Controller;
import com.ingsw2122_n_03.natour.presentation.support.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

public class VerifyAccount extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_account);

        Controller controller = Controller.getInstance();

        MaterialToolbar materialToolbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        TextInputEditText usernameEditText = (TextInputEditText) findViewById(R.id.username);
        TextInputEditText verificationCodeEditText = (TextInputEditText) findViewById(R.id.verificationCode);
        TextView resendCode = (TextView) findViewById(R.id.resendCode);
        Button verifyButton = (Button) findViewById(R.id.verifyButton);

        materialToolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        resendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                controller.sendVerificationCode(username);
            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DA FARE CHECK INPUT
                String username = usernameEditText.getText().toString();
                String verificationCode = verificationCodeEditText.getText().toString();
                controller.confirmSignUp(VerifyAccount.this, username, verificationCode);
            }
        });
    }

    @Override
    public void onSuccess() {
        //SHOW SNACKBAR
    }

    @Override
    public void onFail() {
        //SHOW SNACKBAR
    }
}