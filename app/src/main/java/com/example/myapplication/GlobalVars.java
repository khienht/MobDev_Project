package com.example.myapplication;

public class GlobalVars {
//    public static String baseUrl = "https://103.126.161.199/";
    public static String baseUrl = "https://orssl.switzerlandnorth.cloudapp.azure.com/";
    public static String redirectUrl = baseUrl + "swagger/oauth2-redirect.html";
    public static String getCodeUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=openremote&redirect_uri=https%3A%2F%2Forssl.switzerlandnorth.cloudapp.azure.com%2Fswagger%2Foauth2-redirect.html&state=AAAA";
    public static String signUpUrl = baseUrl + "auth/realms/master/protocol/openid-connect/auth?response_type=code&client_id=openremote&redirect_uri=https%3A%2F%2Forssl.switzerlandnorth.cloudapp.azure.com%2Fswagger%2Foauth2-redirect.html&state=AAAA";
    public static String authType = "authorization_code";
    public static String client = "openremote";
    public static String LOG_TAG = "API LOG";

}
