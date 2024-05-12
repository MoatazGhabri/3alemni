package com.example.rabie;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.QuizViewHolder> {

    private Context context;
    private List<Question> questionList;

    public QuizAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_quiz, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        Question question = questionList.get(position);
        holder.bind(question);
        holder.deleteIcon.setOnClickListener(view -> deleteQuestion(question));
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public class QuizViewHolder extends RecyclerView.ViewHolder {

        private TextView questionTextView;
        private ImageView deleteIcon;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            questionTextView = itemView.findViewById(R.id.questionTextView);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
        }

        public void bind(Question question) {
            questionTextView.setText(question.getQuestionText());
        }
    }

    private void deleteQuestion(Question question) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Do you really want to delete this quiz?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    String questionText = question.getQuestionText();
                    if (questionText != null) {
                        DatabaseReference quizzesRef = FirebaseDatabase.getInstance().getReference("quizzes");
                        quizzesRef.orderByChild("questionText").equalTo(questionText).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                    snapshot.getRef().removeValue();
                                }
                                int position = questionList.indexOf(question);
                                if (position != -1) {
                                    questionList.remove(position);
                                    notifyItemRemoved(position);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                Log.e("QuizAdapter", "Failed to delete quiz: " + databaseError.getMessage());
                            }
                        });
                    } else {
                        Log.e("QuizAdapter", "Question text is null");
                    }
                })
                .setNegativeButton("No", (dialog, which) -> {
                })
                .show();
    }

}

