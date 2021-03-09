package com.example.retrofit;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UPost {

    @SerializedName("elementPureHtml")
    @Expose
    private String elementPureHtml;

    /**
     * @return The elementPureHtml
     */
    public String getElementPureHtml() {
        return elementPureHtml;
    }
}