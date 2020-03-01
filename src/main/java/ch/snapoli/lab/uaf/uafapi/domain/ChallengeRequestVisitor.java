package ch.snapoli.lab.uaf.uafapi.domain;

public interface ChallengeRequestVisitor<O> {

    O visit(ECSARequest ecsaRequest);

    O visit(PushECSARequest ecsaRequest);

    O visit(OTPRequest pocketListRequest);
}
