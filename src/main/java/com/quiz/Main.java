package com.quiz;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.quiz.model.Question;
import com.quiz.service.PdfService;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        //tim file root, ko thay thi tao file moi
        File rootDirectory = new File("pdf_inputs");
        if (!rootDirectory.exists())
            rootDirectory.mkdir();

        // method reference de loc các THU MUC con, bo qua cac file rieng le
        File[] subjectFolders = rootDirectory.listFiles(File::isDirectory);
        if (subjectFolders == null || subjectFolders.length == 0) {
            System.out.println("No subject folders found in pdf_inputs");
            return;
        }

        //set up cong cu loc va xu ly
        PdfService pdfParser = new PdfService();
        Gson jsonConverter = new GsonBuilder().setPrettyPrinting().create();

        //loc xu li tung mon hoc bat ke ten mon hoc (cho het ve lowercase)
        for (File folder : subjectFolders) {
            String subjectName = folder.getName();
            File[] pdfFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".pdf"));

            if (pdfFiles == null || pdfFiles.length == 0)
                continue;

            List<Question> subjectQuestions = new ArrayList<>();
            try {
                for (File file : pdfFiles)
                    subjectQuestions.addAll(pdfParser.extractQuestions(file.getAbsolutePath()));

                try (FileWriter fileWriter = new FileWriter(subjectName + ".json")) {
                    jsonConverter.toJson(subjectQuestions, fileWriter);
                }

                System.out.println("Successfully generated " + subjectName + ".json with " + subjectQuestions.size() + " questions");
            } catch (Exception exception) { //try-catch error
                exception.printStackTrace();
            }
        }
    }
}