package ch.snapoli.lab.uaf.uafapi.domain.usecase;

import ch.snapoli.lab.uaf.uafapi.domain.*;
import ch.snapoli.lab.uaf.uafapi.domain.ChallengeExecutor.ChallengeAnswer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class CreateResourceChallengeUseCase {

    private final ResourceChallengeStorage payloadStorage;
    private final ChallengeExecutorFactory challengeExecutorFactory;
    private final ChallengeSupportedVerifier challengeSupportedVerifier;

    public CreateResourceChallengeResponse create(CreateResourceChallengeRequest request) {


        boolean check = challengeSupportedVerifier.check(request.challengeRequest);

        if(!check)
            throw new IllegalArgumentException("challenge type not supported");

        StartedChallengeRequest startedChallenge = request.challengeRequest.accept(new ChallengeRequestVisitor<StartedChallengeRequest>() {
            @Override
            public StartedChallengeRequest visit(ECSARequest ecsaRequest) {

                return challengeExecutorFactory.create(ecsaRequest).start(ecsaRequest);
            }

            @Override
            public StartedChallengeRequest visit(PushECSARequest ecsaRequest) {
                return challengeExecutorFactory.create(ecsaRequest).start(ecsaRequest);
            }

            @Override
            public StartedChallengeRequest visit(OTPRequest pocketListRequest) {

                ChallengeExecutor<ChallengeRequest, ChallengeAnswer> challengeExecutor = challengeExecutorFactory.create(pocketListRequest);

                return challengeExecutor.start(pocketListRequest);
            }
        });

        String challengeId = UUID.randomUUID().toString();

        payloadStorage.store(challengeId, startedChallenge);

        return new CreateResourceChallengeResponse(challengeId, startedChallenge);
    }

    @RequiredArgsConstructor
    @Getter
    public static class CreateResourceChallengeRequest {
        private final ChallengeRequest challengeRequest;
    }


    @RequiredArgsConstructor
    @Getter
    public static class CreateResourceChallengeResponse {

        private final String challengeId;
        private final StartedChallengeRequest startedChallenge;

    }
}
