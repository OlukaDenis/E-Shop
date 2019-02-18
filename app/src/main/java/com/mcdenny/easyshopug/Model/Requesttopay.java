package com.mcdenny.easyshopug.Model;

import com.google.gson.annotations.SerializedName;

public class Requesttopay {
    @SerializedName("X-Reference-Id")
    private String XReferenceId;

    @SerializedName("X-Target-Environment")
    private String XTargetEnvironment;

    @SerializedName("Ocp-Apim-Subscription-Key")
    private String OcpApimSubscriptionKey;

    @SerializedName("Content-Type")
    private String ContentType;
}
