package com.example.demo;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.PrivateKey;
import java.security.Signature;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import static com.example.demo.UtilsCertificates.getCertChain;
import static com.example.demo.UtilsCertificates.getChavePrivada;

@Service
public class CertificadoDigitalTRTs {


   // @Bean
    public void ler() throws Exception {

        String tokenCaminho = "D:\\Certificados\\elton\\EltonA1-21581993.pfx";
        String senha = "123456";
       // String tokenCaminho = "D:\\Certificados\\Elder\\Elder07388894403Certificado.pfx";
      //  String senha = "SportRecife1987";
        PrivateKey chavePrivada = getChavePrivada(tokenCaminho, senha);

        String certChain = getCertChain(tokenCaminho, senha);

        //Signature assinatura = Signature.getInstance("MD5withRSA");
        Signature assinatura = Signature.getInstance("MD5withRSA");

        assinatura.initSign(chavePrivada);
        AlgorithmParameters parameters = assinatura.getParameters();

        assinatura.update("m4Mqk8GbHaLWNUkvevcAhCCdjg2yK2d0".getBytes(StandardCharsets.UTF_8), 0, "m4Mqk8GbHaLWNUkvevcAhCCdjg2yK2d0".length());

        byte[] assinaturaDigital = assinatura.sign();

        String assinaturaTexto = Base64.getEncoder().encodeToString(assinaturaDigital);
        String certchain1="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";
        Map<String, String> dadosForm = new HashMap<>();
        dadosForm.put("signature",assinaturaTexto);
        dadosForm.put("certChain",certchain1);
        dadosForm.put("certChainStringLog","");

        Map<String, String> dadoscookies = new HashMap<>();

         dadoscookies.put("JSESSIONID","8c905685b2711445~DmQ4UalErQFVCpJ5g0A7T1VZ");

         Connection.Response execute = Jsoup
                .connect("https://pje.trt6.jus.br/primeirograu/logarSC.seam")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .cookies(dadoscookies)

                //.data(dadosForm)
                .followRedirects(false).method(Connection.Method.POST).execute();

        String location = execute.header("Location");
        dadoscookies.putAll(execute.cookies());
        Connection.Response execute2 = Jsoup
                .connect(location)
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .cookies(dadoscookies)
                .followRedirects(false).method(Connection.Method.POST).execute();
        Document parse = execute2.parse();
        System.out.println(assinaturaTexto);
        System.out.println("privateKey");

        Connection.Response executeGov = Jsoup
                .connect("https://pje.trt6.jus.br/primeirograu/logarSC.seam")
                .ignoreContentType(true)
                .ignoreHttpErrors(true)
                .cookies(dadoscookies)
                .data(dadosForm)
                   .followRedirects(false).method(Connection.Method.POST).execute();



    }
}
