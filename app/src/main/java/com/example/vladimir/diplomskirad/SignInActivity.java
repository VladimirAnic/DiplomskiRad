package com.example.vladimir.diplomskirad;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    Button btnLogin;
    private static final int SIGN_IN_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // release listener in onStop
    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !user.toString().equals(""))
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }


    @Override
    public void onClick(View view) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .build(),
                    SIGN_IN_REQUEST_CODE
            );

        } else
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
    }
}