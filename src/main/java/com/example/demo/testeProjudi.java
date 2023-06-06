package com.example.demo;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

@Service
public class testeProjudi {
    /*
     * // truststore default, caso importe as cadeias de certificados para o truststore default do java
     * private final String truststoreType = KeyStore.getDefaultType();
     * private final String truststorePath = System.getenv("JAVA_HOME") + "/lib/security/cacerts";
     * private final String truststorePassword = "changeit";
     */

    // truststore personalizado usando o keytool a partir das cadeias de certificados do SERPRO
    private final String truststoreType = "JKS";
    private final String truststorePath = "/clientcertElton.jks";
    private final String truststorePassword = "123456";

    private final String keystoreType = "PKCS12";
    private final String keystorePath = "EltonA1-21581993.pfx";
    private final String keystorePassword = "123456";
    private final String url = "https://eproc1g.tjsc.jus.br/eproc/externo_controlador.php?acao=login_cert";



  //  @Bean
    public void run() {
        try {
            OkHttpClient httpsClient = createHttpsClient();
            Request request = new Request.Builder().url(url).build();

            Response response = httpsClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new RuntimeException("Unexpected code " + response);
            }

            System.out.println(response.body().string());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public OkHttpClient createHttpsClient() throws Exception {
        long timeout = 120l;
        OkHttpClient httpsClient = new OkHttpClient();
        httpsClient.setConnectTimeout(timeout, TimeUnit.SECONDS);
        httpsClient.setWriteTimeout(timeout, TimeUnit.SECONDS);
        httpsClient.setReadTimeout(timeout, TimeUnit.SECONDS);
        httpsClient.setSslSocketFactory(createSslSocketFactory());
        return httpsClient;
    }

    public SSLSocketFactory createSslSocketFactory() throws Exception {
        try (InputStream keyInputStream = new FileInputStream(keystorePath);
             InputStream trustInputStream = new FileInputStream(truststorePath)) {

            KeyStore clientStore = KeyStore.getInstance(keystoreType);
            char[] clientPassArray = keystorePassword.toCharArray();
            clientStore.load(keyInputStream, clientPassArray);

            KeyStore trustStore = KeyStore.getInstance(truststoreType);
            trustStore.load(trustInputStream, truststorePassword.toCharArray());

            KeyManagerFactory keyManagerFactory = KeyManagerFactory
                    .getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(clientStore, clientPassArray);
            KeyManager[] keyManagers = keyManagerFactory.getKeyManagers();

            TrustManagerFactory trustManagerFactory = TrustManagerFactory
                    .getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();

            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:" + Arrays.toString(trustManagers));
            }

            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(keyManagers, trustManagers, new SecureRandom());

            return sslContext.getSocketFactory();
        } catch (Exception ex) {
            throw ex;
        }
    }
}
