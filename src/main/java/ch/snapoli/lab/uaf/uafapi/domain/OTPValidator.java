package ch.snapoli.lab.uaf.uafapi.domain;

public interface OTPValidator {
    boolean validate(String userId, ChallengeExecutor.OTPChallengeAnswer value);

}
