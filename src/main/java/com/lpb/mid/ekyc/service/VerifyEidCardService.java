package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.VerifyEidCardRequest;

import javax.servlet.http.HttpServletRequest;

public interface VerifyEidCardService {
    ResponseDto<?> verifyEidCard(HttpServletRequest request, VerifyEidCardRequest verifyEidCardRequest);
}
