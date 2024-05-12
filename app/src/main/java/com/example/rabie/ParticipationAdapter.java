package com.example.rabie;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.List;
public class ParticipationAdapter  extends ArrayAdapter<Student> {


        private Context context;
        private int resource;
        private List<Student> studentList;

        public ParticipationAdapter(Context context, int resource, List<Student> studentList) {
            super(context, resource, studentList);
            this.context = context;
            this.resource = resource;
            this.studentList = studentList;
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

            studentNameTextView.setText(student.getName());
            if (student.getScore() != -1) {
                scoreTextView.setText(String.valueOf(student.getScore()));
            } else {
                scoreTextView.setText("X");
                scoreTextView.setTextColor(ContextCompat.getColor(context, android.R.color.holo_red_dark));

            }

            return listItem;
        }
    }
