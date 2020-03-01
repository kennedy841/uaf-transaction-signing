package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class Challenge<TYPE extends StartedChallengeRequest> {
    private final String id;
    private final TYPE type;
}
