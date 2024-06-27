package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.dto.request.DetailEidCardRequest;
import com.lpb.mid.ekyc.dto.request.EidInfoRequest;
import com.lpb.mid.ekyc.dto.response.EidCardInfoDto;
import com.lpb.mid.ekyc.entity.EidCardInfo;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import com.lpb.mid.ekyc.repository.EidCardInfoDetailRepository;
import com.lpb.mid.ekyc.repository.EidCardInfoRepository;
import com.lpb.mid.ekyc.service.EidInfoService;
import com.lpb.mid.exception.ErrorMessage;
import com.lpb.mid.exception.ExceptionHandler;
import com.lpb.mid.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;
import org.springframework.kafka.requestreply.RequestReplyFuture;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.lpb.mid.ekyc.util.Contants.EID_CHANNEL;

@Service
@Log4j2
@RequiredArgsConstructor
public class EidInfoServiceImpl implements EidInfoService {

    private final EidCardInfoRepository eidCardInfoRepository;

    private final EidCardInfoDetailRepository eidCardInfoDetailRepository;

    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;

    private final ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;

    @Override
    @Transactional
    public ResponseDto<?> addEidInfo(HttpServletRequest request, EidInfoRequest eidInfoRequest) {
        log.info("addEidInfo: request add eid info ----->{}", eidInfoRequest.getIdNumber());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("addEidInfo: get userName token fail --->{}", eidInfoRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        RequestReplyFuture<String, Object, Object> replyFuture = KafkaUtils.sendMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate);
        ConsumerRecord<String, Object> consumerRecord;
        JWTDto jwtDto;
        try {
            consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            String val = consumerRecord.value().toString();
            jwtDto = StringConvertUtils.readValueWithInsensitiveProperties(val, JWTDto.class);
        } catch (Exception e) {
            log.error("addEidInfo: Fail to get message from kafka by idNumber ---->{}--->{}", eidInfoRequest.getIdNumber(), e.getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_90);
        }
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("addEidInfo: get userName token fail --->{}", eidInfoRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = getMacFromRequest(request, eidInfoRequest, jwtDto.getSecretKey());
        if (!encodeReq.equals(eidInfoRequest.getMac())) {
            log.warn("addEidInfo: Mac is incorrect, request mac--->{}, calculator mac ---> {} for request ---> {}", eidInfoRequest.getMac(), encodeReq, eidInfoRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        EidCardInfo eidCardInfo = eidCardInfoRepository.findByIdNumber(eidInfoRequest.getIdNumber());
        if (!ObjectUtils.isEmpty(eidCardInfo)) {
            log.warn("addEidInfo: already exist user by idNumber ---->{} and date ---->{}", eidInfoRequest.getIdNumber(), eidInfoRequest.getIssueDate());
            EidCardInfoDetail eidCardInfoDetailList = eidCardInfoDetailRepository.findByIdNumber(eidInfoRequest.getIdNumber());
            updateInformation(eidCardInfo, eidCardInfoDetailList, eidInfoRequest);
        } else {
            log.warn("addEidInfo: create new user by idNumber ---->{} and date ---->{}", eidInfoRequest.getIdNumber(), eidInfoRequest.getIssueDate());
            EidCardInfo eidCardInfoNew = new ModelMapper().map(eidInfoRequest, EidCardInfo.class);
            eidCardInfoNew.setImage(eidInfoRequest.getEidImage());
            eidCardInfoNew.setCreatedDate(new Date());
            eidCardInfoNew.setOldIdentify(eidInfoRequest.getOldIdNumber());
            eidCardInfoNew.setCreatedBy(jwtDto.getCustomerNo());
            eidCardInfoNew.setChannel(EID_CHANNEL);
            eidCardInfoRepository.save(eidCardInfoNew);
            EidCardInfoDetail eidCardInfoDetail = new ModelMapper().map(eidInfoRequest, EidCardInfoDetail.class);
            eidCardInfoDetail.setCreateDate(new Date());
            eidCardInfoDetail.setCreateBy(jwtDto.getCustomerNo());
            eidCardInfoDetailRepository.save(eidCardInfoDetail);
        }
        return ResponseDto.builder()
                .type(ErrorMessage.ERR_000.type)
                .statusCode(ErrorMessage.ERR_000.code)
                .description(ErrorMessage.ERR_000.message)
                .build();
    }

    @Override
    public ResponseDto<?> detailEidCard(HttpServletRequest request, DetailEidCardRequest detailEidCardRequest) {
        log.info("detailEidCard: request get detail eid info ----->{}", detailEidCardRequest.getIdNumber());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("detailEidCard: get userName token fail --->{}", detailEidCardRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        RequestReplyFuture<String, Object, Object> replyFuture = KafkaUtils.sendMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate);
        ConsumerRecord<String, Object> consumerRecord;
        JWTDto jwtDto;
        try {
            consumerRecord = replyFuture.get(10, TimeUnit.SECONDS);
            String val = consumerRecord.value().toString();
            jwtDto = StringConvertUtils.readValueWithInsensitiveProperties(val, JWTDto.class);
        } catch (Exception e) {
            log.error("detailEidCard: Fail to get message from kafka ---->{}--->{}", detailEidCardRequest.getIdNumber(), e.getMessage());
            throw new ExceptionHandler(ErrorMessage.ERR_90);
        }
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("detailEidCard: get userName token fail ---->{}", detailEidCardRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = getMacFromRequest(request, detailEidCardRequest, jwtDto.getSecretKey());
        if (!encodeReq.equals(detailEidCardRequest.getMac())) {
            log.warn("detailEidCard: Mac is incorrect! request mac--->{}, calculator mac ---> {}, for request ---> {}", detailEidCardRequest.getMac(), encodeReq, detailEidCardRequest.getIdNumber());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        EidCardInfoDto eidCardInfoDto = eidCardInfoDetailRepository.findDetailInfoByIdNumberAndIssueDate(detailEidCardRequest.getIdNumber(), detailEidCardRequest.getIssueDate());
        if(ObjectUtils.isEmpty(eidCardInfoDto)){
            throw new ExceptionHandler(ErrorMessage.ERR_29);
        }
        eidCardInfoDto.setMac(getMacForResponse(eidCardInfoDto, jwtDto.getSecretKey()));
        return ResponseDto.builder()
                .type(ErrorMessage.ERR_000.type)
                .statusCode(ErrorMessage.ERR_000.code)
                .description(ErrorMessage.ERR_000.message)
                .data(eidCardInfoDto)
                .build();
    }

    public String getMacFromRequest(HttpServletRequest request, Object object, String secretKey){
        List<String> strings = new ArrayList<>();
        strings.add(request.getHeader(Constants.X_SOURCE_ENV));
        strings.add(request.getHeader(Constants.X_REFERENCE_ID));
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value != null && !field.getName().equals("mac")) {
                    strings.add(value.toString());
                }
            } catch (IllegalAccessException e) {
                log.error("Can not get Mac from request Object ----->{}, cause ----->{}", object, e.getMessage());
            }
        }
        return HmacUtil.genHmac(Convert.getReq(strings), secretKey);
    }

    public String getMacForResponse(Object object, String secretKey){
        List<String> strings = new ArrayList<>();
        Field[] fields = object.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                Object value = field.get(object);
                if (value != null && !field.getName().equals("mac")) {
                    strings.add(value.toString());
                }
            } catch (IllegalAccessException e) {
                log.error("Can not get Mac for response Object ----->{}, cause ----->{}", object, e.getMessage());
            }
        }
        return HmacUtil.genHmac(Convert.getReq(strings), secretKey);
    }

