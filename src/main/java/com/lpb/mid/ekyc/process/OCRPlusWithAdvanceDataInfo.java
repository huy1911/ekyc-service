package com.lpb.mid.ekyc.process;

import com.lpb.mid.ekyc.config.OKHttpFactory;
import com.lpb.mid.ekyc.dto.request.InfoRequest;
import com.lpb.mid.ekyc.dto.response.OCRPlusWithAdvanceDataInfoResponse;
import com.lpb.mid.ekyc.util.TokenRsaUtils;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
@Service
public class OCRPlusWithAdvanceDataInfo {
    @Value("${ekyc.api.url.getInfo}")
    private String urlGetInfo;
    @Autowired
    private TokenRsaUtils tokenRsaUtils;

    public OCRPlusWithAdvanceDataInfoResponse getOCRPlusWithAdvanceData(InfoRequest infoRequest) {
        OCRPlusWithAdvanceDataInfoResponse t24Dto = null;
        String body = StringConvertUtils.convertToString(infoRequest);
        log.info("getOCRPlusWithAdvanceData : request get oldIdentify ---->{} refNo --->{}", body, infoRequest.getRefNo());
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(MediaType.parse("application/json; charset=utf-8"), body);
        Request.Builder builder = new Request.Builder();

        Request request = builder
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Authorization", "Bearer " + tokenRsaUtils.getPrivateKey())
                .url(urlGetInfo)
                .post(requestBody)
                .build();
        OkHttpClient client = OKHttpFactory.getInstance().getHttpClient();
        Response ocrPlusWithAdvanceDataInfoResponse;
        try {
            ocrPlusWithAdvanceDataInfoResponse = client.newCall(request).execute();
            log.info("getOCRPlusWithAdvanceData : call core success response -----> {} refNo ----->{}", ocrPlusWithAdvanceDataInfoResponse, infoRequest.getRefNo());
            String resultSee = Objects.requireNonNull(ocrPlusWithAdvanceDataInfoResponse.body()).string();
            log.info("getOCRPlusWithAdvanceData :result -----> {} by refNo --->{}", resultSee, infoRequest.getRefNo());
            t24Dto = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, OCRPlusWithAdvanceDataInfoResponse.class);
            log.info("getOCRPlusWithAdvanceData : OCRPlusWithAdvanceDataInfoResponse response success by refNo  --->{}", infoRequest.getRefNo());
            return t24Dto;
        } catch (Exception e) {
            log.error("getOCRPlusWithAdvanceData: getOCRPlusWithAdvanceData fail -----> {} by refNo ----->{}", e.getMessage(), infoRequest.getRefNo());
            return t24Dto;
        }

    }
}
