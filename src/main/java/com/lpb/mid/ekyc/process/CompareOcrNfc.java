package com.lpb.mid.ekyc.process;


import com.lpb.mid.ekyc.data.EkycResponse;
import com.lpb.mid.ekyc.data.ValidateNFCInfoRequest;
import com.lpb.mid.ekyc.dto.response.ValidateNfcInfoResponse;
import com.lpb.mid.ekyc.dto.response.CompareOcrNfcResponse;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.utils.Convert;
import com.lpb.mid.utils.HmacUtil;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Log4j2
public class CompareOcrNfc {

    @Value("${ekyc.api.url.valid-nfc}")
    private String validNfcUrl;
    @Autowired
    private OkApi restApi;

    public CompareOcrNfcResponse validateNfcInfo( ValidateNFCInfoRequest request, String secretKey) {
        CompareOcrNfcResponse compareOcrNfcResponse = new CompareOcrNfcResponse();
        try {
            String jsonRequest = StringConvertUtils.convertToString(request);
            log.info("validateNfcInfo : request validateNfcInfo  -------> {} by refNo ----->{} ", jsonRequest,request.getRefNo());
            Response responseValidateNfcInfo = restApi.restApi(jsonRequest, validNfcUrl);
            log.info("validateNfcInfo : call core success response validateNfcInfo -----> {} by refNo --->{}",responseValidateNfcInfo,request.getRefNo());
            String resultSee = Objects.requireNonNull(responseValidateNfcInfo.body()).string();
            // Convert JSON to object
            EkycResponse ekycResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, EkycResponse.class);
            log.info("validateNfcInfo : response ekycResponse validateNfcInfo ---->{},by refNo---->{}", ekycResponse,request.getRefNo());
            // Check response object
            if (ekycResponse.getResult().getResponseCode().equals("00")) {
                ValidateNfcInfoResponse validateNfcInfoResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, ValidateNfcInfoResponse.class);
                //map res CompareOcrNfcResponse


                CompareOcrNfcResponse.citizenInfo citizenInfo = new CompareOcrNfcResponse.citizenInfo();
                citizenInfo.setBirthDate(validateNfcInfoResponse.getData().getCitizenInfo().getBirthDate());
                citizenInfo.setCitizenPid(validateNfcInfoResponse.getData().getCitizenInfo().getCitizenPid());
                citizenInfo.setDateProvide(validateNfcInfoResponse.getData().getCitizenInfo().getDateProvide());
                citizenInfo.setFullName(validateNfcInfoResponse.getData().getCitizenInfo().getFullName());
                citizenInfo.setOutOfDate(validateNfcInfoResponse.getData().getCitizenInfo().getOutOfDate());
                citizenInfo.setImage(validateNfcInfoResponse.getData().getImage());
                citizenInfo.setDg1(validateNfcInfoResponse.getData().getDg1());
                citizenInfo.setDg2(validateNfcInfoResponse.getData().getDg2());
                citizenInfo.setDg13(validateNfcInfoResponse.getData().getDg13());
                citizenInfo.setSod(validateNfcInfoResponse.getData().getSod());

                CompareOcrNfcResponse.nfcCheckValidResult nfcCheckValidResult = new CompareOcrNfcResponse.nfcCheckValidResult();
                nfcCheckValidResult.setFm(validateNfcInfoResponse.getData().getNfcCheckValidResult().getFm());
                nfcCheckValidResult.setFc(validateNfcInfoResponse.getData().getNfcCheckValidResult().getFc());
                nfcCheckValidResult.setCitizenInfo(citizenInfo);

                compareOcrNfcResponse.setNfcCheckValidResult(nfcCheckValidResult);
                compareOcrNfcResponse.setRefNo(validateNfcInfoResponse.getRefNo());
                compareOcrNfcResponse.setMac(genMacRes(nfcCheckValidResult, secretKey));
//                saveEidCardInfoDetail(compareOcrNfcResponse, jwtDto);
//                saveEidCardInfo(compareOcrNfcResponse, jwtDto, request);
                log.info("validateNfcInfo : response ekycResponse validateNfcInfo success ---->{},by refNo ---->{}", compareOcrNfcResponse,request.getRefNo());
                return compareOcrNfcResponse;
            } else {
                log.error("validateNfcInfo : call nfcInfo fail -----> {} by refNo ----->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
                return compareOcrNfcResponse;
            }
        } catch (Exception ex) {
                log.error("validateNfcInfo : call nfcInfo error --->{} by refNo ---->{}", ex.getMessage(),request.getRefNo());
            return compareOcrNfcResponse;
        }
    }

    private String genMacRes(CompareOcrNfcResponse.nfcCheckValidResult nfcCheckValidResult,String secretKey) {
        List<String> strings = new ArrayList<>();
        strings.add(nfcCheckValidResult.getFc());
        strings.add(nfcCheckValidResult.getFm());
        strings.add(nfcCheckValidResult.getCitizenInfo().getBirthDate());
        strings.add(nfcCheckValidResult.getCitizenInfo().getCitizenPid());
        strings.add(nfcCheckValidResult.getCitizenInfo().getDateProvide());
        strings.add(nfcCheckValidResult.getCitizenInfo().getFullName());
        strings.add(nfcCheckValidResult.getCitizenInfo().getOutOfDate());
        strings.add(nfcCheckValidResult.getCitizenInfo().getImage());
        strings.add(nfcCheckValidResult.getCitizenInfo().getDg1());
        strings.add(nfcCheckValidResult.getCitizenInfo().getDg2());
        strings.add(nfcCheckValidResult.getCitizenInfo().getDg13());
        strings.add(nfcCheckValidResult.getCitizenInfo().getSod());
        String encodeRes = HmacUtil.genHmac(Convert.getReq(strings), secretKey);
        log.info("validateNfcInfo : encode res Hmac -->{}", encodeRes);
        return encodeRes;
    }

}
