package com.lpb.mid.ekyc.process;


import com.lpb.mid.ekyc.dto.request.VerifyDsCertRequest;
import com.lpb.mid.ekyc.dto.response.VerifyDsCertResponse;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.utils.Constants;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.Objects;

@Log4j2
@Service
public class VerifyDsCertProcess {
    @Value("${ekyc.api.url.verify_ds_cert}")
    private String urlVerifyDsCert;
    @Autowired
    private OkApi restApi;
    public String validateDsCert(VerifyDsCertRequest request,String refNo) {
        String isVerify = Constants.isVeriFyDsFail;
        try {
            // json request
            String jsonRequest = StringConvertUtils.convertToString(request);
            log.info("validateDsCert : request validate ds cert --->{} by refNo ---->{}", jsonRequest,refNo);
            Response responseValidateDsCert = restApi.restApi(jsonRequest, urlVerifyDsCert);
            log.info("validateDsCert : validate ds cert success response -----> {} by refNo ---->{}", responseValidateDsCert,refNo);
            String resultSee = Objects.requireNonNull(responseValidateDsCert.body()).string();
            log.info("validateDsCert :result -----> {} by refNo ---->{} ", resultSee,refNo);
            VerifyDsCertResponse verifyDsCertResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, VerifyDsCertResponse.class);
            log.info("validateDsCert :verifyDsCertResponse -----> {} by refNo ---->{} ", resultSee,refNo);
            if(ObjectUtils.isEmpty(verifyDsCertResponse.getData()) || !verifyDsCertResponse.getData().getIsValidIdCard()){
                log.error("validateDsCert : validate ds cert fail by refNo ---->{}",refNo);
                return isVerify;
            }
            isVerify = Constants.isVeriFyDsSuccess;
            return isVerify;
        }catch (Exception exception){
            log.error("validateDsCert : validate ds cert error -----> {} by refNo ---->{}", exception.getMessage(),refNo);
            return isVerify;
        }
    }
}
