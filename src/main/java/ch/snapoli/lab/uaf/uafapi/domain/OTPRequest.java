package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
 public class OTPRequest implements ChallengeRequest{
     private final String userId;
     private final String deviceId;

    @Override
    public String deviceId() {
        return deviceId;
    }

    @Override
    public String userId() {
        return userId;
    }

    @Override
    public <O> O accept(ChallengeRequestVisitor<O> visitor) {
        return visitor.visit(this);
    }
}
