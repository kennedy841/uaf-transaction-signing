package ch.snapoli.lab.uaf.uafapi.domain;

public interface ChallengeRequest {

    public String deviceId();

    public String userId();

    public <O> O accept(ChallengeRequestVisitor<O> visitor);

}