    private void updateInformation(EidCardInfo eidCardInfo, EidCardInfoDetail eidCardInfoDetail, EidInfoRequest eidInfoRequest){
        eidCardInfo.setChannel(EID_CHANNEL);
        if(StringUtils.isNotBlank(eidInfoRequest.getOldIdNumber())){
            eidCardInfo.setOldIdentify(eidInfoRequest.getOldIdNumber());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getFullName())){
            eidCardInfo.setFullName(eidInfoRequest.getFullName());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getDob())){
            eidCardInfo.setDob(eidInfoRequest.getDob());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getEidImage())){
            eidCardInfo.setImage(eidInfoRequest.getEidImage());
        }
//        if(StringUtils.isNotBlank(eidCardInfoDetail.getDocumentSigningCertificate()) && eidCardInfoDetail.getDocumentSigningCertificate().equals(eidInfoRequest.getDocumentSigningCertificate())) {
//            if (StringUtils.isNotBlank(eidInfoRequest.getIsVerify())) {
//                if (StringUtils.isBlank(eidCardInfo.getIsVerify())) {
//                    eidCardInfo.setIsVerify(eidInfoRequest.getIsVerify());
//                } else if (eidInfoRequest.getIsVerify().equals("Y") && eidCardInfo.getIsVerify().equals("N")) {
//                    eidCardInfo.setIsVerify(eidInfoRequest.getIsVerify());
//                }
//            }
//        } else if (StringUtils.isNotBlank(eidInfoRequest.getDocumentSigningCertificate())){
//            eidCardInfo.setIsVerify(eidInfoRequest.getIsVerify());
//        }
        if (StringUtils.isNotBlank(eidInfoRequest.getIsVerify())) {
            if(StringUtils.isBlank(eidCardInfo.getIsVerify())){
                eidCardInfo.setIsVerify(eidInfoRequest.getIsVerify());
            }else if (!(eidCardInfo.getIsVerify().equals("Y") &&
                    eidInfoRequest.getIssueDate().equals(eidCardInfo.getIssueDate()))) {
                eidCardInfo.setIsVerify(eidInfoRequest.getIsVerify());
            }
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getIssueDate())){
            eidCardInfo.setIssueDate(eidInfoRequest.getIssueDate());
        }
        eidCardInfoRepository.save(eidCardInfo);
        if(StringUtils.isNotBlank(eidInfoRequest.getGender())){
            eidCardInfoDetail.setGender(eidInfoRequest.getGender());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getEmplCode())){
            eidCardInfoDetail.setEmplCode(eidInfoRequest.getEmplCode());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getLogging())){
            eidCardInfoDetail.setLogging(eidInfoRequest.getLogging());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getNationality()) ){
            eidCardInfoDetail.setNationality(eidInfoRequest.getNationality());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getNation())){
            eidCardInfoDetail.setNation(eidInfoRequest.getNation());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getReligion())){
            eidCardInfoDetail.setReligion(eidInfoRequest.getReligion());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getHomeTown())){
            eidCardInfoDetail.setHomeTown(eidInfoRequest.getHomeTown());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getIdentifying())){
            eidCardInfoDetail.setIdentifying(eidInfoRequest.getIdentifying());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getExpiredDate())){
            eidCardInfoDetail.setExpiredDate(eidInfoRequest.getExpiredDate());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getFatherName())){
            eidCardInfoDetail.setFatherName(eidInfoRequest.getFatherName());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getMotherName())){
            eidCardInfoDetail.setMotherName(eidInfoRequest.getMotherName());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getHusbandOrWifeName())){
            eidCardInfoDetail.setHusbandOrWifeName(eidInfoRequest.getHusbandOrWifeName());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getPlaceOfResidence())){
            eidCardInfoDetail.setPlaceOfResidence(eidInfoRequest.getPlaceOfResidence());
        }
        if(StringUtils.isNotBlank(eidInfoRequest.getDocumentSigningCertificate())){
            eidCardInfoDetail.setDocumentSigningCertificate(eidInfoRequest.getDocumentSigningCertificate());
        }
        eidCardInfoDetailRepository.save(eidCardInfoDetail);
    }
}
