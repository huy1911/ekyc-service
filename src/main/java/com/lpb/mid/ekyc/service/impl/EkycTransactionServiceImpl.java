package com.lpb.mid.ekyc.service.impl;


import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.data.BindingEkycTransactionRequest;
import com.lpb.mid.ekyc.dto.request.EkycTransactionRequest;
import com.lpb.mid.ekyc.process.BindingEkycTransactionProcess;
import com.lpb.mid.ekyc.service.EkycTransactionService;
import com.lpb.mid.ekyc.util.CifUtils;
import com.lpb.mid.ekyc.util.KafkaUtil;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
@RequiredArgsConstructor
@Slf4j
public class EkycTransactionServiceImpl implements EkycTransactionService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    private final BindingEkycTransactionProcess bindingProcess;
    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;

    @Override
    public ResponseDto<?> bindEkycTransaction(HttpServletRequest request, EkycTransactionRequest requestFromClient) throws IOException {
        log.info("bindEkycTransaction : request bindEkycTransaction --->{},by refNo --->{}",requestFromClient,requestFromClient.getRefNo());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("bindEkycTransaction: get userName token fail --->{}", requestFromClient.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, requestFromClient.getRefNo());

        if (StringUtils.isEmpty(jwtDto.getCustomerNo())) {
            log.warn("bindEkycTransaction: get userName token fail by refNo --->{}",requestFromClient.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = Convert.commonGetMac(request, new EkycTransactionRequest(requestFromClient) , jwtDto.getSecretKey());
        log.info("bindEkycTransaction : encode req Hmac ----->{} by refNo ---->{}", encodeReq,requestFromClient.getRefNo());
        if (!encodeReq.equals(requestFromClient.getMac())) {
            log.warn("bindEkycTransaction : Mã hóa failby refNo --->{}",requestFromClient.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        BindingEkycTransactionRequest bindingRequest = BindingEkycTransactionRequest.builder()
                .refNo(requestFromClient.getRefNo())
                .requestId(request.getHeader(Constants.X_REFERENCE_ID))
                .extTransactionId(requestFromClient.getExtTransactionId())
                .bioId(requestFromClient.getBioId())
                .idTypNo(requestFromClient.getIdTypNo())
                .bioLevel(requestFromClient.getBioLevel())
                .cif(CifUtils.genUUID(jwtDto.getCustomerNo()))
                .build();
        log.info("bindEkycTransaction : build request binding ekyc -----> {} by refNo --->{}", bindingRequest,requestFromClient.getRefNo());
//        if(customerInfoMappingRepository.getAllHashIds().contains(bindingRequest.getCif())){
//            log.warn("bindingOnboard: Tai khoan da onboard");
//            throw new ExceptionHandler(ErrorMessage.ERR_37);
//        }
        return bindingProcess.bindingEkycTransaction(bindingRequest, jwtDto.getSecretKey());

    }

//    public String checkRequest(HttpServletRequest servletRequest, EkycTransactionRequest requestFromClient, String secretKey) {
//        List<String> strings = new ArrayList<>();
//        strings.add(servletRequest.getHeader(Constants.X_SOURCE_ENV));
//        strings.add(servletRequest.getHeader(Constants.X_REFERENCE_ID));
//        strings.add(requestFromClient.getRefNo());
//        strings.add(requestFromClient.getExtTransactionId());
//        strings.add(requestFromClient.getBioId());
//        strings.add(requestFromClient.getIdTypNo());
//        strings.add(requestFromClient.getBioLevel());
//        log.info("bindEkycTransaction list string ----->{} by refNo ---->{}", Convert.getReq(strings), requestFromClient.getRefNo());
//        return HmacUtil.genHmac(Convert.getReq(strings), secretKey);
//
//    }
}
