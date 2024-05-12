package com.example.rabie;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.TextView;
import android.widget.Toast;

public class CreateCourse extends AppCompatActivity {
    private FirebaseStorage storage;
    private StorageReference storageReference;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private Spinner categorySpinner;
    private String[] courseCategories = {"Java", "ReactJS", "JavaScript", "HTML", "CSS"};
    private EditText nameEditText, pdfEditText, descEditText;
    private Button saveButton;
    private DatabaseReference databaseReference;
    TextView textView;
    private Uri selectedFileUri;

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_create_course);

        String userEmail = getIntent().getStringExtra("userEmail");

        //categorySpinner = findViewById(R.id.spinner);
        TextView textView = findViewById(R.id.textView1);
        textView.setText("Logged in as: " + userEmail);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courseCategories);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        categorySpinner.setAdapter(adapter);

        drawerLayout = findViewById(R.id.drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                return true;
            }
        });
        nameEditText = findViewById(R.id.nameEditText);
        pdfEditText = findViewById(R.id.pdfEditText);
        descEditText = findViewById(R.id.descEditText);
        saveButton = findViewById(R.id.saveButton);

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
        String category = categorySpinner.getSelectedItem().toString();


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
                                    newCourseRef.child("category").setValue(category);
                                    showCustomToast(true);
                                    nameEditText.setText("");
                                    pdfEditText.setText("");
                                    descEditText.setText("");
                                    categorySpinner.setSelection(0);
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
