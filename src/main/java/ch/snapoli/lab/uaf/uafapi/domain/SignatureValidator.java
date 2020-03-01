package ch.snapoli.lab.uaf.uafapi.domain;

import java.security.PublicKey;

public interface SignatureValidator {
    boolean validate(PublicKey publicKey, String originalMessage, String signatureBase64);
}
