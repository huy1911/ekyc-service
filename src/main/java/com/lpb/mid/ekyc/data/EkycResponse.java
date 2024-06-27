package com.lpb.mid.ekyc.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EkycResponse {
    @JsonProperty("refNo")
    private String refNo;
    @JsonProperty("result")
    private EkycResult result;
}
