package com.example.demo.captcha;


import br.com.expedit.utils.Utilidades;
import com.google.gson.Gson;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Component
public class SubmitCaptcha {

    private static final String APIKEY = "49b2e6ecb6e07163ad103284ddb85be9";
    private static final String APIKEYANY = "pkg.97804d22400541dd8b23640cde0e";
    private static final String APIKEYANY40 = "pkg.e5e44f2031c74f1eaf0aa11f9e7b";
    private static final String APIKEYMonster = "781021946afcbd3f17effd71c28d256a";

    @Autowired
    RestTemplate restTemplate;

    public String solveCaptcha(String siteKey, String url) {
        String link = "http://2captcha.com/in.php?key=" + APIKEY;
        link += "&method=userrecaptcha&googlekey=" + siteKey;
        link += "&pageurl=" + url + "&invisible=1&json=1";
        ResponseSubmitCaptcha responseSubmite = restTemplate.getForObject(link, ResponseSubmitCaptcha.class);

        log.info(responseSubmite.getRequest());
        ResponseSubmitCaptcha responseSubmitCaptcha = null;
        do {
            try {

                Thread.sleep(5000L);//key=1abc234de56fab7c89012d34e56fa7b8&action=get&id=2122988149
                String linkResponse = "http://2captcha.com/res.php?key=" + APIKEY + "&action=get&";
                linkResponse += "id=" + responseSubmite.getRequest() + "&json=1";
                responseSubmitCaptcha = restTemplate.getForObject(linkResponse, ResponseSubmitCaptcha.class);
                log.info(responseSubmitCaptcha.getRequest());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (responseSubmitCaptcha != null && responseSubmitCaptcha.getRequest().equals("CAPCHA_NOT_READY"));
        //{"status":0,"request":"CAPCHA_NOT_READY"}
        return responseSubmitCaptcha.getRequest();

        //{"status":1,"request":"67841185533"}


    }


    public String solveHCaptcha2Captcha(String siteKey, String url) {
        String link = "http://2captcha.com/in.php?key=" + APIKEY;
        link += "&method=hcaptcha&sitekey=" + siteKey;
        link += "&pageurl=" + url + "&invisible=1&json=1";
        ResponseSubmitCaptcha responseSubmite = restTemplate.getForObject(link, ResponseSubmitCaptcha.class);

        log.info(responseSubmite.getRequest());
        ResponseSubmitCaptcha responseSubmitCaptcha = null;
        do {
            try {

                Thread.sleep(5000L);//key=1abc234de56fab7c89012d34e56fa7b8&action=get&id=2122988149
                String linkResponse = "http://2captcha.com/res.php?key=" + APIKEY + "&action=get&";
                linkResponse += "id=" + responseSubmite.getRequest() + "&json=1";
                responseSubmitCaptcha = restTemplate.getForObject(linkResponse, ResponseSubmitCaptcha.class);
                log.info(responseSubmitCaptcha.getRequest());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (responseSubmitCaptcha != null && responseSubmitCaptcha.getRequest().equals("CAPCHA_NOT_READY"));
        //{"status":0,"request":"CAPCHA_NOT_READY"}
        return responseSubmitCaptcha.getRequest();

        //{"status":1,"request":"67841185533"}


    }

    public String solveCaptcha2Captcha(String siteKey, String url) {
        String link = "http://2captcha.com/in.php?key=" + APIKEY;
        link += "&method=userrecaptcha&googlekey=" + siteKey;
        link += "&pageurl=" + url + "&invisible=1&json=1";
        ResponseSubmitCaptcha responseSubmite = null;
        String resultTaskId = "";
        Gson gson = new Gson();
        try {


            Document document = Jsoup.connect(link).method(Connection.Method.POST).ignoreContentType(true).get();
            Map<String, Object> asMap = gson.fromJson(document.text(), Map.class);
            BigDecimal taskId = new BigDecimal(asMap.get("request").toString());
            resultTaskId = String.valueOf(taskId.longValue());

        } catch (Exception e) {e.printStackTrace();}
        //log.info(responseSubmite.getRequest());
        //ResponseSubmitCaptcha responseSubmitCaptcha = null;
        String status = "";
        Document document = null;
        String request = "";
        do {
            try {

                Thread.sleep(5000L);//key=1abc234de56fab7c89012d34e56fa7b8&action=get&id=2122988149
                String linkResponse = "http://2captcha.com/res.php?key=" + APIKEY + "&action=get&";
                linkResponse += "id=" + resultTaskId + "&json=1";
                //responseSubmitCaptcha = restTemplate.getForObject(linkResponse, ResponseSubmitCaptcha.class);
                document = Jsoup.connect(linkResponse).method(Connection.Method.POST).ignoreContentType(true).get();
                System.out.println(document);

                Map<String, Object> asMap = gson.fromJson(document.text(), Map.class);
                status = asMap.get("status").toString();
                request = asMap.get("request").toString();

                //resultTaskId = String.valueOf(taskId.longValue());

                // log.info(responseSubmitCaptcha.getRequest());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } while (request.equals("CAPCHA_NOT_READY"));
        //{"status":0,"request":"CAPCHA_NOT_READY"}
        return request;

        //{"status":1,"request":"67841185533"}
    }

    public String solveCaptchaAny(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.anycaptcha.com/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\"," +
                            "\"task\": {\"type\": \"RecaptchaV2TaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"isInvisible\": true}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.anycaptcha.com/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }

    public String solveCapMonster(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.capmonster.cloud/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\"," +
                            "\"task\": {\"type\": \"NoCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"isInvisible\": true}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.capmonster.cloud/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("text").toString();
    }

    public String solveCaptchaAny40(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.anycaptcha.com/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY40 + "\"," +
                            "\"task\": {\"type\": \"RecaptchaV2TaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"isInvisible\": true}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.anycaptcha.com/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY40 + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }


    public String solveHCaptchaAny(String siteKey, String url) throws Exception {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.anycaptcha.com/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\"," +
                            "\"task\": {\"type\": \"HCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.anycaptcha.com/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();
            try {
                String json2 = responseCaptcha.getBody();
                Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
                System.out.println(json2);
                try {
                    status = asMap2.get("status").toString();
                    solution = responseCaptcha.getBody();
                } catch (Exception e) {
                    e.printStackTrace();
                    status = asMap2.get("errorCode").toString();
                    if (status.equalsIgnoreCase("ERROR")) {
                        throw new Exception(e.getMessage());
                    }
                }
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();

    }

    public String solveHCaptchaAnyAntiCaptcha(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.anti-captcha.com/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEY + "\"," +
                            "\"task\": {\"type\": \"HCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"}}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.anti-captcha.com/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEY + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }


    public String solveCaptchaAnyV3(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.anycaptcha.com/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\"," +
                            "\"task\": {\"type\": \"RecaptchaV3TaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"minScore\": 0.3}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.anycaptcha.com/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYANY + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }

    public String solveReCaptchaAi(String siteKey, String url) {

        String key = "cbf16cd5599ccd5a1fe0091f48c0b2df";
        String responseID = "";
        try {
            String result = Jsoup.connect("http://ocr.captchaai.com/in.php?key=" + key + "&method=userrecaptcha&version=v3&action=verify&min_score=0.3&googlekey=" + siteKey + "&pageurl=" + url).execute().parse().text();

            String id = Utilidades.getStringBetweenTwoChars(result, "OK|", "");

            String response = Jsoup.connect("http://ocr.captchaai.com/res.php?key=" + key + "&action=get&id=" + id).execute().parse().text();
            while (response.contains("NOT_READY")) {
                Thread.sleep(5000);
                response = Jsoup.connect("http://ocr.captchaai.com/res.php?key=" + key + "&action=get&id=" + id).execute().parse().text();
                System.out.println(response);
            }
            return Utilidades.getStringBetweenTwoChars(response, "OK|", "");

        } catch (Exception e) {

        }
        return null;
    }

    public String solveHCaptchaAi(String siteKey, String url) {

        String key = "cbf16cd5599ccd5a1fe0091f48c0b2df";
        String responseID = "";
        try {
            String result = Jsoup.connect("http://ocr.captchaai.com/in.php?key=" + key + "8&method=hcaptcha&sitekey=" + siteKey + "&pageurl=" + url).execute().parse().text();

            String id = Utilidades.getStringBetweenTwoChars(result, "OK|", "");

            String response = Jsoup.connect("http://ocr.captchaai.com/res.php?key=" + key + "&action=get&id=" + id).execute().parse().text();
            while (response.contains("NOT_READY")) {
                Thread.sleep(5000);
                response = Jsoup.connect("http://ocr.captchaai.com/res.php?key=" + key + "&action=get&id=" + id).execute().parse().text();
                System.out.println(response);
            }
            return Utilidades.getStringBetweenTwoChars(response, "OK|", "");

        } catch (Exception e) {

        }
        return null;
    }



    public String solveCaptchaMonsterCap(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.capmonster.cloud/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\"," +
                            "\"task\": {\"type\": \"NoCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"isInvisible\": true}}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.capmonster.cloud/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }

    public String solveHCaptchaMonsterCap(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.capmonster.cloud/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\"," +
                            "\"task\": {\"type\": \"HCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"}}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.capmonster.cloud/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }

    public String solveHCaptchaMonsterCapInvisible(String siteKey, String url) {
        String responseID = "";
        try {

            HttpResponse<String> response = Unirest.post("https://api.capmonster.cloud/createTask")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\"," +
                            "\"task\": {\"type\": \"HCaptchaTaskProxyless\"," +
                            "\"websiteURL\": \"" + url + "\"," +
                            "\"websiteKey\": \"" + siteKey + "\"," +
                            "\"isInvisible\": true}}")
                    .asString();
            responseID = response.getBody();
            Thread.sleep(2000L);
            System.out.println(response.getBody());


        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Gson gson = new Gson();
        String json = responseID;
        Map<String, Object> asMap = gson.fromJson(json, Map.class);
        BigDecimal taskId = new BigDecimal(asMap.get("taskId").toString());
        String resultTaskId = String.valueOf(taskId.longValue());

        String status = "";
        String solution = "";
        do {
            HttpResponse<String> responseCaptcha = Unirest.post("https://api.capmonster.cloud/getTaskResult")
                    .header("Content-Type", "application/json")
                    .body("{\"clientKey\": \"" + APIKEYMonster + "\",\"taskId\": " + resultTaskId + "\r\n}")
                    .asString();

            String json2 = responseCaptcha.getBody();
            Map<String, Object> asMap2 = gson.fromJson(json2, Map.class);
            System.out.println(json2);
            status = asMap2.get("status").toString();
            solution = responseCaptcha.getBody();
            try {
                Thread.sleep(5000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (!status.equals("ready"));


        String jsonFinal = solution;
        Map<String, Object> asMapFinal = gson.fromJson(jsonFinal, Map.class);
        Map<String, Object> resultados = (Map<String, Object>) asMapFinal.get("solution");
        //System.out.println(resultados.get("gRecaptchaResponse"));
        return resultados.get("gRecaptchaResponse").toString();
    }

}
