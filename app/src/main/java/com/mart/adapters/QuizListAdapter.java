package com.mart.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.mart.R;
import com.mart.models.QuestionModel;
import java.util.List;

public class QuizListAdapter extends RecyclerView.Adapter<QuizListAdapter.MyViewHolder> {
    private List<QuestionModel> questionList;

    public QuizListAdapter(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QuestionModel question = questionList.get(position);
        holder.quizTitleText.setText(question.getTitle());
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quizTitleText;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitleText = itemView.findViewById(R.id.quiz_title_text);
        }
    }
}
