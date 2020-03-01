package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ECSARequest implements ChallengeRequest {
    private final String deviceId;
    private final String resourceRef;
    private final String userId;

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
