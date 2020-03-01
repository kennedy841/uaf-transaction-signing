package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.StartedChallengeRequest;
import ch.snapoli.lab.uaf.uafapi.domain.ResourceChallengeStorage;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class InMemoryResourceChallengeStorage implements ResourceChallengeStorage {

    private Map<String, StartedChallengeRequest> map = new HashMap<>();

    @Override
    public void store(String challengeId, StartedChallengeRequest payload) {
        map.put(challengeId, payload);
    }

    @Override
    public Optional<StartedChallengeRequest> get(String challengeId) throws NoChallengeFound {
        return Optional.ofNullable(map.get(challengeId));
    }

    @Override
    public void remove(String challengeId) {
        map.remove(challengeId);
    }
}
