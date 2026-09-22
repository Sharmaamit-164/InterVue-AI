package com.intervueai.backend.ai.speech.controller;

import com.intervueai.backend.ai.speech.SpeechService;
import com.intervueai.backend.ai.speech.dto.TranscriptionResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/speech")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Speech API", description = "Endpoints for speech-to-text audio transcription")
public class SpeechController {

    private final SpeechService speechService;

    public SpeechController(SpeechService speechService) {
        this.speechService = speechService;
    }

    @Operation(
            summary = "Transcribe Candidate Audio Answer",
            description = "Transcribes a recorded audio file (e.g. webm/wav/mp3) from the candidate's microphone into text."
    )
    @PostMapping(value = "/transcribe", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TranscriptionResponse> transcribeAudio(
            @RequestParam("file") MultipartFile file
    ) {
        TranscriptionResponse response = speechService.transcribeAudio(file);
        return ResponseEntity.ok(response);
    }
}
