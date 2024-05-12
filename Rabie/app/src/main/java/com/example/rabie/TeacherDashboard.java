package com.example.rabie;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TeacherDashboard extends AppCompatActivity {

    private TextView studentCountTextView;
    private TextView courseCountTextView;
    private TextView quizCountTextView;

    private TextView teacherTextView;

    private DatabaseReference databaseReference, teacherReference;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.dashboard);

        studentCountTextView = findViewById(R.id.studentCountTextView);
        courseCountTextView = findViewById(R.id.courseCountTextView);
        quizCountTextView = findViewById(R.id.quizCountTextView);
        //surveyCountTextView = findViewById(R.id.surveyCountTextView);
        ImageView notificationIcon = findViewById(R.id.notificationIcon);

        databaseReference = FirebaseDatabase.getInstance().getReference();

        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        String userEmail = sharedPreferences.getString("userEmail", "");
        String teacherName = sharedPreferences.getString("teacherName", "");
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("teacherName", teacherName);
        editor.apply();
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

        getStatistics(teacherName);
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Courses");
        teacherReference = FirebaseDatabase.getInstance().getReference("teachers");
        teacherReference.orderByChild("email").equalTo(userEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String teacherName = snapshot.child("teacherName").getValue(String.class);
                        String userCourse = snapshot.child("course").getValue(String.class);
                        teacherTextView = findViewById(R.id.textVie);
                        teacherTextView.setText(teacherName);
                        TextView userNameTextView = headerView.findViewById(R.id.user_name);
                        userNameTextView.setText(teacherName);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("teacherName", teacherName);
                        editor.apply();

                    }
                } else {
                    Toast.makeText(TeacherDashboard.this, "Teacher not found!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(TeacherDashboard.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.nav_course_form:
                        Intent coursForm = new Intent(TeacherDashboard.this, Create.class);
                        startActivity(coursForm);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_form:
                        Intent formQuiz = new Intent(TeacherDashboard.this, CreateQuizActivity.class);
//                        formQuiz.putExtra("teacherName", teacher.getText().toString());
//                        formQuiz.putExtra("userEmail", userEmail);
                        startActivity(formQuiz);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.student:
                        // Extract the text from the TextView and pass it as a String extra
                        Intent student = new Intent(TeacherDashboard.this, StudentParticipated.class);

                        startActivity(student);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_quiz:
                        Intent quizList = new Intent(TeacherDashboard.this, QuizListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(quizList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.nav_course_list:
                        Intent courseList = new Intent(TeacherDashboard.this, CourseListActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(courseList);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Message:
                        Intent message = new Intent(TeacherDashboard.this, TeacherMessage.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(message);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.sondage:
                        Intent sondage = new Intent(TeacherDashboard.this, SondageActivity.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(sondage);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.pie:
                        Intent pie = new Intent(TeacherDashboard.this, SondageAnalysis.class);
//                        student.putExtra("teacherName", teacher.getText().toString());
//                        student.putExtra("userEmail", userEmail);
                        startActivity(pie);
                        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                        break;
                    case R.id.Logout:
                        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear().apply();
                        FirebaseAuth.getInstance().signOut();
                        Intent logoutIntent = new Intent(TeacherDashboard.this, LoginTeacher.class);
                        logoutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(logoutIntent);
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

                        finish();
                        break;

                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations");
                reclamationRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ImageView redicon = findViewById(R.id.redCircleIcon);

                        if (dataSnapshot.exists()) {
                            boolean hasPendingReclamations = false;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                String status = snapshot.child("status").getValue(String.class);
                                if (status != null && status.equals("pending")) {
                                    hasPendingReclamations = true;
                                    break;
                                }
                            }

                            if (hasPendingReclamations) {
                                //ImageView redicon = findViewById(R.id.redCircleIcon);
                                redicon.setVisibility(View.VISIBLE);
                                showReclamationsDropdown(dataSnapshot);
                            } else {
                                //ImageView redicon = findViewById(R.id.redCircleIcon);

                                redicon.setVisibility(View.GONE);
                                Toast.makeText(TeacherDashboard.this, "No new reclamations", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            //ImageView redicon = findViewById(R.id.redCircleIcon);

                            redicon.setVisibility(View.GONE);
                            Toast.makeText(TeacherDashboard.this, "No new reclamations", Toast.LENGTH_SHORT).show();
                        }
                    }


                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(TeacherDashboard.this, "Database error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

    }

    private void getStatistics(String teacherName) {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", Context.MODE_PRIVATE);
        teacherName = sharedPreferences.getString("teacherName", "");
        DatabaseReference studentsRef = databaseReference.child("participated");
        Query studentsQuery = studentsRef.orderByChild("teacherName").equalTo(teacherName);
        studentsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long studentCount = dataSnapshot.getChildrenCount();
                studentCountTextView.setText(String.valueOf(studentCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference coursesRef = databaseReference.child("Courses");
        Query coursesQuery = coursesRef.orderByChild("teacherName").equalTo(teacherName);
        coursesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long courseCount = dataSnapshot.getChildrenCount();
                courseCountTextView.setText(String.valueOf(courseCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        DatabaseReference quizRef = databaseReference.child("quizzes");
        Query quizQuery = quizRef.orderByChild("teacherName").equalTo(teacherName);
        quizQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long quizCount = dataSnapshot.getChildrenCount();
                quizCountTextView.setText(String.valueOf(quizCount));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("MenuItemClicked", "Item clicked: " + item.getItemId());

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReclamationsDropdown(DataSnapshot dataSnapshot) {
        List<DataSnapshot> pendingReclamations = new ArrayList<>();
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String status = snapshot.child("status").getValue(String.class);
            if (status != null && status.equals("pending")) {
                pendingReclamations.add(snapshot);
            }
        }

        if (pendingReclamations.isEmpty()) {
            Toast.makeText(TeacherDashboard.this, "No new pending reclamations", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(TeacherDashboard.this);
        builder.setTitle("Reclamations");

        ReclamationAdapter adapter = new ReclamationAdapter(TeacherDashboard.this, R.layout.reclamation_item, pendingReclamations);
        builder.setAdapter(adapter, null);

        AlertDialog alertDialog = builder.create();
        alertDialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
        alertDialog.show();
    }


    private class ReclamationAdapter extends ArrayAdapter<DataSnapshot> {
        private Context context;
        private int resource;
        private List<DataSnapshot> reclamationSnapshots;

        public ReclamationAdapter(@NonNull Context context, int resource, @NonNull List<DataSnapshot> reclamationSnapshots) {
            super(context, resource, reclamationSnapshots);
            this.context = context;
            this.resource = resource;
            this.reclamationSnapshots = reclamationSnapshots;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(resource, parent, false);
            }

            DataSnapshot reclamationSnapshot = reclamationSnapshots.get(position);

            String userName = reclamationSnapshot.child("userName").getValue(String.class);
            String message = reclamationSnapshot.child("message").getValue(String.class);

            TextView userNameTextView = convertView.findViewById(R.id.userNameTextView);
            TextView messageTextView = convertView.findViewById(R.id.messageTextView);
            userNameTextView.setText(userName);
            messageTextView.setText(message);

            Button approveButton = convertView.findViewById(R.id.approveButton);
            approveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String reclamationKey = reclamationSnapshot.getKey();
                    DatabaseReference reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations").child(reclamationKey);
                    reclamationRef.child("status").setValue("Approved").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(TeacherDashboard.this, "Reclamation approved", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(TeacherDashboard.this, "Failed to approve reclamation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            return convertView;


        }
    }
}
