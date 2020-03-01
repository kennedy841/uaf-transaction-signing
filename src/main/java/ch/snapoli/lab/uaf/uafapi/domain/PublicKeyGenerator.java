package ch.snapoli.lab.uaf.uafapi.domain;

import java.security.PublicKey;

public interface PublicKeyGenerator {

    public PublicKey create(String x, String y) throws PublicKeyNotValidException;

    public class PublicKeyNotValidException extends RuntimeException {
    }
}
