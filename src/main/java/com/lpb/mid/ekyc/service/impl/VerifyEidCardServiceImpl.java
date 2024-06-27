package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.dto.request.VerifyDsCertRequest;
import com.lpb.mid.ekyc.dto.request.VerifyEidCardRequest;
import com.lpb.mid.ekyc.dto.response.VerifyEidCardResponse;
import com.lpb.mid.ekyc.entity.EidCardInfo;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import com.lpb.mid.ekyc.process.VerifyDsCertProcess;
import com.lpb.mid.ekyc.repository.EidCardInfoDetailRepository;
import com.lpb.mid.ekyc.repository.EidCardInfoRepository;
import com.lpb.mid.ekyc.service.VerifyEidCardService;
import com.lpb.mid.ekyc.util.KafkaUtil;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.*;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

@Service
@Log4j2
public class VerifyEidCardServiceImpl implements VerifyEidCardService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;
    @Autowired
    private EidCardInfoRepository eidCardInfoRepository;
    @Autowired
    private EidCardInfoDetailRepository eidCardInfoDetailRepository;
    @Autowired
    private VerifyDsCertProcess verifyDsCertProcess;

    @Override
    public ResponseDto<?> verifyEidCard(HttpServletRequest request, VerifyEidCardRequest verifyEidCardRequest) {
        ResponseDto<?> responseDto;
        log.info("verifyEidCard : request verifyEidCardRequest ----> {},by cmt --->{}", verifyEidCardRequest, verifyEidCardRequest.getIdNumber());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("verifyEidCard: get userName token fail by cccd --->{}", verifyEidCardRequest.getOldIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, verifyEidCardRequest.getIdNumber());
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("verifyEidCard : get user token fail ,by cccd ---->{}", verifyEidCardRequest.getOldIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = Convert.commonGetMac(request, new VerifyEidCardRequest(verifyEidCardRequest), jwtDto.getSecretKey());
        log.info("verifyEidCard : encode req Hmac ----->{},by refNo ---->{}", encodeReq, verifyEidCardRequest.getOldIdNumber());
        if (!encodeReq.equals(verifyEidCardRequest.getMac())) {
            log.warn("verifyEidCard : Mã hóa fail by refNo -->{}", verifyEidCardRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        EidCardInfo eidCardInfo = eidCardInfoRepository.findByIdNumberAndIssueDate(verifyEidCardRequest.getIdNumber(), verifyEidCardRequest.getIssueDate());
        if (ObjectUtils.isEmpty(eidCardInfo)) {
            log.warn("verifyEidCard: verifyEidCard already exist by idNumber ---->{} and date ---->{}", verifyEidCardRequest.getIdNumber(), verifyEidCardRequest.getIssueDate());
            throw new ExceptionHandler(ErrorMessage.ERR_42);
        }
        VerifyEidCardResponse verifyEidCardResponse;
        if (StringUtils.isEmpty(eidCardInfo.getIsVerify())) {
            EidCardInfoDetail eidCardInfoDetail = eidCardInfoDetailRepository.findFirstByIdNumberAndIssueDateOrderByCreateByDesc(verifyEidCardRequest.getIdNumber(), verifyEidCardRequest.getIssueDate());
            if (ObjectUtils.isEmpty(eidCardInfoDetail)) {
                log.warn("verifyEidCard: eidCardInfoDetail already exist by idNumber ---->{} and date ---->{}", verifyEidCardRequest.getIdNumber(), verifyEidCardRequest.getIssueDate());
                throw new ExceptionHandler(ErrorMessage.ERR_42);
            }
            VerifyDsCertRequest verifyDsCertRequest = VerifyDsCertRequest.builder()
                    .deviceType(Constants.MOBILE)
                    .idCard(eidCardInfoDetail.getIdNumber())
                    .dsCert(eidCardInfoDetail.getDocumentSigningCertificate())
                    .code(Constants.LPBANK_UAT)
                    .province(eidCardInfoDetail.getPlaceOfResidence())
                    .build();
            String isVerify = verifyDsCertProcess.validateDsCert(verifyDsCertRequest, verifyEidCardRequest.getIdNumber());
            if (isVerify.equals(Constants.isVeriFyDsFail)) {
                log.info("verifyEidCard : is verify rar fail by user  ----> {},cccd ---->{}", jwtDto.getCustomerNo(), verifyEidCardRequest.getIdNumber());
                throw new ExceptionHandler(ErrorMessage.ERR_40);
            }
            eidCardInfo.setIsVerify(isVerify);
            eidCardInfoRepository.save(eidCardInfo);
            log.info("verifyEidCard : save isVerify success cccd ---> {}", verifyEidCardRequest.getIdNumber());
            verifyEidCardResponse = VerifyEidCardResponse.builder()
                    .isVerify(isVerify)
                    .mac(HmacUtil.genHmac(Convert.getReq(Collections.singletonList(isVerify)), jwtDto.getSecretKey())).build();
            responseDto = ResponseDto.builder()
                    .type(ErrorMessage.ERR_000.type)
                    .statusCode(ErrorMessage.ERR_000.code)
                    .description(ErrorMessage.ERR_000.message)
                    .data(verifyEidCardResponse)
                    .build();
        } else {
            verifyEidCardResponse = VerifyEidCardResponse.builder()
                    .isVerify(eidCardInfo.getIsVerify())
                    .mac(HmacUtil.genHmac(Convert.getReq(Collections.singletonList(eidCardInfo.getIsVerify())), jwtDto.getSecretKey())).build();
            responseDto = ResponseDto.builder()
                    .type(ErrorMessage.ERR_000.type)
                    .statusCode(ErrorMessage.ERR_000.code)
                    .description(ErrorMessage.ERR_000.message)
                    .data(verifyEidCardResponse)
                    .build();
        }
        return responseDto;
    }
}
