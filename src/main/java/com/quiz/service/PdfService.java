package com.quiz.service;
import com.quiz.model.Question;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfService{

    public List<Question> extractQuestions(String path,String sourceName)throws Exception{
        List<Question> questions=new ArrayList<>();
        PDDocument document=PDDocument.load(new File(path));
        PDFTextStripper textStripper=new PDFTextStripper();
        String fullText=textStripper.getText(document);
        questions.addAll(parseTextByPattern(fullText,sourceName));
        document.close();
        return questions;
    }

    private List<Question> parseTextByPattern(String fullText,String sourceName){
        List<Question> questionsList=new ArrayList<>();
        String[] lines=fullText.split("\n");
        String currentQuestion=null;
        List<String> currentOptions=new ArrayList<>();
        String correctAnswerLetter="";
        String correctFullAnswer="";
        for(String line:lines){
            line=line.trim();
            if(line.isEmpty()||line.startsWith("Giải thích:")||line.contains("Giặt A tại đây")||line.contains("--- PAGE"))
                continue;
            if(line.startsWith("Câu")||line.matches("^\\d+\\..*")){
                if(currentQuestion!=null&&!currentOptions.isEmpty()){
                    for(String option:currentOptions)
                        if(option.startsWith(correctAnswerLetter+"."))
                            correctFullAnswer=option;
                    questionsList.add(new Question(sourceName,currentQuestion,new ArrayList<>(currentOptions),correctFullAnswer));
                }
                currentQuestion=line;
                currentOptions.clear();
                correctAnswerLetter="";
                correctFullAnswer="";
            }else if(line.matches("^[A-D]\\..*")||line.matches("^[a-d]\\..*")){
                currentOptions.add(line);
            }else if(line.startsWith("Đáp án đúng:")){
                correctAnswerLetter=line.replace("Đáp án đúng:","").trim();
            }else{
                if(currentQuestion!=null&&currentOptions.isEmpty()&&!line.startsWith("Đáp án đúng:"))
                    currentQuestion+=" "+line;
                else if(!currentOptions.isEmpty()&&!line.startsWith("Đáp án đúng:")){
                    int lastIndex=currentOptions.size()-1;
                    currentOptions.set(lastIndex,currentOptions.get(lastIndex)+" "+line);
                }
            }
        }
        if(currentQuestion!=null&&!currentOptions.isEmpty()){
            for(String option:currentOptions)
                if(option.startsWith(correctAnswerLetter+"."))
                    correctFullAnswer=option;
            questionsList.add(new Question(sourceName,currentQuestion,currentOptions,correctFullAnswer));
        }
        return questionsList;
    }
}