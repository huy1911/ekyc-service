package com.lpb.mid.ekyc.data;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BindingEkycTransactionRequest {

    private String refNo;
    private String requestId;
    private String extTransactionId;
    private String bioId;
    private String idTypNo;
    private String bioLevel;
    private String cif;

}
