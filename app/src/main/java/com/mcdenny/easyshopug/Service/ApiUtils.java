package com.mcdenny.easyshopug.Service;

public class ApiUtils {
    private ApiUtils(){}
    public static final String BASE_URL = "https://ericssonbasicapi2.azure-api.net/collection/v1_0/";
    public static ApiService getApiService(){
        return RetrofitClient.getClient(BASE_URL).create(ApiService.class);
    }
}