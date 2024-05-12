package com.example.rabie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity {

    private TextView goToSignIn;
    private Button btnSignUp;
    private EditText fullName,email,cin,phone,password;
    private String fullNameS,emailS,cinS,phoneS,passwordS;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_sign_up);

        goToSignIn = findViewById(R.id.goToSignIn);
        btnSignUp = findViewById(R.id.btnSignUp);
        fullName = findViewById(R.id.fullNameSignUp);
        email = findViewById(R.id.emailSignUp);
        cin = findViewById(R.id.cinSignUp);
        phone = findViewById(R.id.phoneSignUp);
        password = findViewById(R.id.passwordSignUp);

        //Firebase
        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        btnSignUp.setOnClickListener(v -> {
            if(validate()){
                progressDialog.setMessage("Please wait...!");
                progressDialog.show();
                firebaseAuth.createUserWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        sendEmailVerification();
                    }else {
                        Toast.makeText(SignUpActivity.this, "Failed !", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                });
            }
        });

        goToSignIn.setOnClickListener(v -> {
            startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

            Toast.makeText(this, "go to Sign in activity ", Toast.LENGTH_SHORT).show();
        });
    }

    private void sendEmailVerification() {
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();
        if (loggedUser != null) {
            loggedUser.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    sendUserData();
                    progressDialog.dismiss();
                    Toast.makeText(this, "Registration done ! Please check your email address !", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SignUpActivity.this,SignInActivity.class));
                    overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

                    finish();

                }else {
                    Toast.makeText(this, "Registration failed!!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
        }
    }

    private void sendUserData() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = firebaseDatabase.getReference("Users");
        User user = new User(fullNameS,emailS,cinS,phoneS);
        myRef.child(""+firebaseAuth.getUid()).setValue(user);
    }

    private boolean validate() {
        boolean res = false;
        fullNameS = fullName.getText().toString().trim();
        emailS = email.getText().toString().trim();
        cinS = cin.getText().toString().trim();
        phoneS = phone.getText().toString().trim();
        passwordS = password.getText().toString().trim();

        if (fullNameS.isEmpty() || fullNameS.length()<7){
            fullName.setError("Full name is invalid !");
        } else if (!isValidEmail(emailS)) {
            email.setError("Email is invalid!");
        }else if (cinS.length() != 8) {
            cin.setError("CIN is invalid!");
        }else if (phoneS.length() != 8) {
            phone.setError("Phone is invalid!");
        }else if (passwordS.isEmpty() || passwordS.length()<6) {
            password.setError("Password is invalid!");
        }else {
            res = true;
        }

        return res;
    }

    private boolean isValidEmail(String email){
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}