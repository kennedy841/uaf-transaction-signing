package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PocketListChallengeExecutor implements ChallengeExecutor<OTPRequest, ChallengeExecutor.OTPChallengeAnswer> {

    private final OptGenerator optGenerator;
    private final OTPValidator otpValidator;

    @Override
    public StartedChallengeRequest start(OTPRequest createRequest) {
        OptGenerator.Otp generate = optGenerator.generate(createRequest.getUserId());
        return new PocketListChallengeRequest(generate, new OTPRequest(createRequest.getUserId(), createRequest.getDeviceId()));
    }

    @Override
    public boolean resolve(StartedChallengeRequest startedChallenge, OTPChallengeAnswer value) {
        return startedChallenge.challengeRequest().accept(new ChallengeRequestVisitor<Boolean>() {
            @Override
            public Boolean visit(ECSARequest ecsaRequest) {
                throw new RuntimeException("not supported");
            }

            @Override
            public Boolean visit(PushECSARequest ecsaRequest) {
                throw new RuntimeException("not supported");
            }

            @Override
            public Boolean visit(OTPRequest pocketListRequest) {
                return otpValidator.validate(pocketListRequest.getUserId(), value);
            }

        });

    }


}
