package ch.snapoli.lab.uaf.uafapi.adapter;


import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECNamedCurveParameterSpec;
import org.bouncycastle.jce.spec.ECNamedCurveSpec;
import org.bouncycastle.util.encoders.Hex;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.ECPublicKey;
import java.security.spec.*;
import java.util.Base64;

public class ECSACryptor {


    public static final String SHA_256_WITH_ECDSA = "SHA256withECDSA";
    public static final String SECP_256_R_1 = "secp256r1";
    //public static final String SECP_256_K_1 = "secp256k1";


    public static KeyPair generateKeys() throws Exception {
        ECGenParameterSpec ecSpec = new ECGenParameterSpec(SECP_256_R_1);
        KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
        g.initialize(ecSpec, new SecureRandom());
        KeyPair keypair = g.generateKeyPair();
        return keypair;
    }


    public static String sign(String privateKeyBase64, String plaintext) throws Exception {

        byte[] decode = Base64.getDecoder().decode(privateKeyBase64);

        PrivateKey privateKey = KeyFactory.getInstance("EC").generatePrivate(new PKCS8EncodedKeySpec(decode));

        return sign(privateKey, plaintext);
    }



    public static String sign(PrivateKey privateKey, String plaintext) throws Exception{
        Signature ecdsaSign = Signature.getInstance(SHA_256_WITH_ECDSA);
        ecdsaSign.initSign(privateKey);
        ecdsaSign.update(plaintext.getBytes("UTF-8"));
        byte[] signature = ecdsaSign.sign();
        return Base64.getEncoder().encodeToString(signature);
    }


    public static PublicKey createPublic(String base64) throws Exception {
        //first 2 char are the encoding
        String s1 = new String(Hex.toHexString(Base64.getDecoder().decode(base64)));
        s1 = s1.substring(2);


        BigInteger x = new BigInteger(s1.substring(0, s1.length() / 2), 16);

        BigInteger y = new BigInteger(s1.substring(s1.length() / 2), 16);


        return createPublic(x.toString(), y.toString());

    }

    public static PublicKey createPublic(String x, String y) throws Exception {
        ECPoint point = new ECPoint(new BigInteger(x), new BigInteger(y));
        ECNamedCurveParameterSpec parameterSpec = ECNamedCurveTable.getParameterSpec(SECP_256_R_1);
        ECParameterSpec spec = new ECNamedCurveSpec(SECP_256_R_1, parameterSpec.getCurve(), parameterSpec.getG(), parameterSpec.getN(), parameterSpec.getH(), parameterSpec.getSeed());
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        ECPublicKey ecPublicKey = (ECPublicKey) keyFactory.generatePublic(new ECPublicKeySpec(point, spec));

        return ecPublicKey;

    }

    public static boolean verify(String publicKeyBase64, String message, String signatureBase64) throws Exception {

        EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64));

        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        return verify(publicKey, message, signatureBase64);
    }

    public static boolean verify(PublicKey publicKey, String message,String signatureBase64) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, UnsupportedEncodingException {
        Signature ecdsaVerify = Signature.getInstance(SHA_256_WITH_ECDSA);

        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(message.getBytes("UTF-8"));
        byte[] decode = Base64.getDecoder().decode(signatureBase64);
        return ecdsaVerify.verify(decode);
    }


}
