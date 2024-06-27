package com.lpb.mid.ekyc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private customerInfo customerInfo;
    private String ekycBiometricId;
    private String hashId;
    private String mac;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class customerInfo {
        private String cardType;
        private String isVerifyRar;
        private String idTypeNo;
        private String oldIdentify;
        private String bioLevel;
    }
}
