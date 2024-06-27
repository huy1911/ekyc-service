package com.lpb.mid.ekyc.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VerifyDsCertRequest {
    @JsonProperty("ds_cert")
    private String dsCert;
    @JsonProperty("id_card")
    private String idCard;
    @JsonProperty("device_type")
    private String deviceType;
    @JsonProperty("code")
    private String code;
    @JsonProperty("province")
    private String province;
}
