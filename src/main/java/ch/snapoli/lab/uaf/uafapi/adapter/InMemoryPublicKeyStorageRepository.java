package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.PublicKeyStorageRepository;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class InMemoryPublicKeyStorageRepository implements PublicKeyStorageRepository {

    Map<String, PublicKey> map = new HashMap<>();

    @Override
    public void store(String deviceId, PublicKey publicKey) {
       map.put(deviceId, publicKey);
    }

    @Override
    public PublicKey get(String deviceId) {
        return map.get(deviceId);
    }
}
