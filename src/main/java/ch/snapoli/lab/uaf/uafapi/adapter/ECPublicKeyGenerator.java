package ch.snapoli.lab.uaf.uafapi.adapter;

import ch.snapoli.lab.uaf.uafapi.domain.PublicKeyGenerator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.security.PublicKey;

@Slf4j
public class ECPublicKeyGenerator implements PublicKeyGenerator {

    @Override
    @SneakyThrows
    public PublicKey create(String x, String y) {
        try {
            return ECSACryptor.createPublic(x, y);
        }
        catch (Exception e){
            log.warn(e.getMessage());
            throw new PublicKeyNotValidException();
        }
    }
}
