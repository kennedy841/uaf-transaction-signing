package ch.snapoli.lab.uaf.uafapi.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChallengeExecutorFactory {

    private final OptGenerator pocketListGenerator;
    private final OTPValidator pocketListValidator;
    private final SignatureValidator signatureValidator;
    private final PayloadConverter payloadConverter;
    private final PublicKeyStorageRepository publicKeyStorageRepository;
    private final DeviceRepository deviceRepository;
    private final PushECSAChallengeExecutor.CnsDelivery cnsDelivery;
    private final ObjectMapper objectMapper;

    public <REQUEST extends ChallengeRequest, RESPONSE extends ChallengeExecutor.ChallengeAnswer> ChallengeExecutor<REQUEST, RESPONSE> create(ChallengeRequest challengeRequest){

        return challengeRequest.accept(new ChallengeRequestVisitor<ChallengeExecutor<REQUEST, RESPONSE>>() {
            @Override
            public ChallengeExecutor<REQUEST, RESPONSE> visit(ECSARequest ecsaRequest) {
                return (ChallengeExecutor<REQUEST, RESPONSE>) new ECSAChallengeExecutor(signatureValidator, payloadConverter, publicKeyStorageRepository);
            }

            @Override
            public ChallengeExecutor<REQUEST, RESPONSE> visit(PushECSARequest ecsaRequest) {
                return (ChallengeExecutor<REQUEST, RESPONSE>)
                        new PushECSAChallengeExecutor(new ECSAChallengeExecutor(
                                signatureValidator, payloadConverter, publicKeyStorageRepository),
                                deviceRepository, cnsDelivery, objectMapper);
            }

            @Override
            public ChallengeExecutor<REQUEST, RESPONSE> visit(OTPRequest pocketListRequest) {
                return (ChallengeExecutor<REQUEST, RESPONSE>) new PocketListChallengeExecutor(pocketListGenerator, pocketListValidator);
            }
        });

    }
}
