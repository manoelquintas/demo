package com.example.demo;

import lombok.val;
import org.bouncycastle.util.encoders.Base64Encoder;

import java.io.*;
import java.net.URL;
import java.security.*;
import java.security.cert.CertPath;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;

import static com.example.demo.Certificates.toByteArray;

public class UtilsCertificates {
    private static byte[] ENC_BASE64 = new byte[]{
            65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84,
            85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108,
            109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50,
            51, 52, 53, 54, 55, 56, 57, 43, 47
    };
    private static byte[] DEC_BASE64;
    static {
        DEC_BASE64 = new byte[128];
        for (int i = 0; i < ENC_BASE64.length; ++i) {
            DEC_BASE64[ENC_BASE64[i]] = (byte)i;
        }
    }
    public static PrivateKey getChavePrivada(String caminho, String senha) throws Exception {
        char[] senhaChars = senha.toCharArray();

       // InputStream entrada = new FileInputStream(caminho);
        InputStream entrada = new URL(caminho).openStream();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(entrada, senhaChars);
        entrada.close();
        Enumeration<String> aliases = ks.aliases();
        String alias = aliases.nextElement();
        Key chavePrivada = (Key) ks.getKey(alias, senhaChars);
        if (chavePrivada instanceof PrivateKey) {
            System.out.println("Chave Privada encontrada!");
            return (PrivateKey) chavePrivada;
        }
        return null;
    }

    public static byte[] criarAssinatura(PrivateKey chavePrivada, byte[] buffer) throws Exception {

        Signature assinatura = Signature.getInstance("MD5withRSA");
        assinatura.initSign(chavePrivada);
        assinatura.update(buffer, 0, buffer.length);
        return assinatura.sign();
    }

    public static String getCertChain(String caminhoCertificado, String senha) throws Exception {
        char[] senhaChars = senha.toCharArray();
        //InputStream entradaCertificado = new FileInputStream(caminhoCertificado);
        InputStream entradaCertificado =  new URL(caminhoCertificado).openStream();
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(entradaCertificado, senhaChars);
        Enumeration<String> aliases = ks.aliases();
        String alias = aliases.nextElement();
        List<Certificate> certificateChain = List.of(ks.getCertificateChain(alias));
         byte[] bytes = toByteArray(certificateChain);
        String certChain = base64Encode(bytes);
        return certChain;

    }

    public static String getCertChain2(String caminhoCertificado, String senha) throws Exception {
        char[] senhaChars = senha.toCharArray();
        InputStream entradaCertificado = new FileInputStream(caminhoCertificado);
        KeyStore ks = KeyStore.getInstance("PKCS12");
        ks.load(entradaCertificado, senhaChars);
        Enumeration<String> aliases = ks.aliases();
        String alias = aliases.nextElement();
        List<Certificate> certificateChain = List.of(ks.getCertificateChain(alias));
        String s = encodeCertChain(ks.getCertificateChain(alias));
       // byte[] bytes = Certificates.toByteArray(certificateChain);
        //String certChain = base64Encode(bytes);
        return s;

    }

    public static String getCertChainReplace(String caminhoCertificado, String senha) {
        try {
            char[] senhaChars = senha.toCharArray();
            InputStream entradaCertificado =  new URL(caminhoCertificado).openStream();
            KeyStore ks = KeyStore.getInstance("PKCS12");
            ks.load(entradaCertificado, senhaChars);
            Enumeration<String> aliases = ks.aliases();
            val alias = aliases.nextElement();
            List<Certificate> certificateChain = List.of(ks.getCertificateChain(alias));
            byte[] bytes = toByteArray(certificateChain);
            var hash = base64Encode(bytes);
            return hash.replace("/", "%2F").replace("+", "%2B");
        } catch (KeyStoreException | CertificateException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    private static final Base64Encoder encoder;
    private static char[] Base64Map;

    public static String encodeCertChain(final Certificate[] certChain) throws CertificateException, IOException {
        final List<Certificate> certList = Arrays.asList(certChain);
        final CertificateFactory certFactory = CertificateFactory.getInstance("X.509");
        final CertPath certPath = certFactory.generateCertPath(certList);
        final byte[] encodedCertChain = certPath.getEncoded("PkiPath");
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encoder.encode(encodedCertChain, 0, encodedCertChain.length, (OutputStream)baos);
        final String ret = new String(baos.toByteArray());
        return ret;
    }
    static {
        encoder = new Base64Encoder();
        Base64Map = new char[] { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/', '=' };
    }

    public static boolean isEmpty(Collection<?> c) {
        return c == null || c.size() == 0;
    }

    public static String base64Encode(final byte[] data) {
        //requireNonEmpty(data, "byte array can't be empty");
        final byte[] encodedBuf = new byte[(data.length + 2) / 3 * 4];
        int srcIndex = 0;
        int destIndex = 0;
        while (srcIndex < data.length - 2) {
            encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] >>> 2 & 0x3F];
            encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 1] >>> 4 & 0xF) | (data[srcIndex] << 4 & 0x3F)];
            encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 2] >>> 6 & 0x3) | (data[srcIndex + 1] << 2 & 0x3F)];
            encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex + 2] & 0x3F];
            srcIndex += 3;
        }
        if (srcIndex < data.length) {
            encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] >>> 2 & 0x3F];
            if (srcIndex < data.length - 1) {
                encodedBuf[destIndex++] = ENC_BASE64[(data[srcIndex + 1] >>> 4 & 0xF) | (data[srcIndex] << 4 & 0x3F)];
                encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex + 1] << 2 & 0x3F];
            } else {
                encodedBuf[destIndex++] = ENC_BASE64[data[srcIndex] << 4 & 0x3F];
            }
        }
        while (destIndex < encodedBuf.length) {
            encodedBuf[destIndex] = 61;
            ++destIndex;
        }
        final String result = new String(encodedBuf);
        return result;
    }

    public static byte[] base64Decode(final String aData) {
        // requireText(aData, "unabled to decode empty data");
        byte[] data;
        int tail;
        for (data = aData.getBytes(), tail = data.length; data[tail - 1] == 61; --tail) {}
        final byte[] decodedBuf = new byte[tail - data.length / 4];
        for (int i = 0; i < data.length; ++i) {
            data[i] = DEC_BASE64[data[i]];
        }
        int srcIndex = 0;
        int destIndex;
        for (destIndex = 0; destIndex < decodedBuf.length - 2; destIndex += 3) {
            decodedBuf[destIndex] = (byte) ((data[srcIndex] << 2 & 0xFF) | (data[srcIndex + 1] >>> 4 & 0x3));
            decodedBuf[destIndex + 1] = (byte) ((data[srcIndex + 1] << 4 & 0xFF) | (data[srcIndex + 2] >>> 2 & 0xF));
            decodedBuf[destIndex + 2] = (byte) ((data[srcIndex + 2] << 6 & 0xFF) | (data[srcIndex + 3] & 0x3F));
            srcIndex += 4;
        }
        if (destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte) ((data[srcIndex] << 2 & 0xFF) | (data[srcIndex + 1] >>> 4 & 0x3));
        }
        if (++destIndex < decodedBuf.length) {
            decodedBuf[destIndex] = (byte) ((data[srcIndex + 1] << 4 & 0xFF) | (data[srcIndex + 2] >>> 2 & 0xF));
        }
        return decodedBuf;
    }
}
