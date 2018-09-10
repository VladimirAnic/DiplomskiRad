package com.example.vladimir.diplomskirad;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.HashMap;
import java.util.Map;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "EmailPassword";


    private EditText mEmailField;
    private EditText mPasswordField;
    final Activity activity = this;
    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        // Views

        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // Buttons
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }
    // [END on_start_check_user]

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(user);
                            IntentIntegrator integrator = new IntentIntegrator(activity);
                            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                            integrator.setPrompt("Skeniraj");
                            integrator.setCameraId(0);
                            integrator.setBeepEnabled(false);
                            integrator.setBarcodeImageEnabled(false);
                            integrator.initiateScan();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Autentifikacija neuspješna.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Autentifikacija neuspješna.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private void signOut() {
        mAuth.signOut();
        updateUI(null);
    }


    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Obvezno.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Obvezno.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Skeniraj");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

        } else {

            findViewById(R.id.email_password_buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.email_password_fields).setVisibility(View.VISIBLE);
            findViewById(R.id.signed_in_buttons).setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();


        if (i == R.id.email_sign_in_button) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_out_button) {
            signOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null){
            if(result.getContents()==null){
                Toast.makeText(this, "Skeniranje prekinuto", Toast.LENGTH_LONG).show();
                signOut();
            }
            else {
                String contents = data.getStringExtra("SCAN_RESULT");
                CallAPI(contents);
               // Toast.makeText(this, "Attendance noted down successfully!", Toast.LENGTH_LONG).show();
                signOut();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
   public void CallAPI(final String contents)
   {
       final FirebaseUser user = mAuth.getCurrentUser();
       // Instantiate the RequestQueue.
       RequestQueue queue = Volley.newRequestQueue(this);
       String url ="http://attendence.azurewebsites.net/test/AddStudentToLecture";
       StringRequest postRequest = new StringRequest(Request.Method.POST, url,
               new com.android.volley.Response.Listener<String>()
               {
                   @Override
                   public void onResponse(String response) {
                       // response
                       Log.d("Response", response);
                       Toast.makeText(getApplicationContext(), "Uspješno upisani na predavanje!", Toast.LENGTH_LONG).show();
                   }
               },
               new com.android.volley.Response.ErrorListener()
               {
                   @Override
                   public void onErrorResponse(VolleyError error) {
                       // error
                       Log.d("Error.Response", error.toString());
                       Toast.makeText(getApplicationContext(), "Ups, nešto je pošlo po krivu!", Toast.LENGTH_LONG).show();
                   }
               }
       ) {
           @Override
           public String getBodyContentType() {
               return "application/x-www-form-urlencoded; charset=UTF-8";
           }
               @Override
               protected Map<String, String> getParams() throws AuthFailureError
               {


                   Map<String, String> params = new HashMap<String, String>();
               params.put("UserName",user.getEmail().toString());
               params.put("LectureID",contents);

               return params;
           }

       };
       queue.add(postRequest);
   }
}