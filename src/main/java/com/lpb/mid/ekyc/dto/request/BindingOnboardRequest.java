package com.lpb.mid.ekyc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BindingOnboardRequest {
    @NotEmpty(message = "refNo không được để trống ")
    @JsonProperty("refNo")
    private String refNo;

    @NotEmpty(message = "ekycDeviceId không được để trống ")
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;

    @NotEmpty(message = "ekycTrustLvl không được để trống ")
    @JsonProperty("ekycTrustLvl")
    private String ekycTrustLvl;

    @NotEmpty(message = "ekycBiometricId không được để trống ")
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;

    @NotEmpty(message = "ekycSessionId không được để trống ")
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;

    @NotEmpty(message = "ekycType không được để trống ")
    @JsonProperty("ekycType")
    private String ekycType;


    @NotEmpty(message = "dateOfBirth không được để trống ")
    @JsonProperty("dateOfBirth")
    private String dateOfBirth;

    @NotEmpty(message = "gender không được để trống ")
    @JsonProperty("gender")
    private String gender;

    @NotEmpty(message = "idCode không được để trống ")
    @JsonProperty("idCode")
    private String idCode;

    @NotEmpty(message = "idIssueDate không được để trống ")
    @JsonProperty("idIssueDate")
    private String idIssueDate;

    @NotEmpty(message = "idIssuePlace không được để trống ")
    @JsonProperty("idIssuePlace")
    private String idIssuePlace;

    @NotEmpty(message = "custName không được để trống ")
    @JsonProperty("custName")
    private String custName;

    @NotEmpty(message = "fullAddress không được để trống ")
    @JsonProperty("fullAddress")
    private String fullAddress;

    @NotEmpty(message = "idType không được để trống ")
    @JsonProperty("idType")
    private String idType;

    @NotEmpty(message = "mac không được để trống ")
    @JsonProperty("mac")
    private String mac;

    public BindingOnboardRequest(BindingOnboardRequest bindingOnboardRequest) {
        this.refNo = bindingOnboardRequest.getRefNo();
        this.ekycDeviceId = bindingOnboardRequest.getEkycDeviceId();
        this.ekycTrustLvl = bindingOnboardRequest.getEkycTrustLvl();
        this.ekycBiometricId = bindingOnboardRequest.getEkycBiometricId();
        this.ekycSessionId = bindingOnboardRequest.getEkycSessionId();
        this.ekycType = bindingOnboardRequest.getEkycType();
        this.dateOfBirth = bindingOnboardRequest.getDateOfBirth();
        this.gender = bindingOnboardRequest.getGender();
        this.idCode = bindingOnboardRequest.getIdCode();
        this.idIssueDate = bindingOnboardRequest.getIdIssueDate();
        this.idIssuePlace = bindingOnboardRequest.getIdIssuePlace();
        this.custName = bindingOnboardRequest.getCustName();
        this.fullAddress = bindingOnboardRequest.getFullAddress();
        this.idType = bindingOnboardRequest.getIdType();
    }
}
