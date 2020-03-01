package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class StandardChallengeSupportedVerifier implements ChallengeSupportedVerifier {

    private final PublicKeyStorageRepository publicKeyStorageRepository;
    private final DeviceRepository deviceRepository;


    @Override
    public boolean check(ChallengeRequest challengeRequest) {
        return challengeRequest.accept(new ChallengeRequestVisitor<Boolean>() {
            @Override
            public Boolean visit(ECSARequest ecsaRequest) {
                return publicKeyStorageRepository.get(ecsaRequest.getDeviceId()) != null;
            }

            @Override
            public Boolean visit(PushECSARequest ecsaRequest) {
                List<DeviceRepository.Device> devices = deviceRepository.findAllWithPushIdAndPublicKey(ecsaRequest.userId());
                return publicKeyStorageRepository.get(ecsaRequest.getDeviceId()) != null && !devices.isEmpty();
            }

            @Override
            public Boolean visit(OTPRequest pocketListRequest) {
                return true;
            }
        });
    }
}
