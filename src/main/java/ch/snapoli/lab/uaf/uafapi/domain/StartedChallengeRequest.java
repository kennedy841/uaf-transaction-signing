package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;

public interface StartedChallengeRequest {

    public ChallengeRequest challengeRequest();

    public Type type();

    public ExecutionContext execution();

    public Status status();

    public <CHALLENGE> CHALLENGE challenge();

    public void status(Status status);

    public static enum Type {
        POCKETLIST, PUBPRIV, PUSHPUBPRIV
    }

    public static enum Status{
        STARTED, EXPIRED, RESOLVED
    }

    @Getter
    class ExecutionContext {
        private int attemptNumber = 0;

        public void increaseAttempt(){
            this.attemptNumber++;
        }
    }

    public <O> O accept(ChallengeAwareVisitor<O> visitor);

}
