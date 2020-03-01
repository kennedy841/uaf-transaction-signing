package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.*;

import java.util.Optional;
import java.util.UUID;

public interface EnrollDeviceChallengeRepository {

    ChallengeId create(EnrollableDevice enrollableDevice, StartedChallengeRequest startedChallenge);

    void remove(EnrollableDevice enrollableDevice);

    Optional<ChallengeEntry> find(ChallengeId challengeId);

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static class ChallengeId {
      private String id;


      public static ChallengeId generate(){
          return new ChallengeId(UUID.randomUUID().toString());
      }
    }

    @RequiredArgsConstructor
    @EqualsAndHashCode
    @Getter
    public static class ChallengeEntry {
        private final StartedChallengeRequest startedChallenge;
        private final EnrollableDevice enrollableDevice;
    }
}
