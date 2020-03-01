package ch.snapoli.lab.uaf.uafapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.security.KeyPair;
import java.security.PublicKey;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

import static org.assertj.core.api.Assertions.*;

@Slf4j
class CryptorTest {

    String MESSAGE = "100";


    @Test
    void fullEncryptionProcess() throws Exception {

        KeyPair keyPair = ECSACryptor.generateKeys();

        String priv = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());


        String pub = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());

        ECPublicKey aPublic = (ECPublicKey) keyPair.getPublic();

        PublicKey publicKey = ECSACryptor.createPublic(aPublic.getW().getAffineX().toString(), aPublic.getW().getAffineY().toString());

        log.info("pub {}", aPublic);
        log.info("priv {} {}",  keyPair.getPrivate().getFormat(), priv);


        assertThat(aPublic).isEqualTo(publicKey);


        String sign = ECSACryptor.sign(priv, MESSAGE);

        log.info("signed data {}", sign);

        boolean verify = ECSACryptor.verify(pub, MESSAGE, sign);

        assertThat(verify).isTrue();
    }


    @Test
    void testCryptAndDecrypt() throws Exception {

        String message = "{" +
                "\"resource\":\"f657f006-7a27-4406-8c88-a5274d2fba08\"," +
                "\"nonce\":49751721" +
                "}";


        log.info(message);


        String priv = "MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDp3EVV9Czu7h3AjmHfy8b09u2HEnxt4dS89k1IKTtXWg==";

        PublicKey publicKey = ECSACryptor.createPublic(
                "108626610445227267021216753531011922029775994809025001798123780253931362059287",
                "13775089719733830772307592798135791007264440289750994841598233847637616238615");

        String pub = Base64.getEncoder().encodeToString(publicKey.getEncoded());


        String signatureBase64 = ECSACryptor.sign(priv, message);

        log.info(signatureBase64);

        boolean verify = ECSACryptor.verify(pub, message, signatureBase64);

        assertThat(verify).isTrue();


    }

    @Test
    void testExternalKey() throws Exception {
        //String publicKey ="MFYwEAYHKoZIzj0CAQYFK4EEAAoDQgAEWXByGRcAgaJJ7ZhpToAFLa+/3ihaVc+j1MkQ5uJAxynPJ9C6ERKVJrMiFpa4p2I8Wn7u8J288y/u23dNbXaXDw==";
        String privateKey ="MEECAQAwEwYHKoZIzj0CAQYIKoZIzj0DAQcEJzAlAgEBBCDp3EVV9Czu7h3AjmHfy8b09u2HEnxt4dS89k1IKTtXWg==";

        PublicKey publicKey = ECSACryptor.createPublic(
                "108626610445227267021216753531011922029775994809025001798123780253931362059287",
                "13775089719733830772307592798135791007264440289750994841598233847637616238615");

        String signature="MEUCIQCDX6snvZveSS46FY74pY/1e7pRw+23x5IhKfl/OT3x6AIgYmd1Kz0Vk2WEmmeoI64kBkX9e374F8V5MQvF/SPvcrA=";


        boolean verify = ECSACryptor.verify(publicKey, MESSAGE, signature);

        assertThat(verify).isTrue();


    }
}