package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CertificatesEsaj {


   // @Bean
    public void ler() throws Exception {
        //  System.setProperty("javax.net.ssl.trustStore", "/certs/clientcertElton.jks");

        String tokenCaminho = "D:\\Certificados\\elton\\EltonA1-21581993.pfx";
        String senha = "123456";
       // String tokenCaminho = "https://certificadoexpedit.s3.amazonaws.com/Elder07388894403Certificado.pfx";
       // String senha = "SportRecife1987";
        //PrivateKey chavePrivada = getChavePrivada(tokenCaminho, senha);
       // String certChain = getCertChain(tokenCaminho, senha);

        Connection.Response execute = Jsoup
                .connect("https://eproc1g.tjrs.jus.br/eproc/externo_controlador.php?acao=login_cert")
                .headers(getDataHeaderStep2())
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        Map<String, String> cookies = execute.cookies();
        String location = execute.header("Location");

        Connection.Response execute2 = Jsoup
                .connect("https://eproc1g.tjrs.jus.br/eproc/" + location)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        String location2 = execute2.header("Location").replace("../../", "");
        cookies.putAll(execute2.cookies());
        Connection.Response execute3 = Jsoup
                .connect("https://eproc1g.tjrs.jus.br/eproc/" + location2)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        String location3 = execute3.header("Location").replace("../../", "");
        cookies.putAll(execute3.cookies());
        Connection.Response execute4 = Jsoup
                .connect("https://eproc1g.tjrs.jus.br/eproc/" + location3)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();

        System.out.println(location);


    }

    public SSLSocketFactory socketFactory() {
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[0];
            }

            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }

            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        }};


        try {


            KeyStore clientStore = KeyStore.getInstance("PKCS12");
            clientStore.load(new FileInputStream("D:\\EltonA1-21581993.pfx"), "123456".toCharArray());
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(clientStore, "123456".toCharArray());
            KeyManager[] kms = kmf.getKeyManagers();

            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(new FileInputStream("D:\\clientcertElton.jks"), "123456".toCharArray());

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(trustStore);
            TrustManager[] tms = tmf.getTrustManagers();

            final SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(kms, tms, new SecureRandom());
            SSLContext.setDefault(sslContext);


            SSLSocketFactory result = sslContext.getSocketFactory();

            return result;
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            throw new RuntimeException("Failed to create a SSL socket factory", e);
        } catch (KeyStoreException e) {
            throw new RuntimeException(e);
        } catch (UnrecoverableKeyException e) {
            throw new RuntimeException(e);
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SSLSocketFactory socketFactory2() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {

        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(new FileInputStream("D:\\EltonA1-21581993.pfx"), "123456".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, "123456".toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public synchronized void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                    @Override
                    public synchronized void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws CertificateException {
                    }
                    @Override
                    public synchronized X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                }
        };

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kms, trustAllCerts, new SecureRandom());
        SSLSocketFactory result = sslContext.getSocketFactory();
        return result;
    }

    public HashMap<String, String> getDataHeaderStep2() {
        HashMap<String, String> headersStep = new HashMap<String, String>();
        headersStep.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7");
        headersStep.put("Accept-Encoding", "gzip, deflate, br");
        headersStep.put("Accept-Language", "pt-BR,pt;q=0.9");
        headersStep.put("Cache-Control", "no-cache");
        headersStep.put("Connection", "keep-alive");
        headersStep.put("Pragma", "no-cache");

        headersStep.put("sec-ch-ua", "\"Google Chrome\";v=\"111\", \"Not(A:Brand\";v=\"8\", \"Chromium\";v=\"111\"");
        headersStep.put("sec-ch-ua-mobile", "?0");
        headersStep.put("sec-ch-ua-platform", "\"Windows\"");
        headersStep.put("Sec-Fetch-Dest", "document");
       // headersStep.put("Host", "pje2g.trf6.jus.br");
        //headersStep.put("Origin", "null");
        headersStep.put("Sec-Fetch-Mode", "navigate");
        headersStep.put("Sec-Fetch-Site", "same-origin");
        headersStep.put("Sec-Fetch-User", "?1");
        headersStep.put("Upgrade-Insecure-Requests", "1");
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");

        return headersStep;
    }
}
