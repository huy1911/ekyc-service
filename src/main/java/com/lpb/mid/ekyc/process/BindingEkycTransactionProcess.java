package com.lpb.mid.ekyc.process;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.data.BindingEkycTransactionRequest;
import com.lpb.mid.ekyc.data.EkycResponse;
import com.lpb.mid.ekyc.dto.response.AuthEkycOnBoardingResponse;
import com.lpb.mid.ekyc.util.Contants;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.Convert;
import com.lpb.mid.utils.HmacUtil;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class BindingEkycTransactionProcess {

    @Value("${ekyc.api.url.binding}")
    private String bindingEkycTransactionUrl;
    @Autowired
    private OkApi restApi;

    public ResponseDto<?> bindingEkycTransaction(BindingEkycTransactionRequest request, String secretKey) throws IOException {
        // json request
        String jsonRequest = StringConvertUtils.convertToString(request);
        log.info("bindingEkycTransaction: request-----> {},by refNo ---> {}", jsonRequest, request.getRefNo());
        Response responseEkycTransaction = restApi.restApi(jsonRequest, bindingEkycTransactionUrl);
        log.info("bindingEkycTransaction : call core success response -----> {} by refNo ---> {}", responseEkycTransaction, request.getRefNo());
        String resultSee = Objects.requireNonNull(responseEkycTransaction.body()).string();
        log.info("bindingEkycTransaction :result -----> {} by refNo ---> {}", resultSee, request.getRefNo());
        // Convert JSON to object
        EkycResponse ekycResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, EkycResponse.class);
        log.info("bindingEkycTransaction : response ekycResponse ---->{} by refNo ---> {}", ekycResponse, request.getRefNo());
        if (ekycResponse.getResult().getResponseCode().equals("00")) {
            log.info("bindingEkycTransaction : response 00  by refNo ---> {}", request.getRefNo());
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
            log.error("bindingOnboard : ExtTransactionId length invalid!---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_41);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD20002)) {
            log.error("bindingOnboard : Người dung không tồn tại hoặc chưa được kích hoạt!---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_42);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD22003)) {
            log.error("bindingOnboard : Cập nhật dữ liệu OCR từ T24 thất bại---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_44);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SDFACE2D20001)) {
            log.error("bindingOnboard : Kích hoạt khuôn mặt người dùng thất bại---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_45);
        } else {
            log.error("bindingEkycTransaction : binding Ekyc Transaction fail ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_34);
        }


    }

}
