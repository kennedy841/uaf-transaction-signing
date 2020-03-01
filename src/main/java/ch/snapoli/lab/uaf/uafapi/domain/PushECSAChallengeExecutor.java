package ch.snapoli.lab.uaf.uafapi.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class PushECSAChallengeExecutor implements ChallengeExecutor<PushECSARequest, ChallengeExecutor.RsaChallengeAnswer>{

    private final ECSAChallengeExecutor ecsaChallengeExecutor;
    private final DeviceRepository deviceRepository;
    private final CnsDelivery cnsDelivery;
    private final ObjectMapper objectMapper;

    @Override
    public StartedChallengeRequest start(PushECSARequest request) {
        ECSAChallengeRequest ecsaChallengeRequest = ecsaChallengeExecutor.start(request);

        List<DeviceRepository.Device> devices = deviceRepository.findAllWithPushIdAndPublicKey(request.getUserId());

        devices.stream().forEach(new Consumer<DeviceRepository.Device>() {
            @Override
            @SneakyThrows
            public void accept(DeviceRepository.Device device) {
                List<String> additionalMessages = request.getPushRequest().getAdditionalMessages();
                Map<String, String> headers = new HashMap<>();

                headers.put("additionalMessages", objectMapper.writeValueAsString(additionalMessages));
                headers.put("payload", objectMapper.writeValueAsString(ecsaChallengeRequest.challenge()));

                cnsDelivery.send(device.getPushId(), request.getPushRequest().getMessage(), headers);
            }
        });

        return new ECSAPushingChallengeRequest(ecsaChallengeRequest.challenge(), ecsaChallengeRequest.challengeRequest);
    }

    @Override
    public boolean resolve(StartedChallengeRequest startedChallenge, RsaChallengeAnswer rsaChallengeAnswer) throws ChallengeFailException {
        return ecsaChallengeExecutor.resolve(startedChallenge, rsaChallengeAnswer);
    }

    public static interface CnsDelivery {
        public void send(String pushId, String message, Map<String, String> headers);
    }
}
