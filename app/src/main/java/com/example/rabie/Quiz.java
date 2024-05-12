package com.example.rabie;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Quiz extends AppCompatActivity {

    private TextView questionTextView,scoreResultTextView;
    private RadioGroup optionsRadioGroup;
    private Button checkButton;
    private Button nextButton;
    private Button submitScoreButton;
    private TextView scoreTextView;

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private boolean isFinalQuestion = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_quiz);
        Intent intent = getIntent();

        String teacherName = intent.getStringExtra("teacherName");
        scoreTextView = findViewById(R.id.scoreTextView);
        questionTextView = findViewById(R.id.questionTextView);
        optionsRadioGroup = findViewById(R.id.optionsRadioGroup);
        checkButton = findViewById(R.id.checkButton);
        nextButton = findViewById(R.id.nextButton);
        submitScoreButton = findViewById(R.id.submitScoreButton);
        scoreResultTextView = findViewById(R.id.scoreResultTextView);
        loadQuestionsFromFirebase(teacherName);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer();
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNextQuestion();
            }
        });

        submitScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitScoreToFirebase();
            }
        });
    }

    private void loadQuestionsFromFirebase(String teacherName) {
        DatabaseReference questionsRef = FirebaseDatabase.getInstance().getReference("quizzes");
        questionsRef.orderByChild("teacherName").equalTo(teacherName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                questionList = new ArrayList<>();
                for (DataSnapshot questionSnapshot : dataSnapshot.getChildren()) {
                    Question question = questionSnapshot.getValue(Question.class);
                    questionList.add(question);
                }
                showNextQuestion();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(Quiz.this, "Failed to load questions.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showNextQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            Question question = questionList.get(currentQuestionIndex);
            questionTextView.setText(question.getQuestionText());
            optionsRadioGroup.removeAllViews();
            for (int i = 0; i < question.getOptions().size(); i++) {
                RadioButton radioButton = new RadioButton(this);
                radioButton.setText(question.getOptions().get(i));
                optionsRadioGroup.addView(radioButton);
            }
            scoreTextView.setText("Score: " + score);
            checkButton.setVisibility(View.VISIBLE);
            nextButton.setVisibility(View.GONE);
            submitScoreButton.setVisibility(View.GONE);
            resetRadioButtons();
            currentQuestionIndex++;
        } else {
            checkButton.setVisibility(View.GONE);
            nextButton.setVisibility(View.GONE);
            submitScoreButton.setVisibility(View.VISIBLE);
            isFinalQuestion = true;
            int totalQuestions = questionList.size();
            double scorePercentage = (double) score / totalQuestions * 100;

            TextView scoreResultTextView = findViewById(R.id.scoreResultTextView);
            scoreResultTextView.setText("Your Score: " + score + "/" + totalQuestions);
            CardView scoreCardView = findViewById(R.id.scoreCardView);
            scoreCardView.setVisibility(View.VISIBLE);

            if (scorePercentage >= 80) {
                TextView congratsTextView = findViewById(R.id.congratsTextView);
                congratsTextView.setVisibility(View.VISIBLE);
                TextView failTextView = findViewById(R.id.failTextView);
                failTextView.setVisibility(View.GONE);
            } else {
                TextView failTextView = findViewById(R.id.failTextView);
                failTextView.setVisibility(View.VISIBLE);
                TextView congratsTextView = findViewById(R.id.congratsTextView);
                congratsTextView.setVisibility(View.GONE);
            }

        }
    }

    private void checkAnswer() {
        RadioButton selectedRadioButton = findViewById(optionsRadioGroup.getCheckedRadioButtonId());
        if (selectedRadioButton != null) {
            int selectedOptionIndex = optionsRadioGroup.indexOfChild(selectedRadioButton);
            Question currentQuestion = questionList.get(currentQuestionIndex - 1);
            for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
                RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(i);
                radioButton.setEnabled(false);
                if (i == currentQuestion.getCorrectOptionIndex()) {
                    radioButton.setTextColor(Color.GREEN);
                } else if (i == selectedOptionIndex) {
                    radioButton.setTextColor(Color.RED);
                }
            }
            if (selectedOptionIndex == currentQuestion.getCorrectOptionIndex()) {
                score++;
            }
            if (!isFinalQuestion) {
                checkButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.VISIBLE);
            } else {
                checkButton.setVisibility(View.GONE);
                nextButton.setVisibility(View.GONE);
                submitScoreButton.setVisibility(View.VISIBLE);
            }
        } else {
            Toast.makeText(this, "Please select an option.", Toast.LENGTH_SHORT).show();
        }
    }


    private void resetRadioButtons() {
        for (int i = 0; i < optionsRadioGroup.getChildCount(); i++) {
            RadioButton radioButton = (RadioButton) optionsRadioGroup.getChildAt(i);
            radioButton.setTextColor(Color.BLACK);
            radioButton.setEnabled(true);
            radioButton.setChecked(false);
        }
    }

    private void submitScoreToFirebase() {
        SharedPreferences sharedPreferences = getSharedPreferences("userPrefs", MODE_PRIVATE);
        String userName = sharedPreferences.getString("fullName", "");
        //String userName = Singleton.getInstance().getUserName();
        Intent intent = getIntent();

        String teacherName = intent.getStringExtra("teacherName");
        if (userName != null) {
            DatabaseReference scoresRef = FirebaseDatabase.getInstance().getReference("scores");
            Score score = new Score(userName,teacherName, this.score);
            scoresRef.push().setValue(score);
            Toast.makeText(this, "Score submitted successfully.", Toast.LENGTH_SHORT).show();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

            finish();
        } else {
            Toast.makeText(this, "User name not found.", Toast.LENGTH_SHORT).show();
        }
    }


}
