package com.lpb.mid.ekyc.process;


import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.data.EkycResponse;
import com.lpb.mid.ekyc.data.ValidateEkycRequest;
import com.lpb.mid.ekyc.dto.response.AuthEkycOnBoardingResponse;
import com.lpb.mid.ekyc.util.Contants;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.Convert;
import com.lpb.mid.utils.HmacUtil;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Objects;

@Component
@Log4j2
public class EkycProcess {
    @Value("${ekyc.api.url.valid-ekyc}")
    private String validEkycUrl;
    @Autowired
    private OkApi restApi;

    public ResponseDto<?> validateEkyc(ValidateEkycRequest request, String secretKey) {
        try {
            // json request
            String jsonRequest = StringConvertUtils.convertToString(request);
            log.info("validateEkyc : request validateEkyc --->{} by refNo ----> {}", jsonRequest,request.getRefNo());
            Response responseValidateEkyc = restApi.restApi(jsonRequest, validEkycUrl);
            log.info("validateEkyc : call core success response -----> {} by refNo {}", responseValidateEkyc,request.getRefNo());
            String resultSee = Objects.requireNonNull(responseValidateEkyc.body()).string();
            log.info("validateEkyc :result -----> {} by refNo --->{} ", resultSee,request.getRefNo());
            EkycResponse ekycResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, EkycResponse.class);
            log.info("validateEkyc : response ekycResponse ---->{} by refNo --->{}", ekycResponse,request.getRefNo());
            if (ekycResponse.getResult().getResponseCode().equals("00")) {
                AuthEkycOnBoardingResponse authEkycOnBoardingResponse = AuthEkycOnBoardingResponse.builder()
                        .refNo(ekycResponse.getRefNo())
                        .mac(HmacUtil.genHmac(Convert.getReq(Collections.singletonList(ekycResponse.getRefNo())), secretKey))
                        .build();
                log.info("validateEkyc : response success 00  by refNo --->{}",request.getRefNo());
                return ResponseDto.builder()
                        .type(ErrorMessage.ERR_000.type)
                        .statusCode(ErrorMessage.ERR_000.code)
                        .description(ErrorMessage.ERR_000.message)
                        .data(authEkycOnBoardingResponse)
                        .build();
            }
            if (ekycResponse.getResult().getResponseCode().equals(Contants.SD21014) || ekycResponse.getResult().getResponseCode().equals(Contants.SD21021)) {
                log.error("validateEkyc : Không tìn thấy thông tin người dùng ---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
                throw new ExceptionHandler(ErrorMessage.ERR_42);
            } else {
                log.error("validateEkyc : validateEkyc fail ---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
                throw new ExceptionHandler(ErrorMessage.ERR_34);
            }

        } catch (Exception ex) {
            log.error("validateEkyc : validateEkyc error --->{} by refNo --->{}", ex.getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_99);
        }
    }


}
