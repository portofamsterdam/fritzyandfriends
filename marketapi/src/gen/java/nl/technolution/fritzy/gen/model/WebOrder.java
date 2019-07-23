/*
 * Fritzy 2.0 Token API
 * This api provides functionality to interact with an ethereum blockchain
 *
 * OpenAPI spec version: 0.0.1
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */

package nl.technolution.fritzy.gen.model;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * WebOrder
 */
public class WebOrder   {
  @JsonProperty("signature")
  private String signature = null;

  @JsonProperty("metaData")
  private Object metaData = null;

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

  @JsonProperty("hash")
  private String hash = null;

  public WebOrder signature(String signature) {
    this.signature = signature;
    return this;
  }

  /**
   * Get signature
   * @return signature
   **/
  @JsonProperty("signature")
  @Schema(description = "")
  public String getSignature() {
    return signature;
  }

  public void setSignature(String signature) {
    this.signature = signature;
  }

  public WebOrder metaData(Object metaData) {
    this.metaData = metaData;
    return this;
  }

  /**
   * Get metaData
   * @return metaData
   **/
  @JsonProperty("metaData")
  @Schema(description = "")
  public Object getMetaData() {
    return metaData;
  }

  public void setMetaData(Object metaData) {
    this.metaData = metaData;
  }

  public WebOrder senderAddress(String senderAddress) {
    this.senderAddress = senderAddress;
    return this;
  }

  /**
   * Get senderAddress
   * @return senderAddress
   **/
  @JsonProperty("senderAddress")
  @Schema(description = "")
  public String getSenderAddress() {
    return senderAddress;
  }

  public void setSenderAddress(String senderAddress) {
    this.senderAddress = senderAddress;
  }

  public WebOrder makerAddress(String makerAddress) {
    this.makerAddress = makerAddress;
    return this;
  }

  /**
   * Get makerAddress
   * @return makerAddress
   **/
  @JsonProperty("makerAddress")
  @Schema(description = "")
  public String getMakerAddress() {
    return makerAddress;
  }

  public void setMakerAddress(String makerAddress) {
    this.makerAddress = makerAddress;
  }

  public WebOrder takerAddress(String takerAddress) {
    this.takerAddress = takerAddress;
    return this;
  }

  /**
   * Get takerAddress
   * @return takerAddress
   **/
  @JsonProperty("takerAddress")
  @Schema(description = "")
  public String getTakerAddress() {
    return takerAddress;
  }

  public void setTakerAddress(String takerAddress) {
    this.takerAddress = takerAddress;
  }

  public WebOrder makerFee(String makerFee) {
    this.makerFee = makerFee;
    return this;
  }

  /**
   * Get makerFee
   * @return makerFee
   **/
  @JsonProperty("makerFee")
  @Schema(description = "")
  public String getMakerFee() {
    return makerFee;
  }

  public void setMakerFee(String makerFee) {
    this.makerFee = makerFee;
  }

  public WebOrder takerFee(String takerFee) {
    this.takerFee = takerFee;
    return this;
  }

  /**
   * Get takerFee
   * @return takerFee
   **/
  @JsonProperty("takerFee")
  @Schema(description = "")
  public String getTakerFee() {
    return takerFee;
  }

  public void setTakerFee(String takerFee) {
    this.takerFee = takerFee;
  }

  public WebOrder makerAssetAmount(String makerAssetAmount) {
    this.makerAssetAmount = makerAssetAmount;
    return this;
  }

  /**
   * Get makerAssetAmount
   * @return makerAssetAmount
   **/
  @JsonProperty("makerAssetAmount")
  @Schema(description = "")
  public String getMakerAssetAmount() {
    return makerAssetAmount;
  }

  public void setMakerAssetAmount(String makerAssetAmount) {
    this.makerAssetAmount = makerAssetAmount;
  }

  public WebOrder takerAssetAmount(String takerAssetAmount) {
    this.takerAssetAmount = takerAssetAmount;
    return this;
  }

  /**
   * Get takerAssetAmount
   * @return takerAssetAmount
   **/
  @JsonProperty("takerAssetAmount")
  @Schema(description = "")
  public String getTakerAssetAmount() {
    return takerAssetAmount;
  }

  public void setTakerAssetAmount(String takerAssetAmount) {
    this.takerAssetAmount = takerAssetAmount;
  }

  public WebOrder makerAssetData(String makerAssetData) {
    this.makerAssetData = makerAssetData;
    return this;
  }

  /**
   * Get makerAssetData
   * @return makerAssetData
   **/
  @JsonProperty("makerAssetData")
  @Schema(description = "")
  public String getMakerAssetData() {
    return makerAssetData;
  }

  public void setMakerAssetData(String makerAssetData) {
    this.makerAssetData = makerAssetData;
  }

  public WebOrder takerAssetData(String takerAssetData) {
    this.takerAssetData = takerAssetData;
    return this;
  }

  /**
   * Get takerAssetData
   * @return takerAssetData
   **/
  @JsonProperty("takerAssetData")
  @Schema(description = "")
  public String getTakerAssetData() {
    return takerAssetData;
  }

  public void setTakerAssetData(String takerAssetData) {
    this.takerAssetData = takerAssetData;
  }

