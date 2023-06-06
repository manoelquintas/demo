package com.example.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.example.demo.UtilsCertificates.getCertChain;
import static com.example.demo.UtilsCertificates.getChavePrivada;

@Service
public class CertificadoDigitalSsoComCnjAuth {
    static String getAlphaNumericString(int n) {
        // chose a Character random from this String
        String AlphaNumericString = "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int) (AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }

   // @Bean
    public void ler() throws Exception {
        int usarProxy = 16;
        Gson gson = new Gson();

        String mensagem = "0." + getAlphaNumericString(12);
        String token = UUID.randomUUID().toString();

        try {

            String tokenCaminho = "https://certificadoexpedit.s3.amazonaws.com/Elder07388894403Certificado.pfx";
            String senha = "SportRecife1987";

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
            Connection.Response executeTjpe = Jsoup.connect("https://pje1g.trf1.jus.br/pje/login.seam")
                    .followRedirects(true).proxy(Proxy.usarProxy(usarProxy)).method(Connection.Method.GET).execute();
            Map<String, String> cookiesTribunal = executeTjpe.cookies();

            //Passa cookieInicial e pega OAuth_Token_Request_State
            Connection.Response executeTjpeSso = Jsoup.connect("https://pje1g.trf1.jus.br/pje/authenticateSSO.seam")
                    .followRedirects(false)
                    .cookies(cookiesTribunal)
                    .proxy(Proxy.usarProxy(usarProxy))
                    .method(Connection.Method.GET).execute();
            cookiesTribunal.putAll(executeTjpeSso.cookies());


            String locationStart = executeTjpeSso.header("Location");
            // URI uri = new URI("https://sso.cloud.pje.jus.br/auth/realms/pje/protocol/openid-connect/auth?response_type=code&client_id=pje-tjpe-1g&redirect_uri=https%253A%252F%252Fpje.tjpe.jus.br%252F1g%252FauthenticateSSO.seam&state=4bb62944-e404-4486-8108-dc25bf0a8e8b&login=true&scope=openid");
            Connection.Response execute1 = Jsoup.connect(locationStart)
                    .followRedirects(true).proxy(Proxy.usarProxy(usarProxy))
                    .method(Connection.Method.GET).execute();

            Map<String, String> cookiesSso = execute1.cookies();

            Document parse = execute1.parse();
            String action = parse.select("[id=kc-form-login]").attr("action");

            //faz o envio para o servidor com assiantura, certchain, token e messagem apra autorizar retornar 204 se for sucesso
            Connection.Response execute = Jsoup
                    .connect("https://sso.cloud.pje.jus.br/auth/realms/pje/pjeoffice-rest")
                    //.headers(getDataHeaderStep2())
                    .header("Content-type", "application/json")
                    .header("Accept", "application/json")
                    //.header("Cookie", sessao)
                    .requestBody(jsonDsso)
                    .proxy(Proxy.usarProxy(usarProxy))
                    .followRedirects(false).method(Connection.Method.POST).execute();
            //cookiesSso.putAll(execute.cookies());
            //Executa o envio do token e prhase para verificar se foi autorizado
            String location = null;
            while (location == null) {
                Connection.Response execute2 = Jsoup.connect(action)
                        // .headers(getDataHeaderStep2())
                        .data("username", "")
                        .data("password", "")
                        .data("pjeoffice-code", token)
                        .cookies(cookiesSso)
                        .proxy(Proxy.usarProxy(usarProxy))
                        //.timeout(999999)
                        .ignoreHttpErrors(true)
                        .data("phrase", mensagem)
                        .followRedirects(false).method(Connection.Method.POST).execute();
                cookiesSso.putAll(execute2.cookies());
                location = execute2.header("Location");
                System.out.println(location);
            }
            String locationTribunal = null;
            //executa o location que foi pega
            while (locationTribunal == null) {
                Connection.Response execute3 = Jsoup.connect(location)
                        .ignoreHttpErrors(true)
                        .ignoreContentType(true)
                        .headers(getDataHeaderStep2())
                        // .header("Host", "pje.tjpe.jus.br")
                        .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")

                        .cookies(cookiesTribunal)
                        .proxy(Proxy.usarProxy(usarProxy))
                        .followRedirects(false).method(Connection.Method.GET).execute();
                locationTribunal = execute3.header("Location");
            }
            //Faz o Excute para o location tribunal
            Connection.Response executeFinal = Jsoup
                    .connect(locationTribunal)
                    .cookies(cookiesTribunal)
                    .headers(getDataHeaderStep2())
                    .proxy(Proxy.usarProxy(usarProxy))
                    .followRedirects(false).method(Connection.Method.GET).execute();

            cookiesTribunal.putAll(executeFinal.cookies());
            String location1 = executeFinal.header("Location");
            //  headersStep.put("Content-type", "application/json");
            //acessar de fato a pagina do tribunal com a sessao ativa

            Connection.Response executeFinal2 = Jsoup
                    .connect(location1)
                    .headers(getDataHeaderStep2())
                    .cookies(cookiesTribunal)
                    .proxy(Proxy.usarProxy(usarProxy))
                    .followRedirects(false).method(Connection.Method.GET).execute();
            Document buscaCertificado = executeFinal2.parse();
            //String cid = Utils.getStringBetweenTwoChars(location1, "cid=", "");
            String enviaPara = Utils.getStringBetweenTwoChars(buscaCertificado.toString(), "\"enviarPara\"\t: \"", "\"");
            String messagemAssinar = Utils.getStringBetweenTwoChars(buscaCertificado.toString(), "\"mensagem\"\t\t: \"", "\"");

            String certChainNovo = getCertChain(tokenCaminho, senha);
            Signature assinaturaNova = Signature.getInstance("MD5withRSA");
            assinaturaNova.initSign(chavePrivada);
            assinaturaNova.update(messagemAssinar.getBytes(StandardCharsets.UTF_8), 0, messagemAssinar.length());
            byte[] assinaturaDigitalNova = assinaturaNova.sign();
            String assinaturaTextoNovo = Base64.getEncoder().encodeToString(assinaturaDigitalNova);

            Map<String, String> dadosForm = new HashMap<>();
            dadosForm.put("assinatura", assinaturaTextoNovo);
            dadosForm.put("cadeiaCertificado", certChainNovo);

            Connection.Response executenovo = Jsoup
                    .connect("https://pje1g.trf1.jus.br/pje" + enviaPara)
                    .ignoreContentType(true)
                    .headers(getDataHeaderStep2())
                    .ignoreHttpErrors(true)
                    .sslSocketFactory(socketFactory())
                    .data(dadosForm)
                    .cookies(cookiesTribunal)
                    .followRedirects(false).method(Connection.Method.POST).execute();
            cookiesTribunal.putAll(executenovo.cookies());
            // Document parse1 = executenovo.parse();
            String location2 = executenovo.header("Location");
            Connection.Response executenovoFim = Jsoup
                    .connect(location2)
                    .ignoreContentType(true)
                    .headers(getDataHeaderStep2())
                    .ignoreHttpErrors(true)
                    .sslSocketFactory(socketFactory())
                    .cookies(cookiesTribunal)
                    .followRedirects(true).method(Connection.Method.GET).execute();
            Document parse1 = executenovoFim.parse();
            System.out.println("enviarPara");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }


    }

    public HashMap<String, String> getDataHeaderStep2() {
        HashMap<String, String> headersStep = new HashMap<String, String>();
        headersStep.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        headersStep.put("Accept-Encoding", "gzip, deflate, br");
        headersStep.put("Accept-Language", "pt-BR,pt;q=0.9,en-US;q=0.8,en;q=0.7,es;q=0.6");
        headersStep.put("Cache-Control", "max-age=0");
        headersStep.put("Connection", "keep-alive");
        headersStep.put("Content-Type", "application/x-www-form-urlencoded");
        headersStep.put("host", "sso.cloud.pje.jus.br");
        headersStep.put("Sec-Fetch-Mode", "navigate");
        headersStep.put("Sec-Fetch-Site", "same-origin");
        headersStep.put("Sec-Fetch-User", "?1");
        headersStep.put("Upgrade-Insecure-Requests", "1");
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36");

        return headersStep;
    }

    public SSLSocketFactory socketFactory() {
        try {
            InputStream input = new URL("https://certificadoexpedit.s3.amazonaws.com/pjeoffice/PJeOffice.jks").openStream();

            KeyStore clientStore = KeyStore.getInstance("JKS");
            clientStore.load(input, "pjeoffice".toCharArray());

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
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    public SSLSocketFactory socketFactory2() {
        try {
            InputStream input = new URL("https://certificadoexpedit.s3.amazonaws.com/pjeoffice/PJeOfficeSSL.jks").openStream();

            KeyStore clientStore = KeyStore.getInstance("JKS");
            clientStore.load(input, "pjeoffice".toCharArray());

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
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

}
