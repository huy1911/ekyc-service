package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.NfcInfoRequest;

import javax.servlet.http.HttpServletRequest;

public interface NfcInfoService {
    ResponseDto<?> compareOcrNfc(HttpServletRequest request, NfcInfoRequest nfcInfoRequest);
}
