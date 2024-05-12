package com.example.rabie;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.tools.r8.L;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginTeacher extends AppCompatActivity {

    private EditText email, password;
    private Button btnSignIn;
    private FirebaseAuth firebaseAuth;
    private ProgressDialog progressDialog;
    private String emailS, passwordS;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private CheckBox rememberMe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_login_teacher);


        email = findViewById(R.id.emailSignIn);
        password = findViewById(R.id.passwordSignIn);
        btnSignIn = findViewById(R.id.btnSignIn);
        rememberMe = findViewById(R.id.rememberMe);

        firebaseAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
//        AuthSingleton authSingleton = AuthSingleton.getInstance();
//
//        if (authSingleton.isLoggedIn()) {
//            startActivity(new Intent(LoginTeacher.this, TeacherDashboard.class));
//            finish(); // Finish the current activity to prevent going back to it after login
//        } else {
//            Toast.makeText(this, "Please sign in!", Toast.LENGTH_SHORT).show();
//        }
        SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
        boolean resCheckBox = preferences.getBoolean("remember", false);

        if (resCheckBox) {
            startActivity(new Intent(LoginTeacher.this, Create.class));
            overridePendingTransition(R.anim.slide_out_right, R.anim.slide_in_left);

        } else {
            Toast.makeText(this, "Please sign in !", Toast.LENGTH_SHORT).show();
        }
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("remember", true);
                    editor.apply();
                } else if (!buttonView.isChecked()) {
                    SharedPreferences preferences = getSharedPreferences("checkBox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("remember", false);
                    editor.apply();
                }
            }
        });



        btnSignIn.setOnClickListener(v -> {
            emailS = email.getText().toString().trim();
            passwordS = password.getText().toString().trim();
            if (!isValidEmail(emailS)) {
                email.setError("Email is invalid!!");
            } else if (passwordS.length() < 4) {
                password.setError("Password is invalid");
            } else {
                login(emailS, passwordS);
            }
        });

    }

    private void login(String emailS, String passwordS) {
        progressDialog.setMessage("Please wait...!");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailS, passwordS).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AuthSingleton.getInstance().setLoggedIn(true);
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String uid = user.getUid();
                    DatabaseReference teacherRef = FirebaseDatabase.getInstance().getReference().child("teachers").child(uid);
                    teacherRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                String userEmail = user.getEmail();
                                SharedPreferences.Editor editor = getSharedPreferences("userPrefs", MODE_PRIVATE).edit();
                                editor.putString("userEmail", userEmail);
                                editor.apply();

                                finish();
                                Intent intent = new Intent(LoginTeacher.this, TeacherDashboard.class);
                                intent.putExtra("userEmail", userEmail);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(LoginTeacher.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                progressDialog.dismiss();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                        }
                    });
                } else {
                    Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            } else {
                Toast.makeText(this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });
    }


    private void checkEmailVerification() {
        FirebaseUser loggedUser = firebaseAuth.getCurrentUser();
        if (loggedUser != null) {
            if (loggedUser.isEmailVerified()) {
                finish();
                startActivity(new Intent(LoginTeacher.this, TeacherDashboard.class));
                progressDialog.dismiss();
            } else {
                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                progressDialog.dismiss();
            }
        }
    }

    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

}