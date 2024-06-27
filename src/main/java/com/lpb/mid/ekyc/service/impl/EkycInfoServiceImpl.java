package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.data.ValidateEkycRequest;
import com.lpb.mid.ekyc.dto.request.InfoRequest;
import com.lpb.mid.ekyc.dto.response.InfoDto;
import com.lpb.mid.ekyc.dto.response.OCRPlusWithAdvanceDataInfoResponse;
import com.lpb.mid.ekyc.dto.response.UserInfoResponse;
import com.lpb.mid.ekyc.entity.CustomerInfoMapping;
import com.lpb.mid.ekyc.entity.EidCardInfo;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import com.lpb.mid.ekyc.dto.request.EkycInfoRequest;
import com.lpb.mid.ekyc.process.EkycProcess;
import com.lpb.mid.ekyc.process.OCRPlusWithAdvanceDataInfo;
import com.lpb.mid.ekyc.repository.CustomerInfoMappingRepository;
import com.lpb.mid.ekyc.repository.EidCardInfoDetailRepository;
import com.lpb.mid.ekyc.repository.EidCardInfoRepository;
import com.lpb.mid.ekyc.service.EkycInfoService;
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
import java.util.ArrayList;
import java.util.List;

@Service
@Log4j2
public class EkycInfoServiceImpl implements EkycInfoService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    @Autowired
    private EkycProcess ekycProcess;
    @Autowired
    private EidCardInfoDetailRepository eidCardInfoDetailRepository;

    @Autowired
    private CustomerInfoMappingRepository customerInfoMappingRepository;
    @Autowired
    private OCRPlusWithAdvanceDataInfo ocrPlusWithAdvanceDataInfo;
    @Autowired
    private EidCardInfoRepository eidCardInfoRepository;
    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;

    @Override
    public ResponseDto<?> authEkycOnBoarding(HttpServletRequest request, EkycInfoRequest ekycInfoRequest) {
        log.info("authEkycOnBoarding : request on boarding ----->{},by refNo ---->{}", ekycInfoRequest, ekycInfoRequest.getRefNo());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("authEkycOnBoarding: get userName token fail --->{}", ekycInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, ekycInfoRequest.getRefNo());
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("authEkycOnBoarding: get userName token fail by refNo ---> {}", ekycInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }

        String encodeReq = Convert.commonGetMac(request, new EkycInfoRequest(ekycInfoRequest), jwtDto.getSecretKey());
        log.info("authEkycOnBoarding : encode req Hmac ----->{} by refNo ---->{}", encodeReq, ekycInfoRequest.getRefNo());
        if (!encodeReq.equals(ekycInfoRequest.getMac())) {
            log.warn("authEkycOnBoarding : Mã hóa fail by RefNo --->{}", ekycInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }

        ValidateEkycRequest validateEkycRequest = ValidateEkycRequest.builder()
                .refNo(ekycInfoRequest.getRefNo())
                .ekycPartner(Constants.SINGALARITY)
                .ekycDeviceId(ekycInfoRequest.getEkycDeviceId())
                .ekycBiometricId(ekycInfoRequest.getEkycBiometricId())
                .ekycSessionId(ekycInfoRequest.getEkycSessionId())
                .ekycTrustLvl(ekycInfoRequest.getEkycTrustLvl())
                .idTypNo(ekycInfoRequest.getIdTypNo())
                .ekycType(ekycInfoRequest.getEkycType())
                .build();
        log.info("authEkycOnBoarding : build ValidateEkycRequest ---->{} by refNo -->{}", validateEkycRequest, ekycInfoRequest.getRefNo());
        return ekycProcess.validateEkyc(validateEkycRequest, jwtDto.getSecretKey());


    }


    @Override
    public ResponseDto<?> getInfo(HttpServletRequest request, String idNumber) {
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("getInfo: get userName token fail");
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto, topicSend, topicReply, replyingTemplate, Constants.Authorization, sendKafkaDto.getCustomerNo());
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("getInfo: get token fail");
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        List<InfoDto> infoDtos;
        if (idNumber != null) {
            String idNumberAes = HmacUtil.decrypt(idNumber,jwtDto.getSecretKey(),Constants.SINGALARITY);
            log.info("getInfo : idNumberAes ----> {}",idNumberAes);
            infoDtos = eidCardInfoDetailRepository.queryCifInfoByIdNumber(idNumberAes);
        } else {
            infoDtos = eidCardInfoDetailRepository.queryCifInfo(jwtDto.getCustomerNo());
        }
        if (infoDtos.size() == 0) {
            log.warn("getInfo : get Info fail by cif ----> {}", jwtDto.getCustomerNo());
            throw new ExceptionHandler(ErrorMessage.ERR_27);
        }
        InfoDto infoDto = infoDtos.get(0);
        log.info("getInfo : infoDto --->{}", infoDto);
        String oldIdentify = infoDto.getOldIdentify();
        if (StringUtils.isEmpty(infoDto.getOldIdentify())) {
            InfoRequest infoRequest = InfoRequest.builder()
                    .ekycBiometricId(infoDto.getEkycBiometricId())
                    .ekycDeviceId(infoDto.getEkycDeviceId())
                    .ekycSessionId(infoDto.getEkycSessionId())
                    .ekycType(infoDto.getEkycType())
                    .nfcSession(infoDto.getNfcSession())
                    .nfcType(infoDto.getNfcType())
                    .refNo(infoDto.getRefNo())
                    .build();
            OCRPlusWithAdvanceDataInfoResponse ocrPlusWithAdvanceDataInfoResponse = ocrPlusWithAdvanceDataInfo.getOCRPlusWithAdvanceData(infoRequest);
            if (ocrPlusWithAdvanceDataInfoResponse.getResult().getResponseCode().equals(Constants.GW999)) {
                log.warn("getInfo : Ocr Plus dữ liệu nâng cao thất bại ----> {}", jwtDto.getCustomerNo());
                throw new ExceptionHandler(ErrorMessage.ERR_41);
            }
            if (!ObjectUtils.isEmpty(ocrPlusWithAdvanceDataInfoResponse)) {
                log.warn("getInfo: get ocrPlusWithAdvanceDataInfoResponse by user ---->{}", jwtDto.getCustomerNo());
                oldIdentify = ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getOldIdentify();
            }
            log.info("getInfo: get old identify ---> {} by customer ---> {}", oldIdentify, jwtDto.getCustomerNo());
            EidCardInfo eidCardInfo = eidCardInfoRepository.findFirstByCustomerNo(jwtDto.getCustomerNo());

            if (ObjectUtils.isEmpty(eidCardInfo) || StringUtils.isEmpty(eidCardInfo.getIdNumber())) {
                log.warn("getInfo : get eidCardInfo fail ----> {}", jwtDto.getCustomerNo());
                throw new ExceptionHandler(ErrorMessage.ERR_27);
            }
            eidCardInfo.setOldIdentify(oldIdentify);
            eidCardInfoRepository.save(eidCardInfo);
            EidCardInfoDetail eidCardInfoDetail = eidCardInfoDetailRepository.findFirstByIdNumberOrderByCreateByDesc(eidCardInfo.getIdNumber());
            if (ObjectUtils.isEmpty(eidCardInfoDetail)) {
                log.warn("getInfo : get eidCardInfoDetail fail ----> {}", jwtDto.getCustomerNo());
                throw new ExceptionHandler(ErrorMessage.ERR_27);
            }
            eidCardInfoDetail.setGender(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getGender());
            eidCardInfoDetail.setHomeTown(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getHomeTown());
            eidCardInfoDetail.setMotherName(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getMotherName());
            eidCardInfoDetail.setFatherName(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getFatherName());
            eidCardInfoDetail.setNationality(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getNationality());
            eidCardInfoDetail.setReligion(ocrPlusWithAdvanceDataInfoResponse.getData().getCitizenInfo().getReligion());
            eidCardInfoDetailRepository.save(eidCardInfoDetail);
        }
        infoDto.setOldIdentify(oldIdentify);
        UserInfoResponse userInfoResponse = new UserInfoResponse();
        UserInfoResponse.customerInfo customerInfo = new UserInfoResponse.customerInfo();
        CustomerInfoMapping customerInfoMapping = customerInfoMappingRepository.findFirstByCustomerNo(jwtDto.getCustomerNo());
        if (!ObjectUtils.isEmpty(customerInfoMapping)) {
            userInfoResponse.setHashId(customerInfoMapping.getHashId());
            customerInfo.setCardType(customerInfoMapping.getUdf1());
            customerInfo.setBioLevel(customerInfoMapping.getUdf5());
            userInfoResponse.setEkycBiometricId(customerInfoMapping.getUdf3());
        }
        customerInfo.setOldIdentify(infoDto.getOldIdentify());
        customerInfo.setIdTypeNo(infoDto.getIdTypeNo());
        customerInfo.setIsVerifyRar(infoDto.getIsVerifyRar());

        userInfoResponse.setCustomerInfo(customerInfo);
        userInfoResponse.setMac(setMacInfo(userInfoResponse, jwtDto.getSecretKey()));
        return ResponseDto.builder()
                .type(ErrorMessage.ERR_000.type)
                .statusCode(ErrorMessage.ERR_000.code)
                .description(ErrorMessage.ERR_000.message)
                .data(userInfoResponse)
                .build();
    }

    public String setMacInfo(UserInfoResponse userInfoResponse, String secretKey) {
        List<String> strings = new ArrayList<>();
        strings.add(userInfoResponse.getCustomerInfo().getIsVerifyRar());
        strings.add(userInfoResponse.getHashId());
        strings.add(userInfoResponse.getCustomerInfo().getIdTypeNo());
        strings.add(userInfoResponse.getCustomerInfo().getBioLevel());
        strings.add(userInfoResponse.getCustomerInfo().getCardType());
        strings.add(userInfoResponse.getEkycBiometricId());
        log.info("authEkycOnBoarding list string ----->{}", Convert.getReq(strings));
        return HmacUtil.genHmac(Convert.getReq(strings), secretKey);
    }

}
