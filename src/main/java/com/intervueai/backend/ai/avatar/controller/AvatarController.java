package com.intervueai.backend.ai.avatar.controller;

import com.intervueai.backend.ai.avatar.AvatarService;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSessionResponse;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakRequest;
import com.intervueai.backend.ai.avatar.dto.AvatarSpeakResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/avatar")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Avatar API", description = "Endpoints for controlling the talking AI interviewer avatar")
public class AvatarController {

    private final AvatarService avatarService;

    public AvatarController(AvatarService avatarService) {
        this.avatarService = avatarService;
    }

    @Operation(
            summary = "Create Avatar Session",
            description = "Initializes a real-time streaming or WebRTC session with the talking avatar."
    )
    @PostMapping("/session")
    public ResponseEntity<AvatarSessionResponse> createSession(
            @RequestBody(required = false) AvatarSessionRequest request
    ) {
        if (request == null) {
            request = new AvatarSessionRequest();
        }
        AvatarSessionResponse response = avatarService.startSession(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Instruct Avatar to Speak",
            description = "Dispatches interview transition or question text to the talking avatar."
    )
    @PostMapping("/speak")
    public ResponseEntity<AvatarSpeakResponse> speak(
            @Valid @RequestBody AvatarSpeakRequest request
    ) {
        AvatarSpeakResponse response = avatarService.speak(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "End Avatar Session",
            description = "Closes the current talking avatar session and releases streaming resources."
    )
    @DeleteMapping("/session/{sessionId}")
    public ResponseEntity<Void> endSession(
            @PathVariable String sessionId
    ) {
        avatarService.endSession(sessionId);
        return ResponseEntity.noContent().build();
    }
}
