package com.intervueai.backend.ai.speech;

import com.intervueai.backend.ai.speech.dto.TranscriptionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SpeechService {

    /**
     * Transcribes an uploaded audio file.
     */
    TranscriptionResponse transcribeAudio(MultipartFile audioFile);

    /**
     * Transcribes raw audio bytes with a specified content type.
     */
    TranscriptionResponse transcribeAudioBytes(byte[] audioBytes, String contentType);
}
