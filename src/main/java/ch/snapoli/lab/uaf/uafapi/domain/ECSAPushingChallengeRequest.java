package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;

@Getter
public class ECSAPushingChallengeRequest extends ECSAChallengeRequest {

    public ECSAPushingChallengeRequest(Payload payload, ChallengeRequest challengeRequest) {
        super(payload, challengeRequest);
    }


    @Override
    public <O> O accept(ChallengeAwareVisitor<O> visitor) {
        return visitor.visit(this);
    }

    @Override
    public Type type() {
        return Type.PUSHPUBPRIV;
    }


}
