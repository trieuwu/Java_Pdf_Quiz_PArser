package com.quiz.model;

import java.util.List;

public class Question{
    private String content;
    private List<String> options;
    private String correctAnswer;

    public Question(String content,List<String> options,String correctAnswer){
        this.content=content;
        this.options=options;
        this.correctAnswer=correctAnswer;
    }

    public String getContent(){
        return content;
    }

    public List<String> getOptions(){
        return options;
    }

    public String getCorrectAnswer(){
        return correctAnswer;
    }
}