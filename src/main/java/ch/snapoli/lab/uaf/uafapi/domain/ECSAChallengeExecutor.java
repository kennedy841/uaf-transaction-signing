package ch.snapoli.lab.uaf.uafapi.domain;

import ch.snapoli.lab.uaf.uafapi.domain.ECSAChallengeRequest.Payload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;

@RequiredArgsConstructor
@Slf4j
public class ECSAChallengeExecutor implements ChallengeExecutor<ECSARequest, ChallengeExecutor.RsaChallengeAnswer> {

    private final SignatureValidator signatureValidator;
    private final PayloadConverter payloadConverter;
    private final PublicKeyStorageRepository publicKeyStorageRepository;


    @Override
    public ECSAChallengeRequest start(ECSARequest resource) {
        return new ECSAChallengeRequest(Payload.of(resource.getResourceRef()), resource);
    }

    @Override
    public boolean resolve(StartedChallengeRequest startedChallenge, RsaChallengeAnswer value) {
        return (boolean) startedChallenge.accept(new ChallengeAwareVisitor<Boolean>() {
            @Override
            public Boolean visit(ECSAChallengeRequest rsaPublicPrivateKeyChallengeTypeAware) {
                return doRsaChallenge(rsaPublicPrivateKeyChallengeTypeAware, startedChallenge, value);
            }

            @Override
            public Boolean visit(PocketListChallengeRequest pocketListChallengeTypeAware) {
                throw new IllegalArgumentException("not supported");
            }

            @Override
            public Boolean visit(ECSAPushingChallengeRequest rsaPublicPrivateKeyPushingChallengeRequest) {
                return doRsaChallenge(rsaPublicPrivateKeyPushingChallengeRequest, startedChallenge, value);
            }
        });
    }

    private Boolean doRsaChallenge(ECSAChallengeRequest rsaPublicPrivateKeyChallengeTypeAware, StartedChallengeRequest startedChallenge, RsaChallengeAnswer value) {
        try {
            //byte[] decode = Base64.getDecoder().decode(value.getSignaturePayloadBase64().getBytes(StandardCharsets.UTF_8));

            String message = payloadConverter.convert(rsaPublicPrivateKeyChallengeTypeAware.getPayload());

            PublicKey publicKey = publicKeyStorageRepository.get(startedChallenge.challengeRequest().deviceId());

            return signatureValidator.validate(publicKey, message, value.getSignaturePayloadBase64());
        }
        catch (Exception e){
            log.warn(e.getMessage());
            return false;
        }
    }

}
