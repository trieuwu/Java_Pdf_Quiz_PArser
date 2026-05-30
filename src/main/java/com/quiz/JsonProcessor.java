package com.quiz;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.quiz.model.JsQuestion;
import com.quiz.model.Question;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class JsonProcessor{
    public void processAndShuffleFile(File jsonFile,File outputFolder){
        Gson gsonConverter=new GsonBuilder().setPrettyPrinting().create();
        Type questionListType=new TypeToken<List<Question>>(){}.getType();
        try{
            List<Question> questionsList;
            try(FileReader fileReader=new FileReader(jsonFile)){
                questionsList=gsonConverter.fromJson(fileReader,questionListType);
            }
            if(questionsList==null||questionsList.isEmpty())return;
            List<JsQuestion> jsQuestions=new ArrayList<>();
            int counter=1;
            for(Question question:questionsList){
                List<String> optionsList=question.getOptions();
                if(optionsList==null||optionsList.size()<4)continue;
                for(int i=0;i<optionsList.size();i++)
                    optionsList.set(i,optionsList.get(i).replaceAll("^(?i)([a-d][:.)\\s-]|\\b[a-d]\\b)","").trim());
                String currentCorrectAnswer=question.getCorrectAnswer();
                if(currentCorrectAnswer!=null)
                    currentCorrectAnswer=currentCorrectAnswer.replaceAll("^(?i)([a-d][:.)\\s-]|\\b[a-d]\\b)","").trim();
                Collections.shuffle(optionsList);
                String correctLetter="";
                if(optionsList.get(0).equals(currentCorrectAnswer))correctLetter="a";
                else if(optionsList.get(1).equals(currentCorrectAnswer))correctLetter="b";
                else if(optionsList.get(2).equals(currentCorrectAnswer))correctLetter="c";
                else if(optionsList.get(3).equals(currentCorrectAnswer))correctLetter="d";
                String questionContent=question.getContent().replaceAll("^Câu\\s*\\d+\\s*:\\s*","").trim();
                jsQuestions.add(new JsQuestion(counter++,questionContent,optionsList.get(0),optionsList.get(1),optionsList.get(2),optionsList.get(3),correctLetter));
            }
            File finalOutputFile=new File(outputFolder,jsonFile.getName().replace(".json","_shuffled.json"));
            try(FileWriter fileWriter=new FileWriter(finalOutputFile)){
                gsonConverter.toJson(jsQuestions,fileWriter);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }
}