package ch.snapoli.lab.uaf.uafapi.domain;

public interface ChallengeSupportedVerifier {
    boolean check(ChallengeRequest challengeRequest);
}
