package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.SignatureValidator;
import lombok.SneakyThrows;

import java.security.PublicKey;
import java.util.Base64;

public class ECSignatureValidator implements SignatureValidator {

    @Override
    @SneakyThrows
    public boolean validate(PublicKey publicKey, String originalMessage, String signature) {
        byte[] encoded = publicKey.getEncoded();
        return ECSACryptor.verify(Base64.getEncoder().encodeToString(encoded), originalMessage, signature);
    }
}
