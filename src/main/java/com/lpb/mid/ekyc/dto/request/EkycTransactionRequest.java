package com.lpb.mid.ekyc.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @author quantt2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EkycTransactionRequest {
    @NotEmpty(message = "refNo không được để trống ")
    private String refNo;

    @NotEmpty(message = "extTransactionId không được để trống ")
    private String extTransactionId;

    @NotEmpty(message = "bioId không được để trống ")
    private String bioId;

    @NotEmpty(message = "idTypNo không được để trống ")
    private String idTypNo;

    @NotEmpty(message = "bioLevel không được để trống ")
    private String bioLevel;

    @NotEmpty(message = "mac không được để trống ")
    private String mac;

    public EkycTransactionRequest(EkycTransactionRequest request) {
        this.refNo = request.getRefNo();
        this.extTransactionId = request.getExtTransactionId();
        this.bioId = request.getBioId();
        this.idTypNo = request.getIdTypNo();
        this.bioLevel = request.getBioLevel();
    }
}
