package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractStartedChallengeRequest implements StartedChallengeRequest{
    protected Status status = Status.STARTED;
    protected final ExecutionContext executionContext = new ExecutionContext();
    protected final ChallengeRequest challengeRequest;

    @Override
    public ChallengeRequest challengeRequest() {
        return challengeRequest;
    }


    @Override
    public ExecutionContext execution() {
        return executionContext;
    }

    @Override
    public Status status() {
        return status;
    }

    @Override
    public void status(Status status) {
        this.status = status;
    }

}
