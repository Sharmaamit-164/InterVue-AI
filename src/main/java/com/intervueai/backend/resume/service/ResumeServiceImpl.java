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

import java.io.ByteArrayInputStream;
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

    // =========================================================
    // UPLOAD RESUME
    // =========================================================

    @Override
    @Transactional
    public ResumeUploadResponse uploadResume(
            String email,
            MultipartFile file
    ) {

        // 1. Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // 2. Check file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException(
                    "Resume file cannot be empty"
            );
        }

        // 3. Check PDF
        if (file.getContentType() == null ||
                !"application/pdf".equalsIgnoreCase(
                        file.getContentType()
                )) {

            throw new IllegalArgumentException(
                    "Only PDF files are allowed"
            );
        }

        try {

            // 4. Read complete PDF into byte array
            byte[] fileData = file.getBytes();

            // 5. Parse PDF text
            String parsedText = pdfResumeParser.parse(
                    new ByteArrayInputStream(fileData)
            );

            // 6. Create Resume entity
            Resume resume = new Resume();

            resume.setUser(user);

            resume.setFileName(
                    file.getOriginalFilename()
            );

            resume.setFileType(
                    file.getContentType()
            );

            // Store actual PDF inside PostgreSQL
            resume.setFileData(fileData);

            // Store extracted resume text
            resume.setParsedText(parsedText);

            resume.setUploadedAt(
                    LocalDateTime.now()
            );

            // 7. Save everything to PostgreSQL
            Resume savedResume =
                    resumeRepository.save(resume);

            // 8. Return response
            return new ResumeUploadResponse(
                    savedResume.getId(),
                    savedResume.getFileName(),
                    "Resume uploaded successfully",
                    savedResume.getUploadedAt()
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read or parse resume file",
                    e
            );
        }
    }

    // =========================================================
    // GET ALL MY RESUMES
    // =========================================================

    @Override
    @Transactional
    public List<ResumeResponse> getMyResumes(
            String email
    ) {

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Get only this user's resumes
        return resumeRepository.findByUser(user)
                .stream()
                .map(this::convertToResponse)
                .toList();
    }

    // =========================================================
    // GET ONE RESUME
    // =========================================================

    @Override
    @Transactional
    public ResumeResponse getMyResume(
            String email,
            Long resumeId
    ) {

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Find resume belonging to this user
        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found")
                );

        return convertToResponse(resume);
    }

    // =========================================================
    // DELETE RESUME
    // =========================================================

    @Override
    @Transactional
    public void deleteMyResume(
            String email,
            Long resumeId
    ) {

        // Find logged-in user
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        // Find resume belonging to this user
        Resume resume = resumeRepository
                .findByIdAndUser(resumeId, user)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found")
                );

        // Delete resume
        resumeRepository.delete(resume);
    }

    // =========================================================
    // CONVERT ENTITY -> RESPONSE
    // =========================================================

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