package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
public class PushECSARequest extends ECSARequest {

    private PushRequest pushRequest;

    @RequiredArgsConstructor
    @Getter
    public static class PushRequest {
        private final String message;
        private final List<String> additionalMessages;
    }


    public PushECSARequest(String deviceId, String resourceRef, String userId, PushRequest pushRequest) {
        super(deviceId, resourceRef, userId);
        this.pushRequest = pushRequest;
    }

    @Override
    public <O> O accept(ChallengeRequestVisitor<O> visitor) {
        return visitor.visit(this);
    }
}
