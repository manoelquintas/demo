package com.example.demo;


import com.example.demo.captcha.SubmitCaptcha;
import com.google.gson.Gson;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URL;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CertificatesTJRJ {


    @Bean
    public void ler() throws Exception {

        java.net.Proxy proxy = Proxy.usarProxy(0);
        Connection.Response execute = Jsoup
                .connect("https://www7.tjrj.jus.br/autenticadorCertDigital/AutenticadorCertDigital.aspx?URLREDIREC=https://www3.tjrj.jus.br/idserverjus-front/#/autenticacao-certificado")
                //.headers(getDataHeaderStep2())
                .sslSocketFactory(socketFactory2())
                .proxy(proxy)
                .followRedirects(false).method(Connection.Method.GET).execute();
        Map<String, String> cookies = execute.cookies();
        String locationIdServerJus = execute.header("Location");

        Connection.Response execute2 = Jsoup
                .connect(locationIdServerJus)
                //.headers(getDataHeaderStep2())
                .cookies(cookies)
                .proxy(proxy)
                .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();
        cookies.putAll(execute2.cookies());
        cookies.put("style", "null");


        String link = "https://www3.tjrj.jus.br/idserverjus-api/certificado?codAutentPublico=" + cookies.get("AUTENTCERTDIGTJRJ") + "&ipRequerente=179.180.149.158";
        Connection.Response execute4 = Jsoup
                .connect(link)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();
        Gson gson = new Gson();
        Map<String, Object> asMap = gson.fromJson(execute4.parse().text(), Map.class);
        String email = asMap.get("email").toString();
        String nome = asMap.get("nome").toString();
        String cpf = asMap.get("cpf").toString();
        String link2 = "https://www3.tjrj.jus.br/idserverjus-api/certificado/obter_idpf?cpf=" + cpf + "&nome=" + nome + "&numoabcd=&email=" + email + "";

        Connection.Response execute5 = Jsoup
                .connect(link2)
                .headers(getDataHeaderStep2())
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();

        String link3 = "https://www3.tjrj.jus.br/idserverjus-api/sessao/sessaoCertificado";

        String body = cpf;
        Connection.Response execute6 = Jsoup
                .connect(link3)
                .requestBody("\"" + cpf + "\"")
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        String json = execute6.parse().text();
        Map<String, Object> asMap2 = gson.fromJson(json, Map.class);
        int idUsu = new BigDecimal(asMap2.get("idUsu").toString()).intValue();
        cookies.put("idUsu", String.valueOf(idUsu));
        String chave = asMap2.get("chave").toString();

        Connection.Response criarJwt = Jsoup
                .connect("https://www3.tjrj.jus.br/idserverjus-api/sessao/criarJwt")
                .requestBody(json)
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        Map<String, Object> asMap3 = gson.fromJson(criarJwt.parse().text(), Map.class);
        String token = asMap3.get("token").toString();
        cookies.put("TOKENJWT", token);

        Connection.Response validaSessaoUsuario = Jsoup
                .connect("https://www3.tjrj.jus.br/idserverjus-api/sessao/validar_sessao?idUsuario=" + idUsu)
                //.requestBody(json)
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(false).method(Connection.Method.GET).execute();
        Document parse = validaSessaoUsuario.parse();

        // String jsonOrgaos="{\"idUsu\":"+idUsu+",\"sgSist\":\"PORTALSERVICOS\"}";

        String jsonRecriarJwt = "{\"token\":\"" + token + "\",\"sgSist\":\"PORTALSERVICOS\",\"codOrgao\":2385}";
        Connection.Response recriarJwt = Jsoup
                .connect("https://www3.tjrj.jus.br/idserverjus-api/sessao/recriarJwt")
                .requestBody(jsonRecriarJwt)
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        Map<String, Object> OrgaoJwtRecriado = gson.fromJson(recriarJwt.parse().text(), Map.class);
        String token1 = OrgaoJwtRecriado.get("token").toString();
        cookies.put("TOKENJWT", token1);


        Connection.Response autenticarnovoJwt = Jsoup
                .connect("https://www3.tjrj.jus.br/idserverjus-api/sessao/autenticarJwt?token=" + token1)

                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();

        Connection.Response portalServicos = Jsoup
                .connect("https://www3.tjrj.jus.br/portalservicos/")

                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Basic dGpyajpzM2NyM3Q=")
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.GET).execute();

        cookies.put("SEGCODORGAO", "2385");
        cookies.put("SEGSESSIONID", chave);
        cookies.put("SIGLASISTEMA", "PORTALSERVICOS");
        cookies.put("SEGCODORGAO", "2385");


        Connection.Response getAuth = Jsoup
                .connect("https://www3.tjrj.jus.br/portalservicos/api/security/jwt-auth")

                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", "Bearer " + chave)
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        Map<String, Object> tokenAuth = gson.fromJson(getAuth.parse().text(), Map.class);
        String jwtAuth = tokenAuth.get("jwt").toString();

        Connection.Response execute9 = Jsoup
                .connect("https://www3.tjrj.jus.br/portalservicos/api/usuarios/atualizar-perfil-logado")
                .requestBody("1")
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .cookies(cookies)
                .header("Authorization", jwtAuth)
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        String tokenFinal = execute9.parse().text().replace("]", "").replace("[", "").replace("\"", "");
        cookies.put("TOKENJWT", tokenFinal);
        cookies.put("Authorization", jwtAuth);

        SubmitCaptcha submitCaptcha1 = new SubmitCaptcha();

        Connection.Response BuscaProcessosPorOab = Jsoup
                .connect("https://www3.tjrj.jus.br/portalservicos/api/processos-oab/")
                .requestBody("{\"origem\":0,\"indProcAtivo\":\"N\",\"anoInicial\":\"2023\",\"anoFinal\":2023,\"processoEletronico\":\"S\"}")
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                //.header("recaptcha-token", responseCaptcha)
                .cookies(cookies)
                .header("Authorization", jwtAuth)
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        String textProcessos = "{\"itensProcesso\":" + BuscaProcessosPorOab.parse().text() + "}";
        Map<String, Object> asMap4 = gson.fromJson(textProcessos, Map.class);

        String responseCaptcha = submitCaptcha1.solveReCaptchaAi("6LcZF04bAAAAAE9QJ62rsPuZYqMxxQC6gUWiDlLU", "https://www3.tjrj.jus.br");
        Connection.Response BuscaProcesso = Jsoup
                .connect("https://www3.tjrj.jus.br/ejud/WS/ConsultaEjud.asmx/DadosProcesso_1")
                .requestBody("{\"nAntigo\":\"202300114973\",\"pCPF\":\"11961096722\",\"pLogin\":\"\"}")
                // .headers(getDataHeaderStep2())
                .header("content-type", "application/json")
                .header("recaptcha-token", responseCaptcha)//2023.251.06557
                .cookies(cookies)
                .header("Authorization", jwtAuth)
                .proxy(proxy)
                .ignoreHttpErrors(true)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();

        //String text = "{\"itensProcesso\":" + BuscaProcesso.parse().text() + "}";
        Map<String, Object> asMap5 = gson.fromJson(BuscaProcesso.parse().text(), Map.class);
        int CodDoc = new BigDecimal(((Map) asMap5.get("d")).get("CodDoc").toString()).intValue();

        String linkAndamentos = "https://www3.tjrj.jus.br/ejud/WS/ConsultaEjud.asmx/ConsultarMovimentosPaginado";
        String linkJsonAndamentos = "{\"codDoc\":\"" + CodDoc + "\",\"numPag\":0}";
        Connection.Response BuscaProcessoAndamentos = Jsoup
                .connect(linkAndamentos)
                .requestBody(linkJsonAndamentos)
                .header("content-type", "application/json")
                .header("Authorization", jwtAuth)
                .ignoreHttpErrors(true)
                .cookies(cookies)
                .ignoreContentType(true)
                //.sslSocketFactory()
                // .sslSocketFactory(socketFactory2())
                .followRedirects(true).method(Connection.Method.POST).execute();
        Map<String, Object> jsonAndamentos = gson.fromJson(BuscaProcessoAndamentos.parse().text(), Map.class);
        asMap5.put("andamentos", (List) jsonAndamentos.get("d"));

        //System.out.println();
        System.out.println(tokenFinal);


    }


    public SSLSocketFactory socketFactory2() throws KeyStoreException,
            NoSuchAlgorithmException, CertificateException, IOException, UnrecoverableKeyException, KeyManagementException {
        String tokenCaminho = "https://certificados-expedit.s3.amazonaws.com/certificado/251/ANDRE_DE_OLIVEIRA_BARBOSA11961096722.pfx";
        String senha = "#Andre165";
        InputStream input = new URL(tokenCaminho).openStream();
        // KeyStore clientStore = KeyStore.getInstance("PKCS12");
        //clientStore.load(new FileInputStream("D:\\EltonA1-21581993.pfx"), "123456".toCharArray());

        KeyStore clientStore = KeyStore.getInstance("PKCS12");
        clientStore.load(input, senha.toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, senha.toCharArray());
        KeyManager[] kms = kmf.getKeyManagers();
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public synchronized void checkClientTrusted(X509Certificate[] chain, String authType)
                            throws java.security.cert.CertificateException {
                    }

                    @Override
                    public synchronized void checkServerTrusted(X509Certificate[] chain, String authType)
                            throws java.security.cert.CertificateException {
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
        headersStep.put("Accept", "application/json, text/plain, */*");
        headersStep.put("Accept-Encoding", "gzip, deflate, br");
        headersStep.put("Accept-Language", "pt-BR,pt;q=0.9");
        headersStep.put("Cache-Control", "no-cache");
        headersStep.put("Connection", "keep-alive");
        headersStep.put("Pragma", "no-cache");
        headersStep.put("Host", "www3.tjrj.jus.br");
        headersStep.put("content-type", "application/json");

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
        headersStep.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:109.0) Gecko/20100101 Firefox/113.0");

        return headersStep;
    }
}
