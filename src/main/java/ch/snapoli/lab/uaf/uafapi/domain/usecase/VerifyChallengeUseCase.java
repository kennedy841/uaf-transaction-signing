package ch.snapoli.lab.uaf.uafapi.domain.usecase;

import ch.snapoli.lab.uaf.uafapi.domain.*;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class VerifyChallengeUseCase {

    private final ResourceChallengeStorage challengeStorage;
    private final ChallengeExecutorFactory challengeExecutorFactory;
    private final int degradationAfterFail;


    public <RESPONSE extends ChallengeExecutor.ChallengeAnswer> VerifyChallengeResponse verify(String challengeId, RESPONSE challengeAnswer) {
        StartedChallengeRequest startedChallenge = challengeStorage.get(challengeId).orElseThrow(ChallengeNotFoundException::new);

        checkAnswerMatch(challengeAnswer, startedChallenge);

        ChallengeExecutor<ChallengeRequest, ChallengeExecutor.ChallengeAnswer> challengeExecutor = challengeExecutorFactory.create(startedChallenge.challengeRequest());

        Boolean validated = challengeExecutor.resolve(startedChallenge, challengeAnswer);

        if(!validated) {
            startedChallenge.execution().increaseAttempt();
            VerifyChallengeResponse verifyChallengeResponse = doVerify(challengeId, startedChallenge);
            startedChallenge.status(StartedChallengeRequest.Status.STARTED);
            challengeStorage.store(challengeId, verifyChallengeResponse.getStartedChallenge());
            return verifyChallengeResponse;

        }

        startedChallenge.status(StartedChallengeRequest.Status.RESOLVED);

        //challengeStorage.remove(challengeId);
        challengeStorage.store(challengeId, startedChallenge);
        return new VerifyChallengeResponse(validated, challengeId, null);


    }

    private VerifyChallengeResponse doVerify(String challengeId, StartedChallengeRequest startedChallenge) {
        return new VerifyChallengeResponse(false, challengeId, startedChallenge.accept(new ChallengeAwareVisitor<StartedChallengeRequest>() {
            @Override
            public StartedChallengeRequest visit(ECSAChallengeRequest rsaPublicPrivateKeyChallengeTypeAware) {

                int attemptNumber = startedChallenge.execution().getAttemptNumber();
                if(attemptNumber >= degradationAfterFail){
                    ChallengeRequest challengeRequest = startedChallenge.challengeRequest();
                    ChallengeExecutor<ChallengeRequest, ChallengeExecutor.ChallengeAnswer> challengeExecutor =
                            challengeExecutorFactory.create(new OTPRequest(challengeRequest.userId(), challengeRequest.deviceId()));
                    return challengeExecutor.start(challengeRequest);
                }


                return new ECSAChallengeRequest(
                        rsaPublicPrivateKeyChallengeTypeAware.getPayload().rebuild(),
                        rsaPublicPrivateKeyChallengeTypeAware.challengeRequest()
                );

            }

            @Override
            public StartedChallengeRequest visit(PocketListChallengeRequest pocketListChallengeTypeAware) {
                return pocketListChallengeTypeAware;
            }

            @Override
            public StartedChallengeRequest visit(ECSAPushingChallengeRequest rsaPublicPrivateKeyPushingChallengeRequest) {
                rsaPublicPrivateKeyPushingChallengeRequest.status(StartedChallengeRequest.Status.EXPIRED);
                return rsaPublicPrivateKeyPushingChallengeRequest;
            }
        }));
    }

    private <RESPONSE extends ChallengeExecutor.ChallengeAnswer> void checkAnswerMatch(RESPONSE challengeAnswer, StartedChallengeRequest startedChallenge) {
        Boolean match = challengeAnswer.accept(new ChallengeExecutor.ChallengeAnswer.ChallengeAnswerVisitor<Boolean>() {
            @Override
            public Boolean visit(ChallengeExecutor.OTPChallengeAnswer otpChallengeAnswer) {
                return startedChallenge.type().equals(StartedChallengeRequest.Type.POCKETLIST);
            }

            @Override
            public Boolean visit(ChallengeExecutor.RsaChallengeAnswer rsaChallengeAnswer) {
                return startedChallenge.type().equals(StartedChallengeRequest.Type.PUBPRIV);
            }
        });

        if(!match){
            throw new RuntimeException("challenge response not supported");
        }
    }

    public static class ChallengeNotFoundException extends RuntimeException{

    }


}
