package ch.snapoli.lab.uaf.uafapi.domain;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class InMemoryDeviceRepository implements DeviceRepository {

    private final Map<String, Device> map = new HashMap<>();

    @Override
    public List<Device> findAllWithPushIdAndPublicKey(String userId) {
        return Optional.ofNullable(map.get(userId)).map(v -> Arrays.asList(v)).orElse(Collections.emptyList());
    }

    @Override
    public void save(Device device) {
        map.put(device.getId(), device);
    }
}
