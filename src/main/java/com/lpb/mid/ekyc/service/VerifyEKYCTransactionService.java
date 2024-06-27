package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.response.VerifyEKYCTransactionRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface VerifyEKYCTransactionService {


    ResponseDto<?> verifyEKYCTransaction(HttpServletRequest request, VerifyEKYCTransactionRequest verifyEKYCTransactionRequest) throws IOException;
}
