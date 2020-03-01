package ch.snapoli.lab.uaf.uafapi.domain;

import java.security.PublicKey;

public interface PublicKeyStorageRepository {
    void store(String deviceId, PublicKey publicKey);

    PublicKey get(String deviceId);
}
