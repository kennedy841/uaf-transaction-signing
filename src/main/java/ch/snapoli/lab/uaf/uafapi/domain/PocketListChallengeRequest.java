package ch.snapoli.lab.uaf.uafapi.domain;

import ch.snapoli.lab.uaf.uafapi.domain.OptGenerator.Otp;
import lombok.Getter;

@Getter
public class PocketListChallengeRequest extends AbstractStartedChallengeRequest {

    private final Otp pocketCode;

    public PocketListChallengeRequest(Otp pocketCode, ChallengeRequest challengeRequest) {
        super(challengeRequest);
        this.pocketCode = pocketCode;
    }

    @Override
    public Type type() {
        return Type.POCKETLIST;
    }

    @Override
    public Otp challenge() {
        return pocketCode;
    }

    @Override
    public <O> O accept(ChallengeAwareVisitor<O> visitor) {
        return visitor.visit(this);
    }

}
