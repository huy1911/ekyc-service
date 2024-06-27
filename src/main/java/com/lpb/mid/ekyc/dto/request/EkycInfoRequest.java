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
public class EkycInfoRequest {
    @NotEmpty(message = "refNo không được để trống ")
    @JsonProperty("refNo")
    private String refNo;

    @NotEmpty(message = "ekycDeviceId không được để trống ")
    @JsonProperty("ekycDeviceId")
    private String ekycDeviceId;

    @NotEmpty(message = "ekycTrustLvl không được để trống ")
    @JsonProperty("ekycTrustLvl")
    private String ekycTrustLvl;

    @NotEmpty(message = "ekycSessionId không được để trống ")
    @JsonProperty("ekycSessionId")
    private String ekycSessionId;

    @NotEmpty(message = "ekycBiometricId không được để trống ")
    @JsonProperty("ekycBiometricId")
    private String ekycBiometricId;

    @NotEmpty(message = "idTypNo không được để trống ")
    @JsonProperty("idTypNo")
    private String idTypNo;

    @NotEmpty(message = "ekycType không được để trống ")
    @JsonProperty("ekycType")
    private String ekycType;

    @NotEmpty(message = "mac không được để trống ")
    @JsonProperty("mac")
    private String mac;


    public EkycInfoRequest(EkycInfoRequest ekycInfoRequest) {
        this.refNo = ekycInfoRequest.getRefNo();
        this.ekycDeviceId = ekycInfoRequest.getEkycDeviceId();
        this.ekycTrustLvl = ekycInfoRequest.getEkycTrustLvl();
        this.ekycSessionId = ekycInfoRequest.getEkycSessionId();
        this.ekycBiometricId = ekycInfoRequest.getEkycBiometricId();
        this.idTypNo = ekycInfoRequest.getIdTypNo();
        this.ekycType = ekycInfoRequest.getEkycType();
    }
}
