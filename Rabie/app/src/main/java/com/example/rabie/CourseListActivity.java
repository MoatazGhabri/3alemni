package com.example.rabie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

public class CourseListActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private static final int FILE_PICKER_REQUEST_CODE = 1;
    private RecyclerView recyclerView;
    private CourseAdapter adapter;
    private DatabaseReference databaseReference;
    private String userEmail;
    private EditText pdfEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_course_list);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CourseAdapter();
        recyclerView.setAdapter(adapter);

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        userEmail = sharedPreferences.getString("userEmail", "");
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
        databaseReference = FirebaseDatabase.getInstance().getReference("Courses");

        databaseReference.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Course> courses = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Course course = snapshot.getValue(Course.class);
                    String courseId = snapshot.getKey();
                    course.setId(courseId);
                    courses.add(course);
                }
                adapter.setCourses(courses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseListActivity.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        adapter.setOnItemClickListener(new CourseAdapter.OnItemClickListener() {
            @Override
            public void onEditClick(int position) {

                showEditDialog(adapter.getCourses().get(position), position);
            }

            @Override
            public void onDeleteClick(int position) {
                showDeleteDialog(position);
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        Intent intent1 = new Intent(CourseListActivity.this,TeacherDashboard.class);
                        startActivity(intent1);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_form:
                        Intent coursForm = new Intent(CourseListActivity.this,Create.class);
                        startActivity(coursForm);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.nav_form:
                        Intent formQuiz = new Intent(CourseListActivity.this,CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                        startActivity(formQuiz);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.student:
                        // Extract the text from the TextView and pass it as a String extra
                        Intent student = new Intent(CourseListActivity.this, StudentParticipated.class);

                        startActivity(student);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_quiz:
                        Intent quizList = new Intent(CourseListActivity.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;

                    case R.id.Message:
                        Intent message = new Intent(CourseListActivity.this, TeacherMessage.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(message);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                        break;
                    case R.id.pie:
                        Intent pie = new Intent(CourseListActivity.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(CourseListActivity.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(sondage);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Logout:
                        // Clear SharedPreferences
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.apply();

                        // Redirect to login screen
                        Intent logoutIntent = new Intent(CourseListActivity.this, LoginTeacher.class);
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

    private void showEditDialog(Course course, int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_edit_course, null);
        builder.setView(dialogView);

        EditText nameEditText = dialogView.findViewById(R.id.editNameEditText);
        EditText pdfEditText = dialogView.findViewById(R.id.editPdfEditText);
        EditText keyEditText = dialogView.findViewById(R.id.editKeyEditText);
        EditText descriptionEditText = dialogView.findViewById(R.id.editDescriptionEditText);
        EditText categoryEdit = dialogView.findViewById(R.id.categoryEditText);
        Button selectPdfButton = dialogView.findViewById(R.id.selectPdfButton);

        nameEditText.setText(course.getName());
        pdfEditText.setText(course.getPdfUrl());
        keyEditText.setText(course.getKey());
        descriptionEditText.setText(course.getDescription());
        categoryEdit.setText(course.getCategory());
        this.pdfEditText = pdfEditText;

        selectPdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFilePicker(pdfEditText);
            }
        });

        builder.setTitle("Edit Course");

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String name = nameEditText.getText().toString().trim();
                String pdfUrl = pdfEditText.getText().toString().trim();
                String key = keyEditText.getText().toString().trim();
                String description = descriptionEditText.getText().toString().trim();
                String category = categoryEdit.getText().toString().trim();

                if (!name.isEmpty() && !pdfUrl.isEmpty() && !key.isEmpty() && !description.isEmpty()) {
                    Course updatedCourse = new Course(course.getId(), name, pdfUrl, key, description,category);
                    Uri newPdfUri = (pdfUrl.equals(course.getPdfUrl())) ? null : Uri.parse(pdfUrl);
                    updateCourse(updatedCourse, position, newPdfUri);
                } else {
                    Toast.makeText(CourseListActivity.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancel", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
        alertDialog.show();
    }

    private void openFilePicker(EditText pdfEditText) {
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
                Uri selectedFileUri = data.getData();
                String selectedFilePath = selectedFileUri.toString();
                pdfEditText.setText(selectedFilePath);
            }
        }
    }

    private void updateCourse(Course updatedCourse, int position, Uri newPdfUri) {
        String courseId = updatedCourse.getId();
        if (courseId != null) {
            String oldPdfUrl = updatedCourse.getPdfUrl();
            DatabaseReference courseRef = databaseReference.child(courseId);

            SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
            String teacherName = sharedPreferences.getString("teacherName", "");

            updatedCourse.setTeacherName(teacherName);

            if (newPdfUri != null) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();
                StorageReference pdfRef = storageReference.child("pdfs/" + updatedCourse.getName() + ".pdf");
                pdfRef.putFile(newPdfUri)
                        .addOnSuccessListener(taskSnapshot -> pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String newPdfUrl = uri.toString();
                            updatedCourse.setPdfUrl(newPdfUrl);
                            courseRef.setValue(updatedCourse)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            Toast.makeText(CourseListActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(CourseListActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        }))
                        .addOnFailureListener(e -> Toast.makeText(CourseListActivity.this, "Failed to upload new PDF file", Toast.LENGTH_SHORT).show());
            } else {
                courseRef.setValue(updatedCourse)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(CourseListActivity.this, "Course updated successfully", Toast.LENGTH_SHORT).show();
                                adapter.notifyDataSetChanged();
                            } else {
                                Toast.makeText(CourseListActivity.this, "Failed to update course", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        } else {
            Toast.makeText(CourseListActivity.this, "Faild to uodate course", Toast.LENGTH_SHORT).show();
        }
    }



    private void showDeleteDialog(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Course");
        builder.setMessage("Are you sure you\n want to delete this course?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteCourse(position);
            }
        });
        builder.setNegativeButton("No", null);
        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
        alertDialog.show();
        //builder.show();
    }

    private void deleteCourse(int position) {
        String courseId = adapter.getCourses().get(position).getId();
        databaseReference.child(courseId).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(CourseListActivity.this, "Course deleted successfully", Toast.LENGTH_SHORT).show();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CourseListActivity.this, "Failed to delete course", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
