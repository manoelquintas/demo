package com.example.demo;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;


import static com.example.demo.UtilsCertificates.*;

@Service
public class CertificadoDigital {


   // @Bean
    public void ler() throws Exception {

        //String tokenCaminho = "D:\\Certificados\\elton\\EltonA1-21581993.pfx";
        //String senha = "123456";
        String tokenCaminho = "https://certificadoexpedit.s3.amazonaws.com/Elder07388894403Certificado.pfx";
        String senha = "SportRecife1987";

        PrivateKey chavePrivada = getChavePrivada(tokenCaminho, senha);




        Connection.Response executeTjpe = Jsoup.connect("https://pje1g.trf1.jus.br/pje/login.seam")
                .followRedirects(true)
                //.proxy(Proxy.usarProxy(2))
                .method(Connection.Method.GET).execute();

        Map<String, String> cookiesTribunal = executeTjpe.cookies();
        String paginaLogin = executeTjpe.parse().toString();
        //ASSINATURA_MENSAGEM = "
        //String mensagem = Utils.getStringBetweenTwoChars(paginaLogin, "ASSINATURA_MENSAGEM = \"", "\";");

        String mensagem = (paginaLogin.contains("ASSINATURA_MENSAGEM"))?Utils.getStringBetweenTwoChars(paginaLogin, "ASSINATURA_MENSAGEM = \"", "\";"):Utils.getStringBetweenTwoChars(paginaLogin, "class=\"hidden\">", "</");
        //String cid = (paginaLogin.contains("CONVERSATION_ID =")) ? Utils.getStringBetweenTwoChars(paginaLogin, "CONVERSATION_ID = '", "';") : Utils.getStringBetweenTwoChars(paginaLogin, "CONVERSATION_ID = \"", "\";");
        String cid = Utils.getStringBetweenTwoChars(paginaLogin, "CONVERSATION_ID = \"", "\";");

        String Jsession = Utils.getStringBetweenTwoChars(paginaLogin, "JSESSION_ID = \"JSESSIONID=", "\";");
        cookiesTribunal.put("JSESSIONID",Jsession);

        String certChain = getCertChain(tokenCaminho, senha);
        Signature assinatura = Signature.getInstance("MD5withRSA");
        assinatura.initSign(chavePrivada);
        assinatura.update(mensagem.getBytes(StandardCharsets.UTF_8), 0, mensagem.length());

        byte[] assinaturaDigital = assinatura.sign();

        String assinaturaTexto = Base64.getEncoder().encodeToString(assinaturaDigital);
        //String assinaturaTexto = base64Encode(assinaturaDigital);

        Map<String, String> dadosForm = new HashMap<>();
        dadosForm.put("assinatura",assinaturaTexto);
        dadosForm.put("cadeiaCertificado",certChain);
        String linkFinalTRT = "https://pje.trt6.jus.br/primeirograu/logarSC.seam?cid="+cid;
        String linkFinalTrf6 = "https://pje2g.trf6.jus.br/pje/homePJeOffice.seam?cid="+cid;
        Connection.Response execute = Jsoup
                .connect("https://pje1g.trf1.jus.br/pje/logarPJeOffice.seam?cid="+cid)
                .ignoreContentType(true)
                .headers(getDataHeaderStep2())
                .ignoreHttpErrors(true)
                //.header("Cookie",)
                //.proxy(Proxy.usarProxy(2))
                //.cookies(cookiesTribunal)
                .sslSocketFactory(socketFactory())
                .data(dadosForm)
                .followRedirects(false).method(Connection.Method.POST).execute();
        cookiesTribunal.putAll(execute.cookies());
//para aqui
        Document parse1 = execute.parse();
        String location = "https://pje1g.trf6.jus.br/pje/logarPJeOffice.seam?cid="+cid;

        Connection.Response execute2 = Jsoup
                .connect("https://pje.trt8.jus.br/pje-seguranca/api/token/perfis")
                .headers(getDataHeaderStep2())
                //.header("Referer", "https://pje.trt6.jus.br/pje-seguranca/api/token/perfis")
                .cookies(cookiesTribunal)
                .proxy(Proxy.usarProxy(2))
                //.sslSocketFactory(socketFactory())
                .ignoreContentType(true)
                .followRedirects(true).method(Connection.Method.GET).execute();
        Map<String, String> cookies = execute2.cookies();
        Document parse = execute2.parse();
        System.out.println(cookies);


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
            SSLContext sslContext = SSLContext.getInstance("TLS");
            final char[] password = "pjeoffice".toCharArray();
            KeyStore keyStore = KeyStore.getInstance("JKS");
            //InputStream input = getClass().getResourceAsStream("/PJeOfficeSSL.jks");
            //https://certificadoexpedit.s3.amazonaws.com/pjeoffice/PJeOffice.jks
            //FileInputStream fis = new FileInputStream("PJeOffice.jks");
            InputStream fis = new URL("https://certificadoexpedit.s3.amazonaws.com/pjeoffice/PJeOffice.jks").openStream();
            keyStore.load(fis, password);
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
            keyManagerFactory.init(keyStore, password);
            sslContext.init(keyManagerFactory.getKeyManagers(), null, null);

            // sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
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
        //headersStep.put("Host", "pje2g.trf6.jus.br");
        //headersStep.put("Origin", "null");
        headersStep.put("Sec-Fetch-Mode", "navigate");
        headersStep.put("Sec-Fetch-Site", "same-origin");
        headersStep.put("Sec-Fetch-User", "?1");
        headersStep.put("Upgrade-Insecure-Requests", "1");
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/111.0.0.0 Safari/537.36");

        return headersStep;
    }
}
