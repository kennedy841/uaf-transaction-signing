package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class VerifyChallengeResponse {
    private final boolean validated;
    private final String challengeId;
    private final StartedChallengeRequest startedChallenge;
}
