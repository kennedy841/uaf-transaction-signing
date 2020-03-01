package ch.snapoli.lab.uaf.uafapi.domain;

public interface PayloadConverter {
    String convert(ECSAChallengeRequest.Payload payload);
}
