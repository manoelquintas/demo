package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@Service
public class CertificatesGov {


    //@Bean
    public void ler() throws Exception {

        Connection.Response executeini = Jsoup
                .connect("https://sso.acesso.gov.br/authorize?response_type=code&client_id=autorizar.meu.inss.gov.br&scope=openid email profile govbr_confiabilidades&redirect_uri=https://meu.inss.gov.br/login.html&nonce=1.0610357199413392&state=4.622621351103846")
                .headers(getDataHeaderStep2())
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        Map<String, String> cookies = executeini.cookies();
        String locationIni = executeini.header("Location");

        Connection.Response execute = Jsoup
                .connect(locationIni)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();
        String authId = Utils.getStringBetweenTwoChars(locationIni, "authorization_id=", "");
        Connection.Response execute2 = Jsoup
                .connect("https://certificado.sso.acesso.gov.br/login?client_id=autorizar.meu.inss.gov.br&authorization_id="+authId)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        String location2= execute2.header("Location");

        //String location2 = execute2.header("Location").replace("../../", "");
        cookies.putAll(execute2.cookies());
        Connection.Response execute3 = Jsoup
                .connect(location2)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .ignoreContentType(true)
                .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        String location3 = execute3.header("Location");
        cookies.putAll(execute3.cookies());
        Connection.Response execute4 = Jsoup
                .connect(location3)
                //.headers(getDataHeaderStep2())
                //.cookies(cookies)
                //.ignoreContentType(true)
                .header("Referer","https://sso.acesso.gov.br/")
                //.sslSocketFactory(socketFactory2())
                .timeout(999999)
                .followRedirects(true).method(Connection.Method.GET).execute();
        String tokenIni = Utils.getStringBetweenTwoChars(location3, "code=", "&");
        Document parse = execute4.parse();

        Connection.Response execute5 = Jsoup
                .connect("https://vip-pmeuinss-api.inss.gov.br/apis/govbrservices/token/"+tokenIni+"/clientid/autorizar.meu.inss.gov.br")
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                //.header("Referer","https://sso.acesso.gov.br/")
                //.sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();
        Document parse1 = execute5.parse();
        cookies.putAll(execute5.cookies());


        //System.out.println();
       // System.out.println(location);


    }


    public SSLSocketFactory socketFactory2() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        String tokenCaminho = "https://certificadoexpedit.s3.amazonaws.com/Elder07388894403Certificado.pfx";
        String senha = "SportRecife1987";
        InputStream input = new URL("https://certificadoexpedit.s3.amazonaws.com/Elder07388894403Certificado.pfx").openStream();
       // KeyStore clientStore = KeyStore.getInstance("PKCS12");
        //clientStore.load(new FileInputStream("D:\\EltonA1-21581993.pfx"), "123456".toCharArray());

        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(input, "SportRecife1987".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, "SportRecife1987".toCharArray());
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
       // headersStep.put("Referer", "https://projudi.tjpr.jus.br/");
       // headersStep.put(":Scheme:", "https");

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
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/113.0.0.0 Safari/537.36");

        return headersStep;
    }
}
