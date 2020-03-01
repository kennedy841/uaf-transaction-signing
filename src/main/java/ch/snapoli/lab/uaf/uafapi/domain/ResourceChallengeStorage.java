package ch.snapoli.lab.uaf.uafapi.domain;

import java.util.Optional;

public interface ResourceChallengeStorage {
    void store(String challengeId, StartedChallengeRequest payload);

    Optional<StartedChallengeRequest> get(String challengeId) throws NoChallengeFound;

    void remove(String challengeId);

    public static class NoChallengeFound extends RuntimeException{

    }

}
