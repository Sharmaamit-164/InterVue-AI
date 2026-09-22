package com.intervueai.backend.ai.speech;

import com.intervueai.backend.ai.speech.dto.TranscriptionResponse;

public interface SpeechProvider {

    /**
     * Name of the speech-to-text provider (e.g. "WHISPER", "DEEPGRAM", "MOCK").
     */
    String getProviderName();

    /**
     * Transcribes raw audio bytes into text.
     *
     * @param audioBytes  raw audio payload
     * @param contentType MIME type of audio (e.g. audio/webm, audio/wav, audio/mp3)
     * @return transcribed text and metadata
     */
    TranscriptionResponse transcribe(byte[] audioBytes, String contentType);
}
