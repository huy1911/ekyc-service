package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.data.BindingWithOnboardingRequest;
import com.lpb.mid.ekyc.entity.CustomerInfoMapping;
import com.lpb.mid.ekyc.dto.request.BindingOnboardRequest;
import com.lpb.mid.ekyc.process.BindingOnboardProcess;
import com.lpb.mid.ekyc.repository.CustomerInfoMappingRepository;
import com.lpb.mid.ekyc.service.BindingOnboardingService;
import com.lpb.mid.ekyc.util.CifUtils;
import com.lpb.mid.ekyc.util.KafkaUtil;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Log4j2
public class BindingOnboardingServiceImpl implements BindingOnboardingService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;
    @Autowired
    private BindingOnboardProcess bindingOnboardProcess;
    @Autowired
    private CustomerInfoMappingRepository customerInfoMappingRepository;

    @Override
    public ResponseDto<?> bindingOnboard(HttpServletRequest request, BindingOnboardRequest bindingOnboardRequest) throws IOException {
        log.info("bindingOnboard : request binding onboard -----> {},by refNo --->{}", bindingOnboardRequest,bindingOnboardRequest.getRefNo());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("bindingOnboard: get userName token fail --->{}", bindingOnboardRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, bindingOnboardRequest.getRefNo());
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("bindingOnboard: get userName token fail by refNo --->{}",bindingOnboardRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = Convert.commonGetMac(request, new BindingOnboardRequest(bindingOnboardRequest), jwtDto.getSecretKey());
        log.info("bindingOnboard : encode req Hmac ----->{},by refNo --->{}", encodeReq,bindingOnboardRequest.getRefNo());
        if (!encodeReq.equals(bindingOnboardRequest.getMac())) {
            log.warn("bindingOnboard : Mã hóa fail by refNo --->{}", bindingOnboardRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        BindingWithOnboardingRequest bindingWithOnboardingRequest = BindingWithOnboardingRequest.builder()
                .refNo(bindingOnboardRequest.getRefNo())
                .requestId(UUID.randomUUID().toString())
                .ekycDeviceId(bindingOnboardRequest.getEkycDeviceId())
                .ekycTrustLvl(bindingOnboardRequest.getEkycTrustLvl())
                .ekycBiometricId(bindingOnboardRequest.getEkycBiometricId())
                .ekycSessionId(bindingOnboardRequest.getEkycSessionId())
                .cif(CifUtils.genUUID(jwtDto.getCustomerNo()))
                .ekycType(bindingOnboardRequest.getEkycType())
                .customerInfo(BindingWithOnboardingRequest.CustomerInfo.builder()
                        .dateOfBirth(bindingOnboardRequest.getDateOfBirth())
                        .gender(bindingOnboardRequest.getGender())
                        .idCode(bindingOnboardRequest.getIdCode())
                        .idIssueDate(bindingOnboardRequest.getIdIssueDate())
                        .idIssuePlace(bindingOnboardRequest.getIdIssuePlace())
                        .custName(bindingOnboardRequest.getCustName())
                        .fullAddress(bindingOnboardRequest.getFullAddress())
                        .idType(bindingOnboardRequest.getIdType())
                        .build())
                .build();
        List<CustomerInfoMapping> customerInfoMapping = customerInfoMappingRepository.findByCustomerNoOrUdf4(jwtDto.getCustomerNo(), bindingOnboardRequest.getIdCode());
        if (customerInfoMapping.size() > 0) {
            log.warn("bindingOnboard: Tai khoan da onboard by refNo --->{}",bindingOnboardRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_38);
        }
        log.info("bindingOnboard : build binding with onboarding request ----> {},by refNo --->{}", bindingOnboardRequest,bindingOnboardRequest.getRefNo());
        return bindingOnboardProcess.bindingOnboard(bindingWithOnboardingRequest, jwtDto.getSecretKey(), request, jwtDto);

    }


}
