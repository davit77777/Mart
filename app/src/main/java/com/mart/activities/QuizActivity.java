package com.mart.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.mart.R;
import com.mart.models.QuestionModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity {
    private TextView questionTextView;
    private Button option1;
    private Button option2;
    private Button option3;
    private Button option4;
    private Button nextButton;
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private List<QuestionModel> questionList;
    private int currentQuestionIndex = 0;
    private int score = 0;
    private String selectedAnswer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        questionTextView = findViewById(R.id.question_textview);
        option1 = findViewById(R.id.btnOption1);
        option2 = findViewById(R.id.btnOption2);
        option3 = findViewById(R.id.btnOption3);
        option4 = findViewById(R.id.btnOption4);
        nextButton = findViewById(R.id.btnNext);
        progressBar = findViewById(R.id.question_progress_indicator);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        questionList = new ArrayList<>();

        fetchQuestions();

        View.OnClickListener answerClickListener = v -> {
            selectedAnswer = ((Button) v).getText().toString();
            resetOptionsBackground();
            v.setBackgroundColor(Color.parseColor("#FFD700"));
            nextButton.setEnabled(true);
        };

        option1.setOnClickListener(answerClickListener);
        option2.setOnClickListener(answerClickListener);
        option3.setOnClickListener(answerClickListener);
        option4.setOnClickListener(answerClickListener);

        nextButton.setEnabled(false);

        nextButton.setOnClickListener(v -> {
            showCorrectAnswer();
            enableOptions(false);
            nextButton.setEnabled(false);

            new Handler().postDelayed(() -> {
                currentQuestionIndex++;
                displayQuestion();
            }, 2000);
        });
    }

    private void fetchQuestions() {
        db.collection("questions").limit(10).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DocumentSnapshot document : task.getResult()) {
                    QuestionModel question = document.toObject(QuestionModel.class);
                    questionList.add(question);
                }
                Collections.shuffle(questionList);
                displayQuestion();
            }
        });
    }

    private void displayQuestion() {
        if (currentQuestionIndex < questionList.size()) {
            resetOptionsBackground();
            enableOptions(true);
            nextButton.setEnabled(false);

            progressBar.setProgress((int) (((double) (currentQuestionIndex + 1) / questionList.size()) * 100));
            QuestionModel currentQuestion = questionList.get(currentQuestionIndex);
            questionTextView.setText(currentQuestion.getTitle());
            option1.setText(currentQuestion.getOption1());
            option2.setText(currentQuestion.getOption2());
            option3.setText(currentQuestion.getOption3());
            option4.setText(currentQuestion.getOption4());
            selectedAnswer = null;
        } else {
            endQuiz();
        }
    }

    private void resetOptionsBackground() {
        int color = getResources().getColor(R.color.gray);
        option1.setBackgroundColor(color);
        option2.setBackgroundColor(color);
        option3.setBackgroundColor(color);
        option4.setBackgroundColor(color);
    }

    private void enableOptions(boolean enable) {
        option1.setEnabled(enable);
        option2.setEnabled(enable);
        option3.setEnabled(enable);
        option4.setEnabled(enable);
    }

    private void showCorrectAnswer() {
        QuestionModel currentQuestion = questionList.get(currentQuestionIndex);
        String correctAnswer = currentQuestion.getCorrectAns();
        if (option1.getText().toString().equals(correctAnswer)) {
            option1.setBackgroundColor(Color.GREEN);
        } else if (option2.getText().toString().equals(correctAnswer)) {
            option2.setBackgroundColor(Color.GREEN);
        } else if (option3.getText().toString().equals(correctAnswer)) {
            option3.setBackgroundColor(Color.GREEN);
        } else if (option4.getText().toString().equals(correctAnswer)) {
            option4.setBackgroundColor(Color.GREEN);
        }

        if (selectedAnswer != null && !selectedAnswer.equals(correctAnswer)) {
            if (option1.getText().toString().equals(selectedAnswer)) {
                option1.setBackgroundColor(Color.RED);
            } else if (option2.getText().toString().equals(selectedAnswer)) {
                option2.setBackgroundColor(Color.RED);
            } else if (option3.getText().toString().equals(selectedAnswer)) {
                option3.setBackgroundColor(Color.RED);
            } else if (option4.getText().toString().equals(selectedAnswer)) {
                option4.setBackgroundColor(Color.RED);
            }
            score = Math.max(score - 5, 0);
        }

        if (selectedAnswer != null && selectedAnswer.equals(correctAnswer)) {
            score += 10;
        }
    }

    private void endQuiz() {
        saveUserScore();
        AlertDialog.Builder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Quiz Finished")
                .setMessage("Your Score: " + score)
                .setPositiveButton("OK", (dialog, which) -> {
                    dialog.dismiss();
                    finish();
                })
                .setCancelable(false)
                .show();
    }

    private void saveUserScore() {
        String userId = user.getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    Long previousScore = document.getLong("score");
                    if (previousScore == null || score > previousScore) {
                        userRef.update("score", score);
                    }
                } else {
                    Map<String, Object> userData = new HashMap<>();
                    userData.put("username", user.getDisplayName());
                    userData.put("score", score);
                    userData.put("visibility", true);

                    userRef.set(userData, SetOptions.merge());
                }
            }
        });
    }
}
