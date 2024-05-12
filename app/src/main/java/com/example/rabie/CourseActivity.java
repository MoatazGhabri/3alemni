package com.example.rabie;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class CourseActivity extends AppCompatActivity {

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.course);
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        Log.d("UserNameLog", "UserName: " + userName);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference();

//        String tech = getIntent().getStringExtra("teacherName");
//        String courseDescription = getIntent().getStringExtra("description");
//        String courseName = getIntent().getStringExtra("name");
//        String coursePdfUrl = getIntent().getStringExtra("pdfUrl");
        Intent intent = getIntent();

        String tech = intent.getStringExtra("teacherName");
        String category = intent.getStringExtra("category");
        ArrayList<String> courseNames = intent.getStringArrayListExtra("courseNames");
        ArrayList<String> coursePdfUrls = intent.getStringArrayListExtra("coursePdfUrls");
        Log.d(TAG, "Course Names: " + courseNames);
        Log.d(TAG, "Course PDF URLs: " + coursePdfUrls);
        if ( courseNames != null && coursePdfUrls != null) {

            TextView teacher = findViewById(R.id.Teachername);
            TextView categoryTextView = findViewById(R.id.categoryTextView);



            //TextView coursePdfUrlTextView = findViewById(R.id.coursePdfUrlTextView);
            teacher.setText(tech);
            categoryTextView.setText(category);
            LinearLayout coursesContainer = findViewById(R.id.coursesContainer);
            for (int i = 0; i < courseNames.size(); i++) {

                Button downloadButton = new Button(this);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, 8, 8, 8);
                params.width = LinearLayout.LayoutParams.MATCH_PARENT;
                downloadButton.setLayoutParams(params);
                downloadButton.setText("Download " + courseNames.get(i));

                final int finalI = i;
                downloadButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ContextCompat.checkSelfPermission(CourseActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(CourseActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    REQUEST_STORAGE_PERMISSION);
                        } else {
                            downloadPdf(courseNames.get(finalI), coursePdfUrls.get(finalI));
                        }

                    }
                });
                downloadButton.setBackgroundResource(R.drawable.shape1);
                downloadButton.setTextColor(Color.WHITE);
                downloadButton.setTextSize(16);
                coursesContainer.addView(downloadButton);
            }
        } else {
            Toast.makeText(CourseActivity.this, "Course details not available", Toast.LENGTH_SHORT).show();
        }
        ImageButton reclamationButton = findViewById(R.id.reclamationButton);
        Button quizButton = findViewById(R.id.quiz);
            quizButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String teacherName = getIntent().getStringExtra("teacherName");
                    checkQuizzesExistInFirebase(teacherName);
                }
            });

            reclamationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showReclamationDialog();
                }
            });



        ImageButton backButton = findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            }
        });
        ImageButton sendMessageButton = findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSendMessageModal();
            }
        });
        ImageButton sondageButton = findViewById(R.id.sondageButton);
        sondageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String teacherName = getIntent().getStringExtra("teacherName");
                checkUserSondage(teacherName);
            }
        });
    }

    private void checkUserSondage(String teacherName) {
        String userName = getSharedPreferences("userPrefs", MODE_PRIVATE).getString("fullName", "");
        DatabaseReference sondageAnalysisRef = FirebaseDatabase.getInstance().getReference("sondageanalysis").child(teacherName);

        sondageAnalysisRef.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    fetchSondageDetails(teacherName);
                } else {
                    Toast.makeText(CourseActivity.this, "You have already participated \nin a sondage for this teacher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, "Error retrieving user sondage data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchSondageDetails(String teacherName) {
        DatabaseReference sondageRef = FirebaseDatabase.getInstance().getReference("sondages");

        sondageRef.child(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot firstSondageSnapshot = dataSnapshot.getChildren().iterator().next();
                    String question = firstSondageSnapshot.child("questionText").getValue(String.class);
                    String buttonType = firstSondageSnapshot.child("buttonType").getValue(String.class);
                    List<String> options = new ArrayList<>();
                    for (DataSnapshot optionSnapshot : firstSondageSnapshot.child("options").getChildren()) {
                        options.add(optionSnapshot.getValue(String.class));
                    }
                    if (buttonType != null) {
                        showSondageDialog(question, options, buttonType);
                    }
                } else {
                    Toast.makeText(CourseActivity.this, "No sondage available for this teacher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, "Error retrieving sondage data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSondageDialog(String question, List<String> options, String buttonType) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sondage");
        builder.setMessage(question);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        if (buttonType.equals("Radio Button")) {
            RadioGroup radioGroup = new RadioGroup(this);
            for (String option : options) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(option);
                radioGroup.addView(radioButton);
            }
            layout.addView(radioGroup);
        } else if (buttonType.equals("Check Box")) {
            for (String option : options) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(option);
                layout.addView(checkBox);
            }
        }

        builder.setView(layout);

        builder.setPositiveButton("OK", (dialog, which) -> {

            List<String> selectedOptions = new ArrayList<>();
            if (buttonType.equals("Radio Button")) {
                RadioGroup radioGroup = (RadioGroup) layout.getChildAt(0);
                int selectedRadioButtonId = radioGroup.getCheckedRadioButtonId();
                RadioButton selectedRadioButton = radioGroup.findViewById(selectedRadioButtonId);
                if (selectedRadioButton != null) {
                    selectedOptions.add(selectedRadioButton.getText().toString());
                }
            } else if (buttonType.equals("Check Box")) {
                for (int i = 0; i < layout.getChildCount(); i++) {
                    View view = layout.getChildAt(i);
                    if (view instanceof CheckBox) {
                        CheckBox checkBox = (CheckBox) view;
                        if (checkBox.isChecked()) {
                            selectedOptions.add(checkBox.getText().toString());
                        }
                    }
                }
            }

            sendSondageResponse(question, selectedOptions);
        });

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);
        dialog.show();
    }

    private void sendSondageResponse(String question, List<String> selectedOptions) {
        String teacherName = getIntent().getStringExtra("teacherName");
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        DatabaseReference sondageAnalysisRef = FirebaseDatabase.getInstance().getReference("sondageanalysis").child(teacherName);

        String key = sondageAnalysisRef.push().getKey();
        if (key != null) {
            HashMap<String, Object> responseData = new HashMap<>();
            responseData.put("questionText", question);
            responseData.put("teacherName", teacherName);
            responseData.put("userName", userName);

            responseData.put("selectedOptions", selectedOptions);
            sondageAnalysisRef.child(key).setValue(responseData);
            Toast.makeText(this, "Sondage response sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to send sondage response", Toast.LENGTH_SHORT).show();
        }
    }
    private void showSendMessageModal() {
        View modalView = getLayoutInflater().inflate(R.layout.modal_send_message, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(modalView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);

        dialog.show();

        RecyclerView chatHistoryRecyclerView = modalView.findViewById(R.id.chatHistoryRecyclerView);
        DisplayChatHistory(chatHistoryRecyclerView);

        Button sendMessageButton = modalView.findViewById(R.id.sendMessageButton);
        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText messageEditText = modalView.findViewById(R.id.messageEditText);
                String message = messageEditText.getText().toString().trim();
                if (!message.isEmpty()) {
                    SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);

                    String teacherName = getIntent().getStringExtra("teacherName");
                    String userName = sharedPreferences.getString("fullName", "");

                    sendMessageToTeacher(message, teacherName, userName);
                    messageEditText.setText("");
                    DisplayChatHistory(chatHistoryRecyclerView);
                } else {
                    Toast.makeText(CourseActivity.this, "Please enter a message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void DisplayChatHistory(RecyclerView recyclerView) {
        String teacherName = getIntent().getStringExtra("teacherName");
        String userName = getSharedPreferences("userPrefs", MODE_PRIVATE).getString("fullName", "");

        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats");

        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<Message> sentMessages = new ArrayList<>();
                List<Message> receivedMessages = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    for (DataSnapshot messageSnapshot : snapshot.getChildren()) {
                        Message message = messageSnapshot.getValue(Message.class);
                        if (message.getSenderName().equals(userName) && message.getTeacherName().equals(teacherName)) {
                            sentMessages.add(message);
                        } else if (message.getTeacherName().equals(teacherName) && message.getSenderName().equals(userName)) {
                            receivedMessages.add(message);
                        }
                    }
                }

                Collections.sort(sentMessages, (message1, message2) -> Long.compare(message1.getTimestamp(), message2.getTimestamp()));
                Collections.sort(receivedMessages, (message1, message2) -> Long.compare(message1.getTimestamp(), message2.getTimestamp()));

                List<Message> allMessages = new ArrayList<>();
                allMessages.addAll(sentMessages);
                //allMessages.addAll(receivedMessages);
                //ChatAdapter adapter = new ChatAdapter(sentMessages, userName);

               ChatAdapter adapter = new ChatAdapter(sentMessages, teacherName, userName);
                recyclerView.setAdapter(adapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(CourseActivity.this));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, "Failed to fetch chat history", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void sendMessageToTeacher(String message, String teacherName, String userName) {
        DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("chats").child(teacherName);
        String messageId = chatRef.push().getKey();
        if (messageId != null) {
            Message newMessage = new Message(userName, message, teacherName,"sentMessage", System.currentTimeMillis());
            chatRef.child(messageId).setValue(newMessage);
        } else {
            Toast.makeText(this, "Failed to send message", Toast.LENGTH_SHORT).show();
        }
    }


    private void downloadPdf(String courseName, String pdfUrl) {
        StorageReference pdfRef = mStorageRef.child("pdfs/" + courseName + ".pdf");
        pdfRef.getDownloadUrl().addOnSuccessListener(uri -> {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(uri.toString()));
            request.setTitle(courseName);
            request.setDescription("Downloading PDF");

            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, courseName + ".pdf");

            DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
            if (downloadManager != null) {
                downloadManager.enqueue(request);
                Toast.makeText(CourseActivity.this, "PDF Downloaded Successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(CourseActivity.this, "Download manager not available", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(exception -> {
            Toast.makeText(CourseActivity.this, "Failed to retrieve PDF URL", Toast.LENGTH_SHORT).show();
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                String courseName = getIntent().getStringExtra("name");
                String coursePdfUrl = getIntent().getStringExtra("pdfUrl");
                downloadPdf(courseName, coursePdfUrl);
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkQuizzesExistInFirebase(String teacherName) {
        DatabaseReference quizzesRef = mDatabase.child("quizzes");

        quizzesRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    checkUserScore(teacherName);
                } else {
                    Toast.makeText(CourseActivity.this, "No quizzes available for this teacher", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, "Error retrieving quizzes data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserScore(String teacherName) {
        String userName = getSharedPreferences("userPrefs", MODE_PRIVATE).getString("fullName", "");
        DatabaseReference scoresRef = mDatabase.child("scores").child(teacherName);

        scoresRef.orderByChild("userName").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Toast.makeText(CourseActivity.this, "You have already passed this quiz", Toast.LENGTH_SHORT).show();
                } else {
                    Intent quizIntent = new Intent(CourseActivity.this, Quiz.class);
                    quizIntent.putExtra("teacherName", teacherName);
                    startActivity(quizIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CourseActivity.this, "Error retrieving user score", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showReclamationDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_reclamation, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_container_bg);

        dialog.show();

        EditText reclamationEditText = dialogView.findViewById(R.id.reclamationEditText);
        Button sendButton = dialogView.findViewById(R.id.sendButton);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String reclamationMessage = reclamationEditText.getText().toString().trim();

                if (!reclamationMessage.isEmpty()) {
                    storeReclamation(reclamationMessage);

                    dialog.dismiss();
                } else {
                    Toast.makeText(CourseActivity.this, "Please enter a reclamation message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    private void storeReclamation(String reclamationMessage) {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        String teacherName = getIntent().getStringExtra("teacherName");

        DatabaseReference reclamationRef = FirebaseDatabase.getInstance().getReference("reclamations");

        String key = reclamationRef.push().getKey();
        if (key != null) {
            HashMap<String, Object> reclamationData = new HashMap<>();
            reclamationData.put("userName", userName);
            reclamationData.put("teacherName", teacherName);
            reclamationData.put("message", reclamationMessage);
            reclamationData.put("status", "pending");
            reclamationRef.child(key).setValue(reclamationData);
            Toast.makeText(this, "Reclamation sent successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Failed to send reclamation", Toast.LENGTH_SHORT).show();
        }
    }

}

