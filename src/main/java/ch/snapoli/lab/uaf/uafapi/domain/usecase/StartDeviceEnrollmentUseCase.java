package ch.snapoli.lab.uaf.uafapi.domain.usecase;

import ch.snapoli.lab.uaf.uafapi.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static ch.snapoli.lab.uaf.uafapi.domain.usecase.StartDeviceEnrollmentUseCase.ApproveResponse.*;

@RequiredArgsConstructor
public class StartDeviceEnrollmentUseCase {

    public static JwkKeySpecification ECP256 = new JwkKeySpecification("EC", "secp256r1");


    private final EnrollDeviceChallengeRepository challengeRepository;
    private final ChallengeExecutorFactory challengeExecutorFactory;

    public ApproveResponse enroll(CreateRequest request){

        challengeRepository.remove(request.getEnrollableDevice());

        StartedChallengeRequest challengeTypeAware = request.getChallengeRequest().accept(new ChallengeRequestVisitor<StartedChallengeRequest>() {

            @Override
            public StartedChallengeRequest visit(ECSARequest ecsaRequest) {
                throw new IllegalArgumentException("not supported");
            }

            @Override
            public StartedChallengeRequest visit(PushECSARequest ecsaRequest) {
                throw new IllegalArgumentException("not supported");
            }

            @Override
            public StartedChallengeRequest visit(OTPRequest pocketListRequest) {
                ChallengeExecutor<ChallengeRequest, ChallengeExecutor.ChallengeAnswer> challengeRequestChallengeAnswerChallengeExecutor = challengeExecutorFactory.create(request.getChallengeRequest());
                return challengeRequestChallengeAnswerChallengeExecutor.start(pocketListRequest);
            }
        });

        EnrollDeviceChallengeRepository.ChallengeId challengeId = challengeRepository.create(request.getEnrollableDevice(), challengeTypeAware);

        return new ApproveResponse(challengeId, challengeTypeAware, Arrays.asList(ECP256));

    }

    public static class ChallengeNotFoundException extends RuntimeException {

    }


    @RequiredArgsConstructor
    @Getter
    public static class ApproveResponse {
        private final EnrollDeviceChallengeRepository.ChallengeId challengId;
        private final StartedChallengeRequest challengeType;
        private final List<JwkKeySpecification> jwkKeySpecifications;

        @RequiredArgsConstructor
        @Getter
        public static class JwkKeySpecification {
            private final String keyType;
            private final String ellipticCurve;
        }
    }

    @RequiredArgsConstructor
    @Getter
    public static class CreateRequest {
        private final EnrollableDevice enrollableDevice;
        private final ChallengeRequest challengeRequest;
    }
}
