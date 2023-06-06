package com.example.demo;

import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import javax.net.ssl.SSLContext;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;

import java.io.FileInputStream;
import java.net.ProxySelector;
import org.apache.http.impl.client.CloseableHttpClient;
import java.io.InputStream;
//import br.jus.cnj.pje.office.utils.swing.JFrameUtils;
import java.security.KeyStore;

public class HttpClientUtils
{
    private static KeyStore KS;

    public static KeyStore getTrustStore() {
        if (HttpClientUtils.KS == null) {
            try {
                final InputStream in = HttpClientUtils.class.getResourceAsStream("/PJeOfficeSSL.jks");
                FileInputStream fis = new FileInputStream("PJeOffice.jks");
                (HttpClientUtils.KS = KeyStore.getInstance(KeyStore.getDefaultType())).load(fis, "pjeoffice".toCharArray());
                in.close();
            }
            catch (Exception e) {
               // JFrameUtils.showError("Erro", "Erro ao instanciar o truststore PJeOffice.jks, mensagen interna: " + e.getMessage());
            }
        }
        return HttpClientUtils.KS;
    }

    public static CloseableHttpClient getHttpClient(final String url) throws Exception {
        return getHttpClient(url, null);
    }

    public static CloseableHttpClient getHttpClient(final String url, final String headerUserAgent) throws Exception {
        final SystemDefaultRoutePlanner routePlanner = new SystemDefaultRoutePlanner(ProxySelector.getDefault());
        HttpClientBuilder builder;
        if (url.startsWith("https")) {
            final KeyStore ts = getTrustStore();
            final SSLContext context = SSLContexts.custom().loadTrustMaterial(ts).build();
            final SSLConnectionSocketFactory factory = new SSLConnectionSocketFactory(context, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
            SSLContext.setDefault(context);
            builder = HttpClients.custom().setSSLSocketFactory((LayeredConnectionSocketFactory)factory).setRoutePlanner((HttpRoutePlanner)routePlanner);
        }
        else {
            builder = HttpClients.custom().setRoutePlanner((HttpRoutePlanner)routePlanner);
        }
        if (headerUserAgent != null) {
            builder.setUserAgent(headerUserAgent);
        }
        return builder.build();
    }
}