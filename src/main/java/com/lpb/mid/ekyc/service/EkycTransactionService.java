package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.EkycTransactionRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface EkycTransactionService {

    ResponseDto<?> bindEkycTransaction(HttpServletRequest request, EkycTransactionRequest ekycTransactionRequest) throws IOException;
}
