/*
 (C) COPYRIGHT TECHNOLUTION BV, GOUDA NL
| =======          I                   ==          I    =
|    I             I                    I          I
|    I   ===   === I ===  I ===   ===   I  I    I ====  I   ===  I ===
|    I  /   \ I    I/   I I/   I I   I  I  I    I  I    I  I   I I/   I
|    I  ===== I    I    I I    I I   I  I  I    I  I    I  I   I I    I
|    I  \     I    I    I I    I I   I  I  I   /I  \    I  I   I I    I
|    I   ===   === I    I I    I  ===  ===  === I   ==  I   ===  I    I
|                 +---------------------------------------------------+
+----+            |  +++++++++++++++++++++++++++++++++++++++++++++++++|
     |            |             ++++++++++++++++++++++++++++++++++++++|
     +------------+                          +++++++++++++++++++++++++|
                                                        ++++++++++++++|
                                                                 +++++|
 */
package nl.technolution.fritzy.wallet.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * WebOrder without hash information
 */
public class HashlessOrder {
    @JsonProperty("signature")
    private String signature = null;

    @JsonProperty("senderAddress")
    private String senderAddress = null;

    @JsonProperty("makerAddress")
    private String makerAddress = null;

    @JsonProperty("takerAddress")
    private String takerAddress = null;

    @JsonProperty("makerFee")
    private String makerFee = null;

    @JsonProperty("takerFee")
    private String takerFee = null;

    @JsonProperty("makerAssetAmount")
    private String makerAssetAmount = null;

    @JsonProperty("takerAssetAmount")
    private String takerAssetAmount = null;

    @JsonProperty("makerAssetData")
    private String makerAssetData = null;

    @JsonProperty("takerAssetData")
    private String takerAssetData = null;

    @JsonProperty("salt")
    private String salt = null;

    @JsonProperty("exchangeAddress")
    private String exchangeAddress = null;

    @JsonProperty("feeRecipientAddress")
    private String feeRecipientAddress = null;

    @JsonProperty("expirationTimeSeconds")
    private String expirationTimeSeconds = null;

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getSenderAddress() {
        return senderAddress;
    }

    public void setSenderAddress(String senderAddress) {
        this.senderAddress = senderAddress;
    }

    public String getMakerAddress() {
        return makerAddress;
    }

    public void setMakerAddress(String makerAddress) {
        this.makerAddress = makerAddress;
    }

    public String getTakerAddress() {
        return takerAddress;
    }

    public void setTakerAddress(String takerAddress) {
        this.takerAddress = takerAddress;
    }

    public String getMakerFee() {
        return makerFee;
    }

    public void setMakerFee(String makerFee) {
        this.makerFee = makerFee;
    }

    public String getTakerFee() {
        return takerFee;
    }

    public void setTakerFee(String takerFee) {
        this.takerFee = takerFee;
    }

    public String getMakerAssetAmount() {
        return makerAssetAmount;
    }

    public void setMakerAssetAmount(String makerAssetAmount) {
        this.makerAssetAmount = makerAssetAmount;
    }

    public String getTakerAssetAmount() {
        return takerAssetAmount;
    }

    public void setTakerAssetAmount(String takerAssetAmount) {
        this.takerAssetAmount = takerAssetAmount;
    }

    public String getMakerAssetData() {
        return makerAssetData;
    }

    public void setMakerAssetData(String makerAssetData) {
        this.makerAssetData = makerAssetData;
    }

    public String getTakerAssetData() {
        return takerAssetData;
    }

    public void setTakerAssetData(String takerAssetData) {
        this.takerAssetData = takerAssetData;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getExchangeAddress() {
        return exchangeAddress;
    }

    public void setExchangeAddress(String exchangeAddress) {
        this.exchangeAddress = exchangeAddress;
    }

    public String getFeeRecipientAddress() {
        return feeRecipientAddress;
    }

    public void setFeeRecipientAddress(String feeRecipientAddress) {
        this.feeRecipientAddress = feeRecipientAddress;
    }

    public String getExpirationTimeSeconds() {
        return expirationTimeSeconds;
    }

    public void setExpirationTimeSeconds(String expirationTimeSeconds) {
        this.expirationTimeSeconds = expirationTimeSeconds;
    }

}
