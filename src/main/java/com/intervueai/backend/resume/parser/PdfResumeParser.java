package com.intervueai.backend.resume.parser;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfResumeParser implements ResumeParser {

    @Override
    public String parse(InputStream inputStream) {

        try {
            byte[] pdfBytes = inputStream.readAllBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                PDFTextStripper textStripper = new PDFTextStripper();

                return textStripper.getText(document).trim();
            }

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to parse PDF resume",
                    e
            );
        }
    }
}