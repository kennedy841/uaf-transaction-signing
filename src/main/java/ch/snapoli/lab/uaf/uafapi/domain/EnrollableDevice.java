package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
@EqualsAndHashCode
public class EnrollableDevice {
    private final String deviceId;
}
