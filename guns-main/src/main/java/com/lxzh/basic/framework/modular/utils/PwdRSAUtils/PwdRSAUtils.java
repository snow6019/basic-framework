package com.lxzh.basic.framework.modular.utils.PwdRSAUtils;

import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import java.security.*;
import java.security.interfaces.RSAPublicKey;

/**
 * @author : zzw
 * @Description: 加解密公共方法类
 * @date : 2020/8/25 8:35
 **/
public class PwdRSAUtils {

    private static final KeyPair keyPair = initKey();
    private static KeyPair initKey() {
        try {
            Provider provider =new BouncyCastleProvider();
            Security.addProvider(provider);
            SecureRandom random = new SecureRandom();
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", provider);
            generator.initialize(1024,random);
            return generator.generateKeyPair();
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static String generateBase64PublicKey() {
        PublicKey publicKey = (RSAPublicKey)keyPair.getPublic();
        return new String(Base64.encodeBase64(publicKey.getEncoded()));
    }
    public static String decryptBase64(String string) {
        return new String(decrypt(Base64.decodeBase64(string.getBytes())));
    }
    private static byte[] decrypt(byte[] byteArray) {
        try {
            Provider provider = new BouncyCastleProvider();
            Security.addProvider(provider);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);
            PrivateKey privateKey = keyPair.getPrivate();
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainText = cipher.doFinal(byteArray);
            return plainText;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        System.out.println(keyPair.getPublic());
        System.out.println(keyPair.getPrivate());
        //System.out.println("获取公钥："+ RSAUtils.generateBase64PublicKey());
        String en_password = "SS4OHKlIb32W4RSuZciCqrsiw6pJnH6qmCc9sY/1YsfQ6zHOeLozID7NCio" +
                "/RrXrSySB4JIl4snhxSVBOvcDWQOAil2MrKsdcPdJS/gsUFbg2v+C9/wCmchQemXNOQlt/l2XTupqsAAl0d4wb4TLAOrzyog+5zc2aKUHcQlb/+k=";
        System.out.println("解析："+ PwdRSAUtils.decryptBase64(en_password));

    }


}
