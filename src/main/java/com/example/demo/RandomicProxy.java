package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Service
@AllArgsConstructor
@Slf4j
public class RandomicProxy {

    public static String[] randomicListProxiesCheap() {
        List<String> proxies = new ArrayList<>();
        proxies.add("200.234.172.52:45776:Y8YM6vg8mZICjJK:ew8LglJHuy0HeJ0");
        proxies.add("200.234.173.251:42272:FifJ7MVWXnsxQPF:XMApeOcmUNSDrfn");
        proxies.add("200.234.173.34:44416:7yHoR5Vx4HtjLsa:YLukgPHQm7mh7Jz");
        proxies.add("200.234.173.185:41549:AAoC3hQYskRoccx:z2rH52cF7QEDkeA");
        proxies.add("200.234.173.144:47667:U2xxL9rCDWV8B9o:iVXOInAI9fI7QTE");
        proxies.add("200.234.173.37:46603:2IzFjt09oOyy8DD:RMWbPdzo1zdn2eB");
        proxies.add("200.234.173.106:41705:QWBpvIHRhousWjw:mYusPBlCQzCFL3j");
        proxies.add("200.234.172.74:41765:R5tm07I1MFLrSGr:AsJHswrHliqV1Ls");
        proxies.add("200.234.172.177:44855:2sPamS4PigT2LIp:w9lNJ65h0f5OMem");
        proxies.add("200.234.172.158:43364:ol5uiqEnz4NrSlw:zo7CR8K1xV0U8vE");


        // proxies.add("200.234.173.229:43205:Y8hUo657OQHv8e3:W6JjZhQklmyyh5u");
        int rnd = new Random().nextInt(proxies.size());

        String resultado = proxies.get(rnd);
        return resultado.split(":");

    }

    public static String[] randomicListProxiesCheapSocks5() {
        List<String> proxies = new ArrayList<>();
        proxies.add("200.234.172.164:54767:mTf0KP07TkMx487:BddMltCEzjKoEvj");
       proxies.add("200.234.172.202:58274:eEPZsUjcWew1pxz:XrMJ9NFs03lmAjo");
        proxies.add("200.234.172.224:51777:bn1UYe5Ez72u7XA:DHlKaqRyXLaglJ7");
        proxies.add("200.234.172.5:51935:9ZqGCG4BhMzramT:KKla2sn9JlIAS83");
        proxies.add("200.234.173.155:52598:QZgSSviXPAVfyAf:w3Ox9yLc5WfxnP9");
        proxies.add("200.234.173.218:57060:nz3OFKQYhEqUocd:wRYdxPD3cqYeZQQ");
        proxies.add("200.234.173.61:56496:ZumUzMkWHIeyfdD:oycqEKXwCd2eB7O");
        proxies.add("200.234.173.76:51672:lSTjnUzipy62vUC:VSkIuIOyLYRYLIF");
        proxies.add("200.234.172.38:59421:0dm0eBydTdcbiGO:xSSum5I1dRoSspK");



        proxies.add("200.234.173.19:48236:5Ffj5WZWlkgyxBL:kWky0t5B8H3B9cR");
        /*proxies.add("200.234.173.132:41574:bOJ0NDmD8Vz45z4:DY2ZdSSAZfeNDIo");
        proxies.add("200.234.173.229:43205:Y8hUo657OQHv8e3:W6JjZhQklmyyh5u");*/

        int rnd = new Random().nextInt(proxies.size());
       // proxies.add("200.234.173.150:59235:aXkdVnXdoqyFAmK:FFMdJEL1mgxhvnk");
      //  proxies.add("200.234.172.13:45935:y9y8yZiLz6LqZhg:fB9rfvVeExZ8jJY");
       // proxies.add("200.234.172.143:47520:yfGihgI1MbsbdYP:WbD3tcIKb8SmdGa");
        // proxies.add("200.234.172.222:43450:4pG17jGAEI9L2Nw:rIaNShTzXyKaYtY");
        String resultado = proxies.get(rnd);
        return resultado.split(":");

    }


    public static String[] randomicListProxiesSeller() {
        List<String> proxies = new ArrayList<>();
        proxies.add("177.136.248.183:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.182:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.167:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.165:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.164:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.169:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.168:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.170:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.174:50100:Selmanoelquintas:K6c0EsU");
        proxies.add("177.136.248.173:50100:Selmanoelquintas:K6c0EsU");


        int rnd = new Random().nextInt(proxies.size()-1);

        String resultado = proxies.get(rnd);
        return resultado.split(":");

    }
}
