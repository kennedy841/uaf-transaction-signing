package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository {

    public List<Device> findAllWithPushIdAndPublicKey(String userId);

    public void save(Device device);

    @RequiredArgsConstructor
    @Getter
    public static class Device {
        private final String id;
        private final String pushId;
    }


}
