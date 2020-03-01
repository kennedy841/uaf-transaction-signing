package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.EnrollDeviceChallengeRepository;
import ch.snapoli.lab.uaf.uafapi.domain.StartedChallengeRequest;
import ch.snapoli.lab.uaf.uafapi.domain.EnrollableDevice;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryEnrollDeviceChallengeRepository implements EnrollDeviceChallengeRepository {


    private Map<ChallengeId, ChallengeEntry> map = new HashMap<>();


    @Override
    public ChallengeId create(EnrollableDevice deviceId, StartedChallengeRequest startedChallenge) {
        ChallengeId generate = ChallengeId.generate();
        map.put(generate, new ChallengeEntry(startedChallenge, deviceId));
        return generate;
    }

    @Override
    public void remove(EnrollableDevice enrollableDevice) {
        Optional<Map.Entry<ChallengeId, ChallengeEntry>> first = map.entrySet().stream().filter(entry -> {
            return entry.getValue().getEnrollableDevice().equals(enrollableDevice);
        }).findFirst();

        first.ifPresent(challengeIdChallengeEntryEntry -> map.remove(challengeIdChallengeEntryEntry.getKey()));

    }

    @Override
    public Optional<ChallengeEntry> find(ChallengeId challengeId) {
        return Optional.ofNullable(map.get(challengeId));
    }

    @RequiredArgsConstructor
    @Data
    private static class Entry{
        private final String data;
        private final String id;
    }
}
