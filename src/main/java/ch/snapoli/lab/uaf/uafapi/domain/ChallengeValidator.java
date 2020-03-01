package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface ChallengeValidator {

    public ChallengeResponseTypeAware validate(StartedChallengeRequest startedChallenge, ChallengeResponseTypeAware challengeResponse);

    @RequiredArgsConstructor
    @Getter
    public class ChallengeResponseTypeAware {

        private final boolean success;

    }
}
