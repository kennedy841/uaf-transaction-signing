package ch.snapoli.lab.uaf.uafapi.adapter;

import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Hex;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static ch.snapoli.lab.uaf.uafapi.adapter.ECSACryptor.SECP_256_R_1;
import static org.assertj.core.api.Assertions.*;

@Slf4j
class CryptorTest {

    String MESSAGE = "100";


    private static final Map<Integer, String> sizeToName = new HashMap<>();
    private static final Map<Integer, byte[]> sizeToHead = new HashMap<>();

    static {
        sizeToName.put(192, "secp192r1");
        sizeToName.put(224, "secp224r1");
        sizeToName.put(256, "secp256r1");
        sizeToName.put(384, "secp384r1");
        // sizeToName.put(521, "secp521r1");
    }



    private static ECPublicKey cvtToJavaECKey(byte[] value)
            throws GeneralSecurityException {

        BigInteger[] xy = split(value);
        ECPoint w = new ECPoint(xy[0], xy[1]);
        // see ECConstants re: casting

        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(SECP_256_R_1);
        ECParameterSpec spec = new ECNamedCurveSpec(SECP_256_R_1, parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());


        ECPublicKeySpec ks = new ECPublicKeySpec(w, spec);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return (ECPublicKey) kf.generatePublic(ks);
    }


    @Test
    void test() throws Exception {


        PublicKey aPublic = ECSACryptor.createPublic("BKRwKvOe/YJNUbrHj2SUrH4+MnbMc1B8WrG1+LJ03o+2m6zDMv3uIxnalAxy3+7QckZoHy5uCaxSugkuhq7Nv34=");

        boolean salvatore = ECSACryptor.verify(aPublic, "salvatore", "MEQCIHu4bQJdJPVkHqxOQOtmVV+G2G/0XqbQpnAsv/EKXKZxAiBfAHVqa6D4L53WpNIhQDnTahe4h94IpuxOsVOAXGBYDQ==");

        assertThat(salvatore).isTrue();


    }

    @Test
    void testIos() throws Exception {


        //PublicKey aPublic = ECSACryptor.createPublic("BKRwKvOe/YJNUbrHj2SUrH4+MnbMc1B8WrG1+LJ03o+2m6zDMv3uIxnalAxy3+7QckZoHy5uCaxSugkuhq7Nv34=");


        PublicKey aPublic = ECSACryptor.createPublic("23675567641527102509708364301710253103409365631772336247028012748885832145114", "51417934331474511331852887543135280707385002321631467092525758575699207301508");

        boolean salvatore = ECSACryptor.verify(aPublic, "salvatore", "MEUCIGuyONxDZ7aiNa9domFB5Av9kzBWM3X9MCSvBjKj0mT2AiEAroo6lFIV4e9DGfn70wBfRdNNGKgdUaSDk/gzwMvbNXc=");

        assertThat(salvatore).isTrue();


    }


    private static byte[] createHeadForNamedCurve(int size)
            throws NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        String name = sizeToName.get(size);
        ECGenParameterSpec m = new ECGenParameterSpec(name);
        kpg.initialize(m);
        KeyPair kp = kpg.generateKeyPair();
        byte[] encoded = kp.getPublic().getEncoded();
        return Arrays.copyOf(encoded, encoded.length - 2 * (size / Byte.SIZE));
    }


    public static ECPublicKey decodeECPublicKey(byte[] pubKeyBytes)
            throws InvalidKeySpecException,
            InvalidKeyException,
            NoSuchAlgorithmException,
            InvalidAlgorithmParameterException {
        // uncompressed keys only


        byte[] w = Arrays.copyOfRange(pubKeyBytes, 1, pubKeyBytes.length);
        int size = w.length / 2 * Byte.SIZE;
        byte[] head = sizeToHead.get(size);
        if (head == null) {
            head = createHeadForNamedCurve(size);
            sizeToHead.put(size, head);
        }

        byte[] encodedKey = new byte[head.length + w.length];
        System.arraycopy(head, 0, encodedKey, 0, head.length);
        System.arraycopy(w, 0, encodedKey, head.length, w.length);
        KeyFactory eckf;
        try {
            eckf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("EC key factory not present in runtime");
        }

        X509EncodedKeySpec ecpks = new X509EncodedKeySpec(encodedKey);
        try {
            return (ECPublicKey) eckf.generatePublic(ecpks);
        } catch (Exception e) {

            throw e;
        }
    }

    /**
     *  Split a byte array into two BigIntegers
     *  @param b length must be even
     *  @return array of two BigIntegers
     *  @since 0.9.9
     */
    private static BigInteger[] split(byte[] b) {
        if ((b.length & 0x01) != 0)
            throw new IllegalArgumentException("length must be even");
        int sublen = b.length / 2;
        byte[] bx = new byte[sublen];
        byte[] by = new byte[sublen];
        System.arraycopy(b, 0, bx, 0, sublen);
        System.arraycopy(b, sublen, by, 0, sublen);
        BigInteger x = new BigInteger(1, bx);
        BigInteger y = new BigInteger(1, by);
        return new BigInteger[] {x, y};
    }


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