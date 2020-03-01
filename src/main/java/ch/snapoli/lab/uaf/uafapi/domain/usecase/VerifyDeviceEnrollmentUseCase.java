package ch.snapoli.lab.uaf.uafapi.domain.usecase;

import ch.snapoli.lab.uaf.uafapi.domain.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.PublicKey;

@RequiredArgsConstructor
public class VerifyDeviceEnrollmentUseCase {

    private final EnrollDeviceChallengeRepository enrollDeviceChallengeRepository;
    private final ChallengeExecutorFactory challengeExecutorFactory;
    private final PublicKeyGenerator publicKeyGenerator;
    private final PublicKeyStorageRepository publicKeyStorageRepository;

    public <RESPONSE extends ChallengeExecutor.ChallengeAnswer> VerifyChallengeResponse verify(VerifyDeviceEnrollmentUseCase.ApproveRequest<RESPONSE> request){

        EnrollDeviceChallengeRepository.ChallengeEntry challengeEntry = enrollDeviceChallengeRepository.find(request.challengeId).orElseThrow(StartDeviceEnrollmentUseCase.ChallengeNotFoundException::new);

        StartedChallengeRequest startedChallenge = challengeEntry.getStartedChallenge();

        ChallengeExecutor<ChallengeRequest, ChallengeExecutor.ChallengeAnswer> challengeExecutor = challengeExecutorFactory.create(startedChallenge.challengeRequest());

        boolean validated = challengeExecutor.resolve(startedChallenge, request.challengeAnswer);

        if(!validated) {
            startedChallenge.execution().increaseAttempt();
        }


        verifyAlgoritmIsSupported(request.jwkKey.ellipticCurve, request.jwkKey.keyType);

        PublicKey publicKey = publicKeyGenerator.create(request.jwkKey.x, request.jwkKey.y);

        publicKeyStorageRepository.store(challengeEntry.getEnrollableDevice().getDeviceId(), publicKey);

        enrollDeviceChallengeRepository.remove(challengeEntry.getEnrollableDevice());

        return new VerifyChallengeResponse(validated, request.challengeId.getId(), startedChallenge);

    }

    @RequiredArgsConstructor
    @Getter
    public static class ApproveRequest<RESPONSE extends ChallengeExecutor.ChallengeAnswer> {
        private final EnrollDeviceChallengeRepository.ChallengeId challengeId;
        private final RESPONSE challengeAnswer;
        private final JwkKey jwkKey;

        @RequiredArgsConstructor
        @Getter
        public static class JwkKey {

            private final String keyType;
            private final String ellipticCurve;
            private final String x;
            private final String y;

        }
    }

    private void verifyAlgoritmIsSupported(String ellipticCurve, String keyType) {
        if(!keyType.equalsIgnoreCase("EC"))
            throw new IllegalArgumentException("not supported key type");
        if(!ellipticCurve.equalsIgnoreCase("secp256r1"))
            throw new IllegalArgumentException("not supported elliptic curve");
    }
}
