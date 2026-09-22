import React, { useState, useEffect, useRef } from 'react';
import { Mic, MicOff, RotateCcw } from 'lucide-react';
import { speechApi } from '../api/speech';

export const AudioRecorder = ({
  onAnswerSubmit,
  disabled = false,
  isListening = false,
  placeholder = "Speak naturally... AI will auto-process your answer when you stop speaking!"
}) => {
  const [isRecording, setIsRecording] = useState(false);
  const [answerText, setAnswerText] = useState('');
  const [recordingTime, setRecordingTime] = useState(0);
  const [isTranscribing, setIsTranscribing] = useState(false);
  const [audioLevel, setAudioLevel] = useState(0);
  const [silenceNotice, setSilenceNotice] = useState('');

  const recognitionRef = useRef(null);
  const timerRef = useRef(null);
  const mediaRecorderRef = useRef(null);
  const audioChunksRef = useRef([]);
  const finalTextRef = useRef('');
  const isListeningRef = useRef(false);
  const silenceTimerRef = useRef(null);
  const audioContextRef = useRef(null);
  const analyserRef = useRef(null);
  const animFrameRef = useRef(null);
  const streamRef = useRef(null);
  const latestAnswerRef = useRef('');

  // Keep refs in sync
  useEffect(() => {
    isListeningRef.current = isRecording;
  }, [isRecording]);

  useEffect(() => {
    latestAnswerRef.current = answerText;
  }, [answerText]);

  const clearSilenceTimer = () => {
    if (silenceTimerRef.current) {
      clearTimeout(silenceTimerRef.current);
      silenceTimerRef.current = null;
    }
    setSilenceNotice('');
  };

  // Reset all state when question changes or disabled becomes true
  useEffect(() => {
    if (disabled) {
      clearSilenceTimer();
      stopRecording();
      setAnswerText('');
      finalTextRef.current = '';
      latestAnswerRef.current = '';
    }
  }, [disabled]);

  const autoSubmitAnswer = (textToSubmit) => {
    clearSilenceTimer();
    stopRecording();

    const text = (textToSubmit || latestAnswerRef.current || finalTextRef.current).trim();
    setAnswerText('');
    finalTextRef.current = '';
    latestAnswerRef.current = '';
    setRecordingTime(0);

    if (text && !disabled) {
      onAnswerSubmit(text);
    }
  };

  // Setup Web Speech API with Automatic Voice Processing
  useEffect(() => {
    const SpeechRecognition = window.SpeechRecognition || window.webkitSpeechRecognition;
    if (SpeechRecognition) {
      const recognition = new SpeechRecognition();
      recognition.continuous = true;
      recognition.interimResults = true;
      recognition.lang = 'en-US';

      recognition.onresult = (event) => {
        if (!isListeningRef.current) return;

        let interim = '';
        for (let i = event.resultIndex; i < event.results.length; ++i) {
          const transcriptPiece = event.results[i][0].transcript;
          if (event.results[i].isFinal) {
            const trimmed = transcriptPiece.trim();
            if (trimmed) {
              finalTextRef.current = finalTextRef.current
                ? `${finalTextRef.current} ${trimmed}`
                : trimmed;
            }
          } else {
            interim += ` ${transcriptPiece.trim()}`;
          }
        }

        const fullText = (
          finalTextRef.current + (interim ? (finalTextRef.current ? ' ' : '') + interim.trim() : '')
        ).trim();

        setAnswerText(fullText);
        latestAnswerRef.current = fullText;

        // AUTOMATIC VOICE PROCESSING:
        // As candidate speaks, reset silence timer.
        // When candidate stops speaking for 2 seconds, automatically submit and process answer!
        if (fullText.length >= 6) {
          clearSilenceTimer();
          setSilenceNotice('Listening to your answer... Will process automatically when you pause.');
          silenceTimerRef.current = setTimeout(() => {
            setSilenceNotice('Finished speaking! Processing answer...');
            autoSubmitAnswer(fullText);
          }, 2000);
        }
      };

      recognition.onerror = (event) => {
        console.warn('Speech recognition notice:', event.error);
        if (event.error === 'not-allowed') {
          stopRecording();
        }
      };

      recognition.onend = () => {
        if (isListeningRef.current) {
          try {
            recognition.start();
          } catch (e) {}
        }
      };

      recognitionRef.current = recognition;
    }

    return () => {
      clearSilenceTimer();
      stopAudioMonitoring();
      if (recognitionRef.current) {
        try {
          recognitionRef.current.stop();
        } catch (ignored) {}
      }
      clearInterval(timerRef.current);
    };
  }, []);

  // Auto-start listening when avatar finishes speaking
  useEffect(() => {
    if (isListening && !isRecording && !disabled) {
      startRecording();
    }
  }, [isListening, disabled]);

  const startAudioMonitoring = (stream) => {
    try {
      const AudioCtx = window.AudioContext || window.webkitAudioContext;
      if (!AudioCtx) return;
      const audioCtx = new AudioCtx();
      audioContextRef.current = audioCtx;

      const analyser = audioCtx.createAnalyser();
      analyser.fftSize = 64;
      analyserRef.current = analyser;

      const source = audioCtx.createMediaStreamSource(stream);
      source.connect(analyser);

      const dataArray = new Uint8Array(analyser.frequencyBinCount);

      const updateLevel = () => {
        if (!analyserRef.current) return;
        analyserRef.current.getByteFrequencyData(dataArray);
        let sum = 0;
        for (let i = 0; i < dataArray.length; i++) {
          sum += dataArray[i];
        }
        const avg = sum / dataArray.length;
        setAudioLevel(Math.min(100, Math.round((avg / 128) * 100)));
        animFrameRef.current = requestAnimationFrame(updateLevel);
      };

      updateLevel();
    } catch (e) {
      console.warn('Audio monitoring could not be initialized:', e);
    }
  };

  const stopAudioMonitoring = () => {
    if (animFrameRef.current) {
      cancelAnimationFrame(animFrameRef.current);
      animFrameRef.current = null;
    }
    if (audioContextRef.current && audioContextRef.current.state !== 'closed') {
      try {
        audioContextRef.current.close();
      } catch (ignored) {}
      audioContextRef.current = null;
    }
    setAudioLevel(0);
  };

  const startRecording = async () => {
    if (isRecording || disabled) return;
    clearSilenceTimer();
    setAnswerText('');
    finalTextRef.current = '';
    latestAnswerRef.current = '';

    try {
      const stream = await navigator.mediaDevices.getUserMedia({ audio: true });
      streamRef.current = stream;
      audioChunksRef.current = [];

      startAudioMonitoring(stream);

      const mediaRecorder = new MediaRecorder(stream);
      mediaRecorderRef.current = mediaRecorder;

      mediaRecorder.ondataavailable = (event) => {
        if (event.data.size > 0) {
          audioChunksRef.current.push(event.data);
        }
      };

      mediaRecorder.start();
      setIsRecording(true);
      isListeningRef.current = true;
      setRecordingTime(0);

      timerRef.current = setInterval(() => {
        setRecordingTime((t) => t + 1);
      }, 1000);

      if (recognitionRef.current) {
        try {
          recognitionRef.current.start();
        } catch (e) {
          console.warn('Speech recognition start note:', e);
        }
      }
    } catch (err) {
      console.error('Mic access error:', err);
    }
  };

  const stopRecording = () => {
    clearSilenceTimer();
    setIsRecording(false);
    isListeningRef.current = false;
    clearInterval(timerRef.current);
    stopAudioMonitoring();

    if (recognitionRef.current) {
      try {
        recognitionRef.current.stop();
      } catch (ignored) {}
    }

    if (mediaRecorderRef.current && mediaRecorderRef.current.state !== 'inactive') {
      mediaRecorderRef.current.stop();

      mediaRecorderRef.current.onstop = async () => {
        if (streamRef.current) {
          streamRef.current.getTracks().forEach((track) => track.stop());
        }

        if (!finalTextRef.current.trim() && !answerText.trim() && audioChunksRef.current.length > 0) {
          const audioBlob = new Blob(audioChunksRef.current, { type: 'audio/webm' });
          try {
            setIsTranscribing(true);
            const result = await speechApi.transcribeAudio(audioBlob);
            if (result && result.text) {
              setAnswerText(result.text.trim());
              finalTextRef.current = result.text.trim();
            }
          } catch (e) {
            console.error('Backend transcription failed:', e);
          } finally {
            setIsTranscribing(false);
          }
        }
      };
    }
  };

  const handleTextChange = (e) => {
    const val = e.target.value;
    setAnswerText(val);
    finalTextRef.current = val;
    latestAnswerRef.current = val;
  };

  const handleReset = () => {
    clearSilenceTimer();
    setAnswerText('');
    finalTextRef.current = '';
    latestAnswerRef.current = '';
    setRecordingTime(0);
  };

  const formatTime = (seconds) => {
    const mins = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${mins}:${secs < 10 ? '0' : ''}${secs}`;
  };

  return (
    <div style={{ marginTop: 16 }}>
      {/* Listening Status Banner */}
      {isRecording && (
        <div
          style={{
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            padding: '12px 18px',
            marginBottom: 14,
            borderRadius: 'var(--radius-sm)',
            background: 'rgba(16, 185, 129, 0.12)',
            border: '1px solid rgba(16, 185, 129, 0.3)',
          }}
        >
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span
              style={{
                width: 10,
                height: 10,
                borderRadius: '50%',
                backgroundColor: 'var(--accent-emerald)',
                boxShadow: '0 0 10px var(--accent-emerald)',
                display: 'inline-block',
                animation: 'pulse 1.5s infinite',
              }}
            />
            <span style={{ fontSize: '0.9rem', color: '#6ee7b7', fontWeight: 600 }}>
              {silenceNotice || 'Live Voice Active — Speak naturally! Auto-processes when you stop speaking.'}
            </span>
          </div>

          {/* Voice Waveform Indicator */}
          <div style={{ display: 'flex', alignItems: 'center', gap: 3, height: 18 }}>
            {[1, 2, 3, 4, 5].map((i) => {
              const h = Math.max(4, Math.min(18, Math.round((audioLevel / 100) * 18 * (i % 2 === 0 ? 1.2 : 0.8))));
              return (
                <span
                  key={i}
                  style={{
                    width: 3,
                    height: `${h}px`,
                    backgroundColor: 'var(--accent-emerald)',
                    borderRadius: 2,
                    transition: 'height 0.1s ease',
                  }}
                />
              );
            })}
          </div>
        </div>
      )}

      {/* Controls Bar */}
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {!isRecording ? (
            <button
              type="button"
              onClick={startRecording}
              disabled={disabled}
              className="btn btn-primary"
              style={{ borderRadius: 'var(--radius-full)' }}
            >
              <Mic size={18} />
              Open Mic & Speak
            </button>
          ) : (
            <button
              type="button"
              onClick={stopRecording}
              className="btn btn-danger"
              style={{ borderRadius: 'var(--radius-full)' }}
            >
              <MicOff size={18} />
              Pause Mic ({formatTime(recordingTime)})
            </button>
          )}

          {isTranscribing && (
            <span style={{ fontSize: '0.85rem', color: 'var(--accent-cyan)' }}>
              Transcribing audio via AI...
            </span>
          )}
        </div>

        {answerText && (
          <button
            type="button"
            onClick={handleReset}
            disabled={disabled}
            className="btn btn-secondary btn-sm"
            title="Clear text"
          >
            <RotateCcw size={14} /> Clear
          </button>
        )}
      </div>

      {/* Live Text Area (Read-only live stream transcript) */}
      <div className="form-group" style={{ marginBottom: 0 }}>
        <textarea
          className="form-textarea"
          value={answerText}
          onChange={handleTextChange}
          placeholder={placeholder}
          disabled={disabled}
          rows={3}
          style={{
            fontSize: '1rem',
            lineHeight: '1.5',
            borderColor: isRecording ? 'var(--accent-emerald)' : undefined,
            boxShadow: isRecording ? '0 0 0 1px rgba(16, 185, 129, 0.25)' : undefined,
          }}
        />
      </div>
    </div>
  );
};