  public WebOrder salt(String salt) {
    this.salt = salt;
    return this;
  }

  /**
   * Get salt
   * @return salt
   **/
  @JsonProperty("salt")
  @Schema(description = "")
  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  public WebOrder exchangeAddress(String exchangeAddress) {
    this.exchangeAddress = exchangeAddress;
    return this;
  }

  /**
   * Get exchangeAddress
   * @return exchangeAddress
   **/
  @JsonProperty("exchangeAddress")
  @Schema(description = "")
  public String getExchangeAddress() {
    return exchangeAddress;
  }

  public void setExchangeAddress(String exchangeAddress) {
    this.exchangeAddress = exchangeAddress;
  }

  public WebOrder feeRecipientAddress(String feeRecipientAddress) {
    this.feeRecipientAddress = feeRecipientAddress;
    return this;
  }

  /**
   * Get feeRecipientAddress
   * @return feeRecipientAddress
   **/
  @JsonProperty("feeRecipientAddress")
  @Schema(description = "")
  public String getFeeRecipientAddress() {
    return feeRecipientAddress;
  }

  public void setFeeRecipientAddress(String feeRecipientAddress) {
    this.feeRecipientAddress = feeRecipientAddress;
  }

  public WebOrder expirationTimeSeconds(String expirationTimeSeconds) {
    this.expirationTimeSeconds = expirationTimeSeconds;
    return this;
  }

  /**
   * Get expirationTimeSeconds
   * @return expirationTimeSeconds
   **/
  @JsonProperty("expirationTimeSeconds")
  @Schema(description = "")
  public String getExpirationTimeSeconds() {
    return expirationTimeSeconds;
  }

  public void setExpirationTimeSeconds(String expirationTimeSeconds) {
    this.expirationTimeSeconds = expirationTimeSeconds;
  }

  public WebOrder hash(String hash) {
    this.hash = hash;
    return this;
  }

  /**
   * Get hash
   * @return hash
   **/
  @JsonProperty("hash")
  @Schema(description = "")
  public String getHash() {
    return hash;
  }

  public void setHash(String hash) {
    this.hash = hash;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    WebOrder order = (WebOrder) o;
    return Objects.equals(this.signature, order.signature) &&
        Objects.equals(this.metaData, order.metaData) &&
        Objects.equals(this.senderAddress, order.senderAddress) &&
        Objects.equals(this.makerAddress, order.makerAddress) &&
        Objects.equals(this.takerAddress, order.takerAddress) &&
        Objects.equals(this.makerFee, order.makerFee) &&
        Objects.equals(this.takerFee, order.takerFee) &&
        Objects.equals(this.makerAssetAmount, order.makerAssetAmount) &&
        Objects.equals(this.takerAssetAmount, order.takerAssetAmount) &&
        Objects.equals(this.makerAssetData, order.makerAssetData) &&
        Objects.equals(this.takerAssetData, order.takerAssetData) &&
        Objects.equals(this.salt, order.salt) &&
        Objects.equals(this.exchangeAddress, order.exchangeAddress) &&
        Objects.equals(this.feeRecipientAddress, order.feeRecipientAddress) &&
        Objects.equals(this.expirationTimeSeconds, order.expirationTimeSeconds) &&
        Objects.equals(this.hash, order.hash);
  }

  @Override
  public int hashCode() {
    return Objects.hash(signature, metaData, senderAddress, makerAddress, takerAddress, makerFee, takerFee, makerAssetAmount, takerAssetAmount, makerAssetData, takerAssetData, salt, exchangeAddress, feeRecipientAddress, expirationTimeSeconds, hash);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class WebOrder {\n");
    
    sb.append("    signature: ").append(toIndentedString(signature)).append("\n");
    sb.append("    metaData: ").append(toIndentedString(metaData)).append("\n");
    sb.append("    senderAddress: ").append(toIndentedString(senderAddress)).append("\n");
    sb.append("    makerAddress: ").append(toIndentedString(makerAddress)).append("\n");
    sb.append("    takerAddress: ").append(toIndentedString(takerAddress)).append("\n");
    sb.append("    makerFee: ").append(toIndentedString(makerFee)).append("\n");
    sb.append("    takerFee: ").append(toIndentedString(takerFee)).append("\n");
    sb.append("    makerAssetAmount: ").append(toIndentedString(makerAssetAmount)).append("\n");
    sb.append("    takerAssetAmount: ").append(toIndentedString(takerAssetAmount)).append("\n");
    sb.append("    makerAssetData: ").append(toIndentedString(makerAssetData)).append("\n");
    sb.append("    takerAssetData: ").append(toIndentedString(takerAssetData)).append("\n");
    sb.append("    salt: ").append(toIndentedString(salt)).append("\n");
    sb.append("    exchangeAddress: ").append(toIndentedString(exchangeAddress)).append("\n");
    sb.append("    feeRecipientAddress: ").append(toIndentedString(feeRecipientAddress)).append("\n");
    sb.append("    expirationTimeSeconds: ").append(toIndentedString(expirationTimeSeconds)).append("\n");
    sb.append("    hash: ").append(toIndentedString(hash)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}
