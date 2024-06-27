package com.lpb.mid.ekyc.dto.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InfoDto {
    private String isVerifyRar;
    //    private String hashId;
    private String oldIdentify;
    //    private String bioLevel;
//    private String cardType;
    private String refNo;
    private String ekycBiometricId;

    private String ekycDeviceId;
    private String ekycSessionId;
    private String ekycType;
    private String nfcSession;
    private String nfcType;
    private String idTypeNo;
}
