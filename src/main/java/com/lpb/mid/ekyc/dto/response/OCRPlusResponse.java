package com.lpb.mid.ekyc.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OCRPlusResponse {
    private OCRPlusWithAdvanceDataInfoResponse ocrPlusInfoResponse;
    private CompareOcrNfcResponse compareResponse;
}
