package com.lpb.mid.ekyc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NfcInfoRequest {
    @NotEmpty(message = "birthDate không được để trống ")
    @JsonProperty("birthDate")
    private String birthDate;

    @NotEmpty(message = "ekycBiometricId không được để trống ")
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;

    @NotEmpty(message = "ekycDeviceId không được để trống ")
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;

    @NotEmpty(message = "ekycSessionId không được để trống ")
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;

    @NotEmpty(message = "ekycType không được để trống ")
    @JsonProperty("ekycType")
    private String ekycType;

    @NotEmpty(message = "fullName không được để trống ")
    @JsonProperty("fullName")
    private String fullName;

    @NotEmpty(message = "idExpireDate không được để trống ")
    @JsonProperty("idExpireDate")
    private String idExpireDate;

    @NotEmpty(message = "idIssueDate không được để trống ")
    @JsonProperty("idIssueDate")
    private String idIssueDate;

    @NotEmpty(message = "idNumber không được để trống ")
    @JsonProperty("idNumber")
    private String idNumber;

    @NotEmpty(message = "nfcSession không được để trống ")
    @JsonProperty("nfcSession")
    private String nfcSession;

    @NotEmpty(message = "nfcType không được để trống ")
    @JsonProperty("nfcType")
    private String nfcType;

    @NotEmpty(message = "refNo không được để trống ")
    @JsonProperty("refNo")
    private String refNo;

    @NotEmpty(message = "mac không được để trống ")
    @JsonProperty("mac")
    private String mac;

    public NfcInfoRequest(NfcInfoRequest nfcInfoRequest) {
        this.birthDate = nfcInfoRequest.getBirthDate();
        this.ekycBiometricId = nfcInfoRequest.getEkycBiometricId();
        this.ekycDeviceId = nfcInfoRequest.getEkycDeviceId();
        this.ekycSessionId = nfcInfoRequest.getEkycSessionId();
        this.ekycType = nfcInfoRequest.getEkycType();
        this.fullName = nfcInfoRequest.getFullName();
        this.idExpireDate = nfcInfoRequest.getIdExpireDate();
        this.idIssueDate = nfcInfoRequest.getIdIssueDate();
        this.idNumber = nfcInfoRequest.getIdNumber();
        this.nfcSession = nfcInfoRequest.getNfcSession();
        this.nfcType = nfcInfoRequest.getNfcType();
        this.refNo = nfcInfoRequest.getRefNo();
    }
}
