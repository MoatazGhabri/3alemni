package com.example.rabie;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class CreateQuizActivity extends AppCompatActivity {

    private EditText questionEditText, usernameEditText,indexEditText;
    private LinearLayout optionsLayout;
    private Button addOption, submitButton,removeOption;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private List<EditText> optionEditTextList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_create_quiz);

        questionEditText = findViewById(R.id.questionEditText);
        optionsLayout = findViewById(R.id.optionsLayout);
        indexEditText = findViewById(R.id.indexEditText);

        usernameEditText = findViewById(R.id.usernameEditText);
        addOption = findViewById(R.id.addOptionButton);
        removeOption = findViewById(R.id.removeOptionButton);
        submitButton = findViewById(R.id.submitButton);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        String teacherName = sharedPreferences.getString("teacherName", "");
        usernameEditText.setText(teacherName);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        View headerView = navigationView.getHeaderView(0);
        TextView userEmailTextView = headerView.findViewById(R.id.user_email);
        userEmailTextView.setText(userEmail);
        TextView userNameTextView = headerView.findViewById(R.id.user_name);
        userNameTextView.setText(teacherName);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                return true;
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        Intent intent1 = new Intent(CreateQuizActivity.this,TeacherDashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_form:
                        Intent coursForm = new Intent(CreateQuizActivity.this,Create.class);
                        startActivity(coursForm);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;

                    case R.id.student:
                        Intent student = new Intent(CreateQuizActivity.this, StudentParticipated.class);

                        startActivity(student);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_quiz:
                        Intent quizList = new Intent(CreateQuizActivity.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_list:
                        Intent courseList = new Intent(CreateQuizActivity.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(courseList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Message:
                        Intent message = new Intent(CreateQuizActivity.this, TeacherMessage.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(message);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.pie:
                        Intent pie = new Intent(CreateQuizActivity.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(CreateQuizActivity.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(sondage);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Logout:
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        Intent logoutIntent = new Intent(CreateQuizActivity.this, LoginTeacher.class);
                        logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(logoutIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        finish();
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addOptionEditText();
            }
        });
        removeOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeOptionEditText();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveQuizToFirebase();
            }
        });

        addOptionEditText();
        addOptionEditText();
        removeOption.setVisibility(View.GONE);

    }

    private void addOptionEditText() {
        EditText optionEditText = new EditText(this);
        optionEditText.setHint("Option " + (optionEditTextList.size() + 1));
        optionEditText.setBackground(ContextCompat.getDrawable(this, R.drawable.shapeedit));
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        int marginInPixels = getResources().getDimensionPixelSize(R.dimen.option_margin);
        int paddingInPixels = getResources().getDimensionPixelSize(R.dimen.option_padding);
        layoutParams.setMargins(0, marginInPixels, 0, 0);
        optionEditText.setPadding(paddingInPixels, paddingInPixels, paddingInPixels, paddingInPixels);
        optionEditText.setLayoutParams(layoutParams);
        optionsLayout.addView(optionEditText);
        optionEditTextList.add(optionEditText);
        removeOption.setVisibility(View.VISIBLE);
    }
    private void removeOptionEditText() {
        if (optionEditTextList.size() > 2) {
            EditText removedOptionEditText = optionEditTextList.remove(optionEditTextList.size() - 1);
            optionsLayout.removeView(removedOptionEditText);

            if (optionEditTextList.size()  <= 2) {
                removeOption.setVisibility(View.GONE);
            }
        }
    }

    private void saveQuizToFirebase() {
        String question = questionEditText.getText().toString();
        List<String> options = new ArrayList<>();
        for (EditText optionEditText : optionEditTextList) {
            options.add(optionEditText.getText().toString());
        }
        int correctOptionIndex = Integer.parseInt(indexEditText.getText().toString());
        String username = usernameEditText.getText().toString();

        DatabaseReference quizRef = FirebaseDatabase.getInstance().getReference("quizzes").push();
        quizRef.child("questionText").setValue(question);
        quizRef.child("options").setValue(options);
        quizRef.child("correctOptionIndex").setValue(correctOptionIndex);
        quizRef.child("teacherName").setValue(username);

        Toast.makeText(this, "Quiz submitted successfully.", Toast.LENGTH_SHORT).show();
        questionEditText.getText().clear();
        indexEditText.getText().clear();
        for (EditText optionEditText : optionEditTextList) {
            optionEditText.getText().clear();
        }
        optionEditTextList.clear();
        optionsLayout.removeAllViews();
        addOptionEditText();
        addOptionEditText();
        removeOption.setVisibility(View.GONE);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
