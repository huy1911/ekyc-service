package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.EkycInfoRequest;

import javax.servlet.http.HttpServletRequest;

public interface EkycInfoService {
    ResponseDto<?> authEkycOnBoarding(HttpServletRequest request, EkycInfoRequest ekycInfoRequest);

    ResponseDto<?> getInfo(HttpServletRequest request, String idNumber);
}
