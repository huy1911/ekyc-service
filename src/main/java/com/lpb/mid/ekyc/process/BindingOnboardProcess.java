package com.lpb.mid.ekyc.process;

import com.fasterxml.uuid.Generators;
import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.data.BindingWithOnboardingRequest;
import com.lpb.mid.ekyc.data.EkycResponse;
import com.lpb.mid.ekyc.dto.response.BindingOnboardResponse;
import com.lpb.mid.ekyc.entity.CustomerInfoMapping;
import com.lpb.mid.ekyc.repository.CustomerInfoMappingRepository;
import com.lpb.mid.ekyc.util.Contants;
import com.lpb.mid.ekyc.util.OkApi;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.Constants;
import com.lpb.mid.utils.Convert;
import com.lpb.mid.utils.HmacUtil;
import com.lpb.mid.utils.StringConvertUtils;
import lombok.extern.log4j.Log4j2;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Objects;

@Component
@Log4j2
public class BindingOnboardProcess {
    @Value("${ekyc.api.url.onboarding}")
    private String bindingOnboardUrl;
    @Autowired
    private OkApi restApi;
    @Autowired
    private CustomerInfoMappingRepository customerInfoMappingRepository;

    public ResponseDto<?> bindingOnboard(BindingWithOnboardingRequest request, String secretKey, HttpServletRequest httpServletRequest, JWTDto jwtDto) throws IOException {
        // json request
        String jsonRequest = StringConvertUtils.convertToString(request);
        log.info("bindingOnboard: request ---->{} by refNo --->{} ", jsonRequest,request.getRefNo());
        Response responseBindingOnboard = restApi.restApi(jsonRequest, bindingOnboardUrl);
        log.info("bindingOnboard : call core success response -----> {} by customerNo --->{} by refNo-->{}", responseBindingOnboard, jwtDto.getCustomerNo(),request.getRefNo());
        String resultSee = Objects.requireNonNull(responseBindingOnboard.body()).string();
        log.info("bindingOnboard :result -----> {} by refNo --->{}", resultSee, request.getRefNo());
        EkycResponse ekycResponse = StringConvertUtils.readValueWithInsensitiveProperties(resultSee, EkycResponse.class);
        log.info("bindingOnboard : response ekycResponse ---->{} by refNo --->{}", ekycResponse,request.getRefNo());
        if (ekycResponse.getResult().getResponseCode().equals("00")) {
            log.info("bindingOnboard : response success 00  by refNo --->{}",request.getRefNo());
            String hashIdSha1 = encryptHashId(request.getCif());
            if (hashIdSha1.length() < 40) {
                hashIdSha1 = 0 + hashIdSha1;
            }
            List<String> strings = new ArrayList<>();
            strings.add(ekycResponse.getRefNo());
            strings.add(request.getCif());
            BindingOnboardResponse bindingOnboardResponse = BindingOnboardResponse.builder()
                    .refNo(ekycResponse.getRefNo())
                    .hashId(hashIdSha1)
                    .mac(HmacUtil.genHmac(Convert.getReq(strings), secretKey))
                    .build();
            saveDataDindingOnboard(jwtDto, httpServletRequest, request, hashIdSha1);

            return ResponseDto.builder()
                    .type(ErrorMessage.ERR_000.type)
                    .statusCode(ErrorMessage.ERR_000.code)
                    .description(ErrorMessage.ERR_000.message)
                    .data(bindingOnboardResponse)
                    .build();
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.GW888)) {
            log.error("bindingOnboard : Thẻ hoặc khuôn mặt không hợp lệ ---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_32);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD20002)) {
            log.error("bindingOnboard : Người dùng chưa kích hoạt ---->{} by refNo ---->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_43);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD22003)) {
            log.error("bindingOnboard : Cập nhật dữ liệu ocr thất bại ---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_44);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SD23301)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD23302)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD23304)
                || ekycResponse.getResult().getResponseCode().equals(Contants.SD23305)) {
            log.error("bindingOnboard : Invalid JWT Token ---->{}", ekycResponse.getResult().getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_46);
        }
        if (ekycResponse.getResult().getResponseCode().equals(Contants.SDFACE2D20001)) {
            log.error("bindingOnboard : Kích hoạt khuôn mặt người dùng thất bại---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_45);
        } else {
            log.error("bindingOnboard : validateEkyc fail ---->{} by refNo --->{}", ekycResponse.getResult().getMessage(),request.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_34);
        }
    }

    public void saveDataDindingOnboard(JWTDto jwtDto, HttpServletRequest httpServletRequest
            , BindingWithOnboardingRequest request, String hashIdSha1) {
        CustomerInfoMapping customerInfoMapping = CustomerInfoMapping.builder()
                .guid(Generators.timeBasedEpochGenerator().generate().toString())
                .customerNo(jwtDto.getCustomerNo())
                .channel(httpServletRequest.getHeader(Constants.X_SOURCE_ENV))
                .hashId(hashIdSha1)
                .createDate(LocalDate.now())
                .udf1(request.getEkycType())
                .udf2(request.getCustomerInfo().getIdType())
                .udf3(request.getEkycBiometricId())
                .udf4(request.getCustomerInfo().getIdCode())
                .udf5(request.getEkycTrustLvl())
                .build();
        customerInfoMappingRepository.save(customerInfoMapping);
        log.info("bindingOnboard : save binding onboard success by cif ---->{}", request.getCif());

    }


    private String encryptHashId(String hashId) {
        String sha1 = "";
        try {
            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
            crypt.reset();
            crypt.update(hashId.getBytes(StandardCharsets.UTF_8));
            sha1 = byteToHex(crypt.digest());
        } catch (Exception e) {
            log.warn("encryptHashId : sha1 faill ---->{}", e.getMessage());
            e.printStackTrace();
        }

        return sha1;
    }

    private String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }
}
