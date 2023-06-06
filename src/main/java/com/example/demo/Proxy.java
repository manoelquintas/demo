package com.example.demo;

import org.apache.cxf.transport.http.ReferencingAuthenticator;

import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;

public class Proxy {

    public static java.net.Proxy usarProxy(int usarProxy) {


        java.net.Proxy proxy = null;
        if (usarProxy == 0) {
            setAuthNull(" ", " ");
            // proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("localhost", 8080));

        } else if (usarProxy == 1) {
            setAuth("brd-customer-hl_65569181-zone-zone2", "f6q2ja2bs68i");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 2) {
            setAuth("brd-customer-hl_65569181-zone-data_center-session-glob_expedti102932", "j82yenzqs6wu");
            //setAuth("brd-customer-hl_65569181-zone-zone2", "f6q2ja2bs68i");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 3) {
            setAuth("expedit123", "Expedit102938_country-br_city-recife_session-xfrti6z4_lifetime-10m");
            //setAuth("brd-customer-hl_65569181-zone-zone2", "f6q2ja2bs68i");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("geo.iproyal.com", 12321));
        } else if (usarProxy == 4) {
            setAuth("expedit123", "Expedit102938_country-br");
            //setAuth("brd-customer-hl_65569181-zone-zone2", "f6q2ja2bs68i");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("geo.iproyal.com", 12321));
        } else if (usarProxy == 5) {
            setAuth("brd-customer-hl_65569181-zone-zone3web", "1sw8ha8mhs2n");
            //setAuth("brd-customer-hl_65569181-zone-zone2", "f6q2ja2bs68i");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 7) {
            setAuth("brd-customer-hl_65569181-zone-staticips-session-glob_expedti102938" + "123456", "n53vtnvp1x57");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 8) {
            setAuth("brd-customer-hl_65569181-zone-residencialpayforusage-country-br-session-glob_expedti1029388", "nddry6t7lkj3");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 9) {
            setAuth("brd-customer-hl_65569181-zone-datacenteripdedicatedbr", "6ti2pms834ju");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("zproxy.lum-superproxy.io", 22225));
        } else if (usarProxy == 10) {
            setAuth("user-lu4812970-region-br", "Expedit$102938");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("na.lunaproxy.com", 12233));
        } else if (usarProxy == 11) {
            String[] proxie = RandomicProxy.randomicListProxiesCheap();
            setAuth(proxie[2], proxie[3]);
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxie[0], Integer.valueOf(proxie[1])));
        } else if (usarProxy == 12) {
            setAuth("14a30c5b122b5", "f305158e85");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("177.136.236.205", 12323));
        }else if (usarProxy == 13) {
            setAuth("francisco_expedit_com_br", "Expedit_102938-country-BR");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("la.residential.rayobyte.com", 8000));
        } else if (usarProxy == 14) {
            String[] proxie = RandomicProxy.randomicListProxiesCheapSocks5();
            setAuth(proxie[2], proxie[3]);
            proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress(proxie[0], Integer.valueOf(proxie[1])));

        }
        else if (usarProxy == 15) {
            setAuth("0dm0eBydTdcbiGO", "xSSum5I1dRoSspK");
            proxy = new java.net.Proxy(java.net.Proxy.Type.SOCKS, new InetSocketAddress("200.234.172.38", 59421));
        }
        else if (usarProxy == 16) {
            setAuth("geonode_vDNi2FOWS9-country-BR", "8926b0aa-5003-4517-bf36-82e4cd3e56f7");
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress("rotating-residential.geonode.com", 9000));
        }
        else if (usarProxy == 17) {
            String[] proxie = RandomicProxy.randomicListProxiesSeller();
            setAuth(proxie[2], proxie[3]);
            proxy = new java.net.Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(proxie[0], Integer.valueOf(proxie[1])));
        }
        return proxy;
    }

    public static void setAuth(String user, String pass) {
        //AuthCacheValue.setAuthCache(new AuthCacheImpl());
        //Apache CXF
        ReferencingAuthenticator.setDefault(
                new Authenticator() {
                    public PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(
                                user, pass.toCharArray());
                    }
                }
        );
    }

    public static void setAuthNull(String user, String pass) {
        //AuthCacheValue.setAuthCache(new AuthCacheImpl());
        //Apache CXF
        ReferencingAuthenticator.setDefault(null);
    }
}
