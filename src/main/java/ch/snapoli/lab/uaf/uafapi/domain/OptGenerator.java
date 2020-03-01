package ch.snapoli.lab.uaf.uafapi.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public interface OptGenerator {

    Otp generate(String userId);

    @RequiredArgsConstructor
    @Getter
    public static class Otp {
        private final int list;
        private final int index;
    }
}
