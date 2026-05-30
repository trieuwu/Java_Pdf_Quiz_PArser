package com.quiz.model;
import java.util.List;
public class Question{
    private String source;
    private String content;
    private List<String> options;
    private String correctAnswer;
    public Question(String source,String content,List<String> options,String correctAnswer){
        this.source=source;
        this.content=content;
        this.options=options;
        this.correctAnswer=correctAnswer;
    }
    public String getSource(){
        return source;
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
    public void setCorrectAnswer(String correctAnswer){
        this.correctAnswer=correctAnswer;
    }
}