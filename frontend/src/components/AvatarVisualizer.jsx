import React, { useEffect, useState } from 'react';
import { Volume2, VolumeX, Sparkles } from 'lucide-react';

export const AvatarVisualizer = ({
  transitionText = '',
  questionText = '',
  status = 'idle', // 'idle' | 'speaking' | 'listening' | 'thinking'
  onSpeechEnd = () => {},
  autoSpeak = true,
}) => {
  const [isSpeaking, setIsSpeaking] = useState(false);
  const [voiceMuted, setVoiceMuted] = useState(false);
  const [mouthOpen, setMouthOpen] = useState(0);

  // Lip-sync simulation animation loop during speech
  useEffect(() => {
    let interval;
    if (isSpeaking) {
      interval = setInterval(() => {
        setMouthOpen(Math.floor(Math.random() * 8) + 2);
      }, 120);
    } else {
      setMouthOpen(0);
    }
    return () => clearInterval(interval);
  }, [isSpeaking]);

  // Handle Speech Synthesis
  useEffect(() => {
    if (!autoSpeak || voiceMuted) return;

    const fullSpeech = [transitionText, questionText].filter(Boolean).join('. ');
    if (!fullSpeech) return;

    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel(); // Stop any previous speech

      const utterance = new SpeechSynthesisUtterance(fullSpeech);
      utterance.rate = 1.0;
      utterance.pitch = 1.05;

      // Select natural voice if available
      const voices = window.speechSynthesis.getVoices();
      const preferredVoice = voices.find(
        (v) => v.lang.startsWith('en') && (v.name.includes('Natural') || v.name.includes('Google') || v.name.includes('Samantha') || v.name.includes('Karen'))
      ) || voices.find((v) => v.lang.startsWith('en'));

      if (preferredVoice) {
        utterance.voice = preferredVoice;
      }

      utterance.onstart = () => setIsSpeaking(true);
      utterance.onend = () => {
        setIsSpeaking(false);
        onSpeechEnd();
      };
      utterance.onerror = () => {
        setIsSpeaking(false);
        onSpeechEnd();
      };

      window.speechSynthesis.speak(utterance);
    }

    return () => {
      if ('speechSynthesis' in window) {
        window.speechSynthesis.cancel();
      }
    };
  }, [transitionText, questionText, voiceMuted, autoSpeak]);

  const toggleVoice = () => {
    if ('speechSynthesis' in window) {
      window.speechSynthesis.cancel();
    }
    setIsSpeaking(false);
    setVoiceMuted(!voiceMuted);
  };

  const getStatusBadge = () => {
    if (isSpeaking) {
      return (
        <span className="badge badge-hire" style={{ gap: 6 }}>
          <Sparkles size={12} /> AI Interviewer Speaking...
        </span>
      );
    }
    if (status === 'listening') {
      return (
        <span className="badge badge-strong-hire">
          ● Listening to you...
        </span>
      );
    }
    if (status === 'thinking') {
      return (
        <span className="badge badge-progress">
          ● Processing answer...
        </span>
      );
    }
    return (
      <span className="badge badge-consider">
        ● Ready
      </span>
    );
  };

  return (
    <div className={`avatar-container ${isSpeaking ? 'avatar-speaking' : ''}`}>
      <div className="avatar-halo"></div>

      {/* Voice Mute Toggle */}
      <button
        onClick={toggleVoice}
        style={{
          position: 'absolute',
          top: 16,
          right: 16,
          background: 'rgba(255, 255, 255, 0.08)',
          border: '1px solid var(--border-subtle)',
          borderRadius: '50%',
          width: 36,
          height: 36,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: voiceMuted ? 'var(--accent-rose)' : 'var(--accent-cyan)',
          cursor: 'pointer',
        }}
        title={voiceMuted ? 'Unmute AI voice' : 'Mute AI voice'}
      >
        {voiceMuted ? <VolumeX size={18} /> : <Volume2 size={18} />}
      </button>

      {/* Animated Talking Avatar Face */}
      <div className="avatar-canvas-wrapper">
        <svg
          viewBox="0 0 200 200"
          style={{ width: '100%', height: '100%' }}
        >
          <defs>
            <linearGradient id="avatarSkin" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#fed7aa" />
              <stop offset="100%" stopColor="#fba779" />
            </linearGradient>
            <linearGradient id="avatarHair" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#312e81" />
              <stop offset="100%" stopColor="#1e1b4b" />
            </linearGradient>
            <linearGradient id="avatarSuit" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#1e293b" />
              <stop offset="100%" stopColor="#0f172a" />
            </linearGradient>
            <linearGradient id="aiBadge" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" stopColor="#6366f1" />
              <stop offset="100%" stopColor="#06b6d4" />
            </linearGradient>
          </defs>

          {/* Background circle */}
          <circle cx="100" cy="100" r="95" fill="#0b1329" />

          {/* Suit & Collar */}
          <path d="M 40 200 L 40 160 Q 100 130 160 160 L 160 200 Z" fill="url(#avatarSuit)" />
          <path d="M 85 160 L 100 185 L 115 160 Z" fill="#ffffff" />
          <path d="M 96 160 L 100 180 L 104 160 Z" fill="#6366f1" />

          {/* Neck */}
          <rect x="85" y="120" width="30" height="30" fill="#fba779" rx="5" />

          {/* Head */}
          <ellipse cx="100" cy="90" rx="42" ry="48" fill="url(#avatarSkin)" />

          {/* Hair */}
          <path
            d="M 58 85 C 58 45, 142 45, 142 85 C 135 60, 110 55, 100 55 C 80 55, 65 65, 58 85 Z"
            fill="url(#avatarHair)"
          />

          {/* Eyebrows */}
          <path d="M 72 72 Q 82 68 90 72" stroke="#1e1b4b" strokeWidth="2.5" fill="none" strokeLinecap="round" />
          <path d="M 110 72 Q 118 68 128 72" stroke="#1e1b4b" strokeWidth="2.5" fill="none" strokeLinecap="round" />

          {/* Eyes */}
          <ellipse cx="80" cy="82" rx="5" ry="3.5" fill="#0f172a" />
          <ellipse cx="120" cy="82" rx="5" ry="3.5" fill="#0f172a" />
          <circle cx="82" cy="81" r="1.5" fill="#ffffff" />
          <circle cx="122" cy="81" r="1.5" fill="#ffffff" />

          {/* Glasses Frame */}
          <rect x="68" y="74" width="24" height="16" rx="4" fill="none" stroke="#6366f1" strokeWidth="2" />
          <rect x="108" y="74" width="24" height="16" rx="4" fill="none" stroke="#6366f1" strokeWidth="2" />
          <line x1="92" y1="82" x2="108" y2="82" stroke="#6366f1" strokeWidth="2" />

          {/* Nose */}
          <path d="M 100 86 Q 102 96 98 98 L 102 98" stroke="#ea580c" strokeWidth="1.5" fill="none" strokeLinecap="round" />

          {/* Lip-Sync Animated Mouth */}
          <path
            d={`M 88 114 Q 100 ${114 + mouthOpen} 112 114 Q 100 ${114 - (mouthOpen > 3 ? 2 : 0)} 88 114 Z`}
            fill="#e11d48"
            stroke="#9f1239"
            strokeWidth="1.5"
          />
        </svg>
      </div>

      {/* Reactive Sound Bars */}
      <div className="sound-waves">
        <span className="sound-bar"></span>
        <span className="sound-bar"></span>
        <span className="sound-bar"></span>
        <span className="sound-bar"></span>
        <span className="sound-bar"></span>
        <span className="sound-bar"></span>
      </div>

      <div style={{ marginTop: 14 }}>
        {getStatusBadge()}
      </div>
    </div>
  );
};
