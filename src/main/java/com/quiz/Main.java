package com.quiz;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quiz.model.Question;
import com.quiz.service.PdfService;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
public class Main{
    public static void main(String[] args){
        File inputFolder=new File("pdf_inputs");
        if(!inputFolder.exists())inputFolder.mkdir();
        List<File> pdfFiles=new ArrayList<>();
        findPdfFiles(inputFolder,pdfFiles);
        if(pdfFiles.isEmpty()){
            System.out.println("No PDF files found");
            return;
        }
        PdfService pdfService=new PdfService();
        JsonProcessor jsonProcessor=new JsonProcessor();
        Gson gsonConverter=new GsonBuilder().setPrettyPrinting().create();
        try{
            for(File file:pdfFiles){
                String sourceName=file.getName().replace(".pdf","");
                String subjectName=file.getParentFile().getName();
                File outputFolder=new File("pdf_outputs/"+subjectName);
                if(!outputFolder.exists())outputFolder.mkdirs();
                List<Question> extractedQuestions=pdfService.extractQuestions(file.getAbsolutePath(),sourceName);
                File rawJsonFile=new File(outputFolder,sourceName+".json");
                try(FileWriter fileWriter=new FileWriter(rawJsonFile)){
                    gsonConverter.toJson(extractedQuestions,fileWriter);
                }
                System.out.println("Generated "+sourceName+".json in pdf_outputs/"+subjectName);
                jsonProcessor.processAndShuffleFile(rawJsonFile,outputFolder);
                System.out.println("Generated "+sourceName+"_shuffled.json in pdf_outputs/"+subjectName);
            }
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }
    private static void findPdfFiles(File folder,List<File> pdfFiles){
        File[] files=folder.listFiles();
        if(files==null)return;
        for(File file:files){
            if(file.isDirectory())findPdfFiles(file,pdfFiles);
            else if(file.getName().toLowerCase().endsWith(".pdf"))pdfFiles.add(file);
        }
    }
}