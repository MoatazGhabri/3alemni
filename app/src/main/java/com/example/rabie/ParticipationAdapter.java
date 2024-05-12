package com.example.rabie;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
public class ParticipationAdapter extends ArrayAdapter<Student> {

    private Context context;
    private int resource;
    private List<Student> studentList;
    private String teacherName;
    public ParticipationAdapter(Context context, int resource, List<Student> studentList, String teacherName) {
        super(context, resource, studentList);
        this.context = context;
        this.resource = resource;
        this.studentList = studentList;
        this.teacherName = teacherName;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            listItem = inflater.inflate(resource, parent, false);
        }

        Student student = studentList.get(position);

        TextView studentNameTextView = listItem.findViewById(R.id.studentNameTextView);
        TextView scoreTextView = listItem.findViewById(R.id.scoreTextView1);
        ImageView deleteButton = listItem.findViewById(R.id.delete);

        studentNameTextView.setText(student.getName());
        if (student.getScore() != -1) {
            scoreTextView.setText(String.valueOf(student.getScore()));
        } else {
            scoreTextView.setText("X");
            scoreTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));
        }

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference().child("scores");

                scoresRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot scoreData : snapshot.getChildren()) {
                            String studentName = scoreData.child("userName").getValue(String.class);
                            if (studentName.equals(student.getName())) {
                                scoreData.getRef().removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            studentList.remove(position);
                                            notifyDataSetChanged();
                                            Toast.makeText(context, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(context, "Failed to delete user", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e(TAG, "Failed to read score value.", error.toException());
                    }
                });
            }
        });

        return listItem;
    }
}
