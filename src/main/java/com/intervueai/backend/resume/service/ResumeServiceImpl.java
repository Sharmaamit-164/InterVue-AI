package com.intervueai.backend.resume.service;

import com.intervueai.backend.resume.dto.ResumeResponse;
import com.intervueai.backend.resume.dto.ResumeUploadResponse;
import com.intervueai.backend.resume.entity.Resume;
import com.intervueai.backend.resume.parser.PdfResumeParser;
import com.intervueai.backend.resume.repository.ResumeRepository;
import com.intervueai.backend.user.entity.User;
import com.intervueai.backend.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeServiceImpl implements ResumeService {

    private final ResumeRepository resumeRepository;
    private final UserRepository userRepository;
    private final PdfResumeParser pdfResumeParser;

    public ResumeServiceImpl(
            ResumeRepository resumeRepository,
            UserRepository userRepository,
            PdfResumeParser pdfResumeParser
    ) {
        this.resumeRepository = resumeRepository;
        this.userRepository = userRepository;
        this.pdfResumeParser = pdfResumeParser;
    }

    @Override
    @Transactional
    public ResumeUploadResponse uploadResume(
            String email,
            MultipartFile file
    ) {

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file cannot be empty"
            );
        }

        // Validate PDF
        if (!"application/pdf".equalsIgnoreCase(file.getContentType())) {
            throw new IllegalArgumentException(
                    "Only PDF files are allowed"
            );
        }

        try {

            // Read actual PDF file
            byte[] fileData = file.getBytes();

            // Extract text from PDF
            String parsedText =
                    pdfResumeParser.parse(file.getInputStream());

            // Create Resume entity
            Resume resume = new Resume();

            resume.setUser(user);
            resume.setFileName(file.getOriginalFilename());
            resume.setFileType(file.getContentType());
            resume.setFileData(fileData);
            resume.setParsedText(parsedText);
            resume.setUploadedAt(LocalDateTime.now());

            // Save PDF + metadata + parsed text
            Resume savedResume =
                    resumeRepository.save(resume);

            // Return response
            return new ResumeUploadResponse(
                    savedResume.getId(),
                    savedResume.getFileName(),
                    "Resume uploaded successfully",
                    savedResume.getUploadedAt()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read resume file",
                    e
            );
        }
    }

    @Override
    public List<ResumeResponse> getMyResumes(
            String email
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return resumeRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    @Override
    public ResumeResponse getMyResume(
            String email,
            Long resumeId
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        return convertToResponse(resume);
    }

    @Override
    @Transactional
    public void deleteMyResume(
            String email,
            Long resumeId
    ) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));

        resumeRepository.delete(resume);
    }

    private ResumeResponse convertToResponse(
            Resume resume
    ) {

        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getParsedText(),
                resume.getUploadedAt()
        );
    }
}