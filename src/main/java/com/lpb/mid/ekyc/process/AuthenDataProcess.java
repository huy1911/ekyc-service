package com.lpb.mid.ekyc.process;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.data.EkycResponse;
import com.lpb.mid.ekyc.data.ValidateVerifyEKYCTransactionRequest;
import com.lpb.mid.ekyc.dto.response.AuthEkycOnBoardingResponse;
import com.lpb.mid.ekyc.util.Contants;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.Convert;
import com.lpb.mid.utils.HmacUtil;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Component
@Log4j2
public class AuthenDataProcess {
    @Value("${ekyc.api.url.verify}")
    private String validAuthenUrl;
    @Autowired
    private OkApi restApi;

    public ResponseDto<?> validateAuthenDataInfo(ValidateVerifyEKYCTransactionRequest request, String secretKey) throws IOException {
        // json request
        String jsonRequest = StringConvertUtils.convertToString(request);
        log.info("validateAuthenDataInfo : request validate authen data info --->{} by refNo --->{}", jsonRequest, request.getRefNo());
        Response responseAuthenDataInfo = restApi.restApi(jsonRequest, validAuthenUrl);
        log.info("validateAuthenDataInfo : call core success response -----> {} by refNo ----> {}", responseAuthenDataInfo, request.getRefNo());
        String resultAuthenDataInfo = Objects.requireNonNull(responseAuthenDataInfo.body()).string();
        log.info("validateAuthenDataInfo :result -----> {} by refNo ---> {}", resultAuthenDataInfo, request.getRefNo());
        // Convert JSON to object
        EkycResponse ekycResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultAuthenDataInfo, EkycResponse.class);
        log.info("validateAuthenDataInfo : response ekycResponse ---->{} by refNo --->{}", ekycResponse, request.getRefNo());
        if (ekycResponse.getResult().getResponseCode().equals("00")) {
            log.info("validateAuthenDataInfo : response 00 by refNo --->{}", request.getRefNo());
            AuthEkycOnBoardingResponse authEkycOnBoardingResponse = AuthEkycOnBoardingResponse.builder()
                    .refNo(ekycResponse.getRefNo())
                    .mac(HmacUtil.genHmac(Convert.getReq(Collections.singletonList(ekycResponse.getRefNo())), secretKey))
                    .build();
            return ResponseDto.builder()
                    .type(ErrorMessage.ERR_000.type)
                    .statusCode(ErrorMessage.ERR_000.code)
                    .description(ErrorMessage.ERR_000.message)
                    .data(authEkycOnBoardingResponse)
                    .build();
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.GW999)) {
            log.error("bindingOnboard : Ocr Plus dữ liệu nâng cao thất bại ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_41);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD20002)) {
            log.error("bindingOnboard : Người dùng chưa kích hoạt ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_43);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD21012)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD21006)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD21009)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD21010)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD21011)) {
            log.error("bindingOnboard : Invalid JWT Token ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_47);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SDFACE2D20001)) {
            log.error("bindingOnboard : Kích hoạt khuôn mặt người dùng thất bại---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_38);
        } else {
            log.error("validateAuthenDataInfo : validate authen data info fail ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_34);
        }


    }

}
