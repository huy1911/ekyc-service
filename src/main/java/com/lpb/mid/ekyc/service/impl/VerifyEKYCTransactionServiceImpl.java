package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.data.ValidateVerifyEKYCTransactionRequest;
import com.lpb.mid.ekyc.dto.response.VerifyEKYCTransactionRequest;
import com.lpb.mid.ekyc.process.AuthenDataProcess;
import com.lpb.mid.ekyc.service.VerifyEKYCTransactionService;
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

@Service
@Log4j2
public class VerifyEKYCTransactionServiceImpl implements VerifyEKYCTransactionService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    @Autowired
    private AuthenDataProcess authenDataProcess;

    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;

    @Override
    public ResponseDto<?> verifyEKYCTransaction(HttpServletRequest request, VerifyEKYCTransactionRequest verifyEKYCTransactionRequest) throws IOException {
        log.info("verifyEKYCTransaction : request verifyEKYCTransaction --->{},by refNo ---->{}",verifyEKYCTransactionRequest,verifyEKYCTransactionRequest.getRefNo());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("verifyEKYCTransaction: get userName token fail by refNo --->{}", verifyEKYCTransactionRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, verifyEKYCTransactionRequest.getRefNo());

        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("verifyEKYCTransaction: get userName token fail by refNo ---> {}",verifyEKYCTransactionRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = Convert.commonGetMac(request, new VerifyEKYCTransactionRequest(verifyEKYCTransactionRequest), jwtDto.getSecretKey());
        log.info("verifyEKYCTransaction : encode req Hmac ----->{} by refNo ---->{}", encodeReq,verifyEKYCTransactionRequest.getRefNo());
        if (!encodeReq.equals(verifyEKYCTransactionRequest.getMac())) {
            log.warn("verifyEKYCTransaction : Mã hóa fail by refNo --->{}",verifyEKYCTransactionRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        ValidateVerifyEKYCTransactionRequest validateVerifyEKYCTransactionRequest = ValidateVerifyEKYCTransactionRequest.builder()
                .refNo(verifyEKYCTransactionRequest.getRefNo())
                .requestId(verifyEKYCTransactionRequest.getRequestId())
                .extTransactionId(verifyEKYCTransactionRequest.getExtTransactionId())
                .bioId(verifyEKYCTransactionRequest.getBioId())
                .build();
        return authenDataProcess.validateAuthenDataInfo(validateVerifyEKYCTransactionRequest, jwtDto.getSecretKey());


    }
//
//    public String checkRequest(HttpServletRequest request, VerifyEKYCTransactionRequest verifyEKYCTransactionRequest, String secretKey) {
//        List<String> strings = new ArrayList<>();
//        strings.add(request.getHeader(Constants.X_SOURCE_ENV));
//        strings.add(request.getHeader(Constants.X_REFERENCE_ID));
//        strings.add(verifyEKYCTransactionRequest.getRefNo());
//        strings.add(verifyEKYCTransactionRequest.getRequestId());
//        strings.add(verifyEKYCTransactionRequest.getExtTransactionId());
//        strings.add(verifyEKYCTransactionRequest.getBioId());
//        log.info("verifyEKYCTransaction list string ----->{} by refNo ---->{}", Convert.getReq(strings), verifyEKYCTransactionRequest.getRefNo());
//        return HmacUtil.genHmac(Convert.getReq(strings), secretKey);
//    }
}
