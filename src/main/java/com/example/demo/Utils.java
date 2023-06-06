package com.example.demo;

public class Utils {

    public static String getStringBetweenTwoChars(String input, String startChar, String endChar) {

        try {
            int start = input.indexOf(startChar);
            if (startChar.isEmpty()) {
                start = 0;
            }
            if (start != -1) {
                int end = input.indexOf(endChar, start + startChar.length());
                if (endChar.isEmpty()) {
                    end = input.length();
                }
                if (end != -1) {
                    return input.substring(start + startChar.length(), end);
                }
            }
        } catch (Exception e) {
           // EnvioErro.envioEmailErro("Erro 5: " + input + " - " + startChar + " - " + endChar + " - " + e.getMessage() + e.getStackTrace(), e);
            e.printStackTrace();
        }
        return null; // return null; || return "" ;
    }


}
