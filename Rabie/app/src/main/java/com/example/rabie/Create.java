package com.example.rabie;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.TextView;
import android.widget.Toast;


public class Create extends AppCompatActivity {

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private EditText nameEditText, pdfEditText, descEditText, category, keyEdit;
    private Button saveButton;
    private TextView teacherTextView;

    private DatabaseReference databaseReference, teacherReference;
    private Uri selectedFileUri;

    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private TextView teacher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_create);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        String teacherName = sharedPreferences.getString("teacherName", "");

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

        nameEditText = findViewById(R.id.nameEditText);
        pdfEditText = findViewById(R.id.pdfEditText);
        descEditText = findViewById(R.id.descEditText);
        category = findViewById(R.id.category);
        //teacher = findViewById(R.id.textVie);
        saveButton = findViewById(R.id.saveButton);
        keyEdit = findViewById(R.id.keyEdit);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
        teacherReference = FirebaseDatabase.getInstance().getReference("teachers");

        teacherReference.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String teacherName = snapshot.child("teacherName").getValue(String.class);
                        String userCourse = snapshot.child("course").getValue(String.class);
                        //teacherTextView = findViewById(R.id.textVie);
                        category = findViewById(R.id.category);
                        //teacherTextView.setText(teacherName);
                        category.setText(userCourse);
                        TextView userNameTextView = headerView.findViewById(R.id.user_name);
                        userNameTextView.setText(teacherName);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("teacherName", teacherName);
                        editor.apply();

                    }
                } else {
                    Toast.makeText(Create.this, "Teacher not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(Create.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        pdfEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker();
            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCourse();
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        Intent intent1 = new Intent(Create.this,TeacherDashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;

                    case R.id.nav_form:
                        Intent formQuiz = new Intent(Create.this,CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                        startActivity(formQuiz);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.student:
                        Intent student = new Intent(Create.this, StudentParticipated.class);
                        student.putExtra("teacherName", teacher.getText().toString());
                        student.putExtra("userEmail", userEmail);
                        startActivity(student);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_quiz:
                        Intent quizList = new Intent(Create.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_list:
                        Intent courseList = new Intent(Create.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(courseList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;

                    case R.id.Message:

                        Intent messageIntent = new Intent(Create.this, TeacherMessage.class);
                        startActivity(messageIntent);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.pie:
                        Intent pie = new Intent(Create.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(Create.this, SondageActivity.class);
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

                        Intent logoutIntent = new Intent(Create.this, LoginTeacher.class);
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
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), FILE_PICKER_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                selectedFileUri = data.getData();
                pdfEditText.setText(selectedFileUri.toString());
            }
        }
    }

    private void saveCourse() {
        String courseName = nameEditText.getText().toString().trim();
        String pdfUrl = pdfEditText.getText().toString().trim();
        String description = descEditText.getText().toString().trim();
        String categoryValue = category.getText().toString().trim();
        //String teacherName = teacherTextView.getText().toString().trim();
        String key = keyEdit.getText().toString().trim();
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String teacherName = sharedPreferences.getString("teacherName", "");

        if (!courseName.isEmpty() && selectedFileUri != null && !description.isEmpty()) {
            StorageReference pdfRef = storageReference.child("pdfs/" + courseName + ".pdf");
            pdfRef.putFile(selectedFileUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            pdfRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String pdfUrl = uri.toString();
                                    DatabaseReference newCourseRef = databaseReference.push();
                                    newCourseRef.child("name").setValue(courseName);
                                    newCourseRef.child("pdfUrl").setValue(pdfUrl);
                                    newCourseRef.child("description").setValue(description);
                                    newCourseRef.child("category").setValue(categoryValue);
                                    newCourseRef.child("teacherName").setValue(teacherName);
                                    newCourseRef.child("key").setValue(key);

                                    showCustomToast(true);
                                    nameEditText.setText("");
                                    pdfEditText.setText("");
                                    descEditText.setText("");
                                    keyEdit.setText("");
                                    selectedFileUri = null;
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            showCustomToast(false);
                        }
                    });
        } else {
            showCustomToast(false);
        }
    }

    private void showCustomToast(boolean isSuccess) {
        LayoutInflater inflater = getLayoutInflater();
        View layout;

        if (isSuccess) {
            layout = inflater.inflate(R.layout.toast_success, findViewById(R.id.custom_toast_container));
        } else {
            layout = inflater.inflate(R.layout.toast_failure, findViewById(R.id.custom_toast));
        }

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
