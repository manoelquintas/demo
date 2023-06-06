package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.demo.UtilsCertificates.getCertChain;
import static com.example.demo.UtilsCertificates.getChavePrivada;

@RestController
@RequestMapping("/pjeOffice")
@Slf4j
public class RecebeAssinar {

    @GetMapping("/requisicao/")
    public void executarCreta(String r, String u) {

        System.out.println(r);
        System.out.println(u);

        Gson gson = new Gson();
        Map<String, Object> asMap = gson.fromJson(r, Map.class);

        //String sessao = asMap.get("sessao").toString();
       // String servidor = asMap.get("servidor").toString();
       // String codigoSeguranca = asMap.get("codigoSeguranca").toString();
       // String tarefa = asMap.get("tarefa").toString();
       // Map<String, Object> asMapTarefa = gson.fromJson(tarefa, Map.class);
       // String enviarPara = asMapTarefa.get("enviarPara").toString();
        String mensagem = "0."+getAlphaNumericString(12);//asMapTarefa.get("mensagem").toString();
        String token = UUID.randomUUID().toString();//asMapTarefa.get("token").toString();
        //String endPoint = servidor + enviarPara;

        try {
            String tokenCaminho = "D:\\Certificados\\elton\\EltonA1-21581993.pfx";
            String senha = "123456";

            PrivateKey chavePrivada = getChavePrivada(tokenCaminho, senha);

            String certChain = getCertChain(tokenCaminho, senha);
            Signature assinatura = Signature.getInstance("MD5withRSA");

            assinatura.initSign(chavePrivada);
            assinatura.update(mensagem.getBytes(StandardCharsets.UTF_8), 0, mensagem.length());

            byte[] assinaturaDigital = assinatura.sign();
            String assinaturaTexto = Base64.getEncoder().encodeToString(assinaturaDigital);

            DadosSSO dsso = new DadosSSO(assinaturaTexto, certChain, token, mensagem);
            final ObjectMapper om = new ObjectMapper();
            final String jsonDsso = om.writeValueAsString((Object) dsso);

            //pegaCookieInicial
            Connection.Response executeTjpe = Jsoup.connect("https://pje.tjpe.jus.br/1g/login.seam")
                    .followRedirects(true).method(Connection.Method.GET).execute();
            Map<String, String> cookiesTribunal = executeTjpe.cookies();

            //Passa cookieInicial e pega OAuth_Token_Request_State
            Connection.Response executeTjpeSso = Jsoup.connect("https://pje.tjpe.jus.br/1g/authenticateSSO.seam")
                    .followRedirects(false).method(Connection.Method.GET).execute();
            cookiesTribunal.putAll(executeTjpeSso.cookies());
            String locationStart = executeTjpeSso.header("Location");


            Connection.Response execute1 = Jsoup.connect(locationStart)
                    .followRedirects(true).method(Connection.Method.GET).execute();

            Map<String, String> cookiesSso = execute1.cookies();

            Document parse = execute1.parse();
            String action = parse.select("[id=kc-form-login]").attr("action");

            //faz o envio para o servidor com assiantura, certchain, token e messagem apra autorizar retornar 204 se for sucesso
            Connection.Response execute = Jsoup
                    .connect("https://sso.cloud.pje.jus.br/auth/realms/pje/pjeoffice-rest")
                    .headers(getDataHeaderStep2())
                    .header("Content-type", "application/json")
                    .header("Accept", "application/json")
                    //.header("Cookie", sessao)
                    .requestBody(jsonDsso)
                    .sslSocketFactory(socketFactory())
                    .followRedirects(false).method(Connection.Method.POST).execute();

            //Executa o envio do token e prhase para verificar se foi autorizado
            Connection.Response execute2 = Jsoup.connect(action)
                    .headers(getDataHeaderStep2())
                    .sslSocketFactory(socketFactory())
                    .data("username", "")
                    .data("password", "")
                    .data("pjeoffice-code", token)
                    .cookies(cookiesSso)
                    .data("phrase", mensagem)
                    .followRedirects(false).method(Connection.Method.POST).execute();
            cookiesSso.putAll(execute2.cookies());
            String location = execute2.header("Location");

            //executa o location que foi pega
            Connection.Response execute3 = Jsoup.connect(location)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .header("Host", "pje.tjpe.jus.br")
                    .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
                    .sslSocketFactory(socketFactory())
                    .cookies(cookiesTribunal)
                    .followRedirects(false).method(Connection.Method.GET).execute();
            String locationTribunal = execute3.header("Location");

            //Faz o Excute para o location tribunal
            Connection.Response executeFinal = Jsoup
                    .connect(locationTribunal)
                    .cookies(cookiesTribunal)
                    .followRedirects(false).method(Connection.Method.GET).execute();
            cookiesTribunal.putAll(executeFinal.cookies());

            //acessar de fato a pagina do tribunal com a sessao ativa
            Connection.Response executeFinalTribunal = Jsoup
                    .connect("https://pje.tjpe.jus.br/1g/Processo/ConsultaProcesso/listView.seam")
                    .cookies(cookiesTribunal)
                    .followRedirects(true).method(Connection.Method.GET).execute();
            Document parse1 = executeFinalTribunal.parse();
            System.out.println("enviarPara");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        System.out.println("enviarPara");

    }

    public HashMap<String, String> getDataHeaderStep2() {
        HashMap<String, String> headersStep = new HashMap<String, String>();
        headersStep.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headersStep.put("Accept-Encoding", "gzip, deflate, br");
        headersStep.put("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6");
        headersStep.put("Cache-Control", "max-age=0");
        headersStep.put("Connection", "keep-alive");
        //  headersStep.put("Content-Length", "206");
        //headersStep.put("Content-Type", "application/x-www-form-urlencoded");
        headersStep.put("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
        headersStep.put("sec-ch-ua-mobile", "?0");
        headersStep.put("sec-ch-ua-platform", "\"Windows\"");
        headersStep.put("sec-ch-ua-platform", "\"Windows\"");
        headersStep.put("Sec-Fetch-Dest", "document");
        headersStep.put("host", "sso.cloud.pje.jus.br");
        headersStep.put("Origin", "null");
        headersStep.put("Sec-Fetch-Mode", "navigate");
        headersStep.put("Sec-Fetch-Site", "same-origin");
        headersStep.put("Sec-Fetch-User", "?1");
        headersStep.put("Upgrade-Insecure-Requests", "1");
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

        return headersStep;
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
            InputStream input = getClass().getResourceAsStream("/PJeOfficeSSL.jks");
            FileInputStream fis = new FileInputStream("PJeOffice.jks");
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

    static String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
        String AlphaNumericString =  "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

}
