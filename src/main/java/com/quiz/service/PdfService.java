package com.quiz.service;

import com.quiz.model.Question;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfService{
    public List<Question> extractQuestions(String path)throws Exception{
        List<Question> questions=new ArrayList<>();
        PDDocument doc=PDDocument.load(new File(path));
        PDFTextStripper stripper=new PDFTextStripper();
        for(int i=0;i<doc.getNumberOfPages();i++){
            PDPage page=doc.getPage(i);
            stripper.setStartPage(i+1);
            stripper.setEndPage(i+1);
            String pageText=stripper.getText(doc);
            List<String> highlights=extractPageHighlights(page);
            questions.addAll(parsePageText(pageText,highlights));
        }
        doc.close();
        return questions;
    }

    private List<String> extractPageHighlights(PDPage page)throws Exception{
        List<String> highlights=new ArrayList<>();
        List<PDAnnotation> annotations=page.getAnnotations();
        PDFTextStripperByArea stripper=new PDFTextStripperByArea();
        int count=0;
        for(PDAnnotation annot:annotations){
            if(annot.getSubtype().equals("Highlight")){
                org.apache.pdfbox.pdmodel.common.PDRectangle pdRect=annot.getRectangle();
                float x=pdRect.getLowerLeftX();
                float y=page.getMediaBox().getHeight()-pdRect.getUpperRightY();
                Rectangle2D rect=new Rectangle2D.Float(x,y,pdRect.getWidth(),pdRect.getHeight());
                stripper.addRegion("region"+count,rect);
                count++;
            }
        }
        stripper.extractRegions(page);
        for(int j=0;j<count;j++){
            String text=stripper.getTextForRegion("region"+j);
            if(text!=null&&!text.trim().isEmpty())
                highlights.add(text.trim());
        }
        return highlights;
    }

    private List<Question> parsePageText(String pageText,List<String> highlights){
        List<Question> list=new ArrayList<>();
        String[] lines=pageText.split("\n");
        String currentQuestion=null;
        List<String> currentOptions=new ArrayList<>();
        String correctAnswer="";
        for(String line:lines){
            line=line.trim();
            if(line.isEmpty())
                continue;
            if(line.startsWith("Câu")||line.matches("^\\d+\\..*")){
                if(currentQuestion!=null)
                    list.add(new Question(currentQuestion,new ArrayList<>(currentOptions),correctAnswer));
                currentQuestion=line;
                currentOptions.clear();
                correctAnswer="";
            }else if(line.matches("^[A-D]\\..*")||line.matches("^[a-d]\\..*")){
                currentOptions.add(line);
                for(String highlight:highlights){
                    if(line.contains(highlight))
                        correctAnswer=line;
                }
            }
        }
        if(currentQuestion!=null)
            list.add(new Question(currentQuestion,currentOptions,correctAnswer));
        return list;
    }
}