package com.lpb.mid.ekyc.service.impl;

import com.lpb.mid.dto.JWTDto;
import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.dto.SendKafkaDto;
import com.lpb.mid.ekyc.data.ValidateNFCInfoRequest;
import com.lpb.mid.ekyc.dto.request.InfoRequest;
import com.lpb.mid.ekyc.dto.request.VerifyDsCertRequest;
import com.lpb.mid.ekyc.dto.response.CompareOcrNfcResponse;
import com.lpb.mid.ekyc.dto.response.OCRPlusResponse;
import com.lpb.mid.ekyc.dto.response.OCRPlusWithAdvanceDataInfoResponse;
import com.lpb.mid.ekyc.entity.EidCardInfo;
import com.lpb.mid.ekyc.entity.EidCardInfoDetail;
import com.lpb.mid.ekyc.dto.request.NfcInfoRequest;
import com.lpb.mid.ekyc.process.CompareOcrNfc;
import com.lpb.mid.ekyc.process.OCRPlusWithAdvanceDataInfo;
import com.lpb.mid.ekyc.process.VerifyDsCertProcess;
import com.lpb.mid.ekyc.repository.EidCardInfoDetailRepository;
import com.lpb.mid.ekyc.repository.EidCardInfoRepository;
import com.lpb.mid.ekyc.service.NfcInfoService;
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
import javax.transaction.Transactional;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

@Service
@Log4j2
public class NfcInfoServiceImpl implements NfcInfoService {
    @Value("${kafka.send.topic}")
    private String topicSend;
    @Value("${kafka.reply.topic}")
    private String topicReply;
    @Autowired
    private CompareOcrNfc compareOcrNfc;
    @Autowired
    private EidCardInfoDetailRepository eidCardInfoDetailRepository;
    @Autowired
    private OCRPlusWithAdvanceDataInfo ocrPlusWithAdvanceDataInfo;
    @Autowired
    private VerifyDsCertProcess verifyDsCertProcess;
    @Autowired
    private EidCardInfoRepository eidCardInfoRepository;
    @Autowired
    private ReplyingKafkaTemplate<String, Object, Object> replyingTemplate;

    @Override
    @Transactional
    public ResponseDto<?> compareOcrNfc(HttpServletRequest request, NfcInfoRequest nfcInfoRequest) {

        log.info("CompareOcrNfc : request CompareOcrNfc ----> {},by refNo --->{}", nfcInfoRequest, nfcInfoRequest.getRefNo());
        SendKafkaDto sendKafkaDto = Convert.getSendKafkaDto(request);
        if (ObjectUtils.isEmpty(sendKafkaDto)) {
            log.warn("CompareOcrNfc: get userName token fail by refNo --->{}", nfcInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        JWTDto jwtDto = KafkaUtil.getMessageKafka(sendKafkaDto,topicSend,topicReply,replyingTemplate,Constants.Authorization,nfcInfoRequest.getRefNo());
        if (ObjectUtils.isEmpty(jwtDto)) {
            log.warn("CompareOcrNfc: get user token fail ,by refNo ---->{}", nfcInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_13);
        }
        String encodeReq = Convert.commonGetMac(request, new NfcInfoRequest(nfcInfoRequest), jwtDto.getSecretKey());
        log.info("CompareOcrNfc : encode req Hmac ----->{},by refNo ---->{}", encodeReq, nfcInfoRequest.getRefNo());
        if (!encodeReq.equals(nfcInfoRequest.getMac())) {
            log.warn("CompareOcrNfc : Mã hóa fail by refNo -->{}", nfcInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_21);
        }
        EidCardInfo eidCardInfo = eidCardInfoRepository.findFirstByCustomerNoOrIdNumberOrderByCreatedByDesc(jwtDto.getCustomerNo(), nfcInfoRequest.getIdNumber());
        if (!ObjectUtils.isEmpty(eidCardInfo) && StringUtils.isEmpty(eidCardInfo.getCustomerNo())) {
            eidCardInfo.setCustomerNo(jwtDto.getCustomerNo());
            eidCardInfoRepository.save(eidCardInfo);
        }
        if (!ObjectUtils.isEmpty(eidCardInfo) && (!eidCardInfo.getCustomerNo().equals(jwtDto.getCustomerNo()) ||
                !eidCardInfo.getIdNumber().equals(nfcInfoRequest.getIdNumber()))) {
            log.warn("CompareOcrNfc : dữ liệu không hợp lệ by customerNo ---->{},by refNo ---->{}", jwtDto.getCustomerNo(), nfcInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_23);
        }
        ValidateNFCInfoRequest validateNFCInfoRequest = ValidateNFCInfoRequest.builder()
                .refNo(nfcInfoRequest.getRefNo())
                .ekycPartner(Constants.SINGALARITY)
                .ekycDeviceId(nfcInfoRequest.getEkycDeviceId())
                .ekycBiometricId(nfcInfoRequest.getEkycBiometricId())
                .ekycSessionId(nfcInfoRequest.getEkycSessionId())
                .ekycType(nfcInfoRequest.getEkycType())
                .nfcSession(nfcInfoRequest.getNfcSession())
                .nfcType(nfcInfoRequest.getNfcType())
                .idNumber(nfcInfoRequest.getIdNumber())
                .fullName(nfcInfoRequest.getFullName())
                .birthDate(nfcInfoRequest.getBirthDate())
                .idIssueDate(nfcInfoRequest.getIdIssueDate())
                .idExpireDate(nfcInfoRequest.getIdExpireDate())
                .build();
        InfoRequest infoRequest = InfoRequest.builder()
                .ekycBiometricId(nfcInfoRequest.getEkycBiometricId())
                .ekycDeviceId(nfcInfoRequest.getEkycDeviceId())
                .ekycSessionId(nfcInfoRequest.getEkycSessionId())
                .ekycType(nfcInfoRequest.getEkycType())
                .nfcSession(nfcInfoRequest.getNfcSession())
                .nfcType(nfcInfoRequest.getNfcType())
                .refNo(nfcInfoRequest.getRefNo())
                .build();
        CompletableFuture<OCRPlusWithAdvanceDataInfoResponse> ocrPlusAdvanResponse = CompletableFuture.supplyAsync(() ->
                ocrPlusWithAdvanceDataInfo.getOCRPlusWithAdvanceData(infoRequest));
        CompletableFuture<CompareOcrNfcResponse> compareOcrResponse = CompletableFuture.supplyAsync(() ->
                compareOcrNfc.validateNfcInfo(validateNFCInfoRequest, jwtDto.getSecretKey())
        );
        CompletableFuture<OCRPlusResponse> result = ocrPlusAdvanResponse.thenCombine(compareOcrResponse, this::merge).whenComplete((res, ex) -> log.error("CompareOcrNfc : CompletableFuture fail "));
        OCRPlusResponse ocrPlusResponse = result.join();
        if (ocrPlusResponse.getCompareResponse().getNfcCheckValidResult() == null || ObjectUtils.isEmpty(ocrPlusResponse.getOcrPlusInfoResponse())
                || !ocrPlusResponse.getOcrPlusInfoResponse().getResult().getResponseCode().equals("00")) {
            log.info("CompareOcrNfc : build compareOcrNfcResponse fail by user  ----> {},by refNo ---->{}", jwtDto.getCustomerNo(), nfcInfoRequest.getRefNo());
            throw new ExceptionHandler(ErrorMessage.ERR_39);
        }
        VerifyDsCertRequest verifyDsCertRequest = VerifyDsCertRequest.builder()
                .deviceType(Constants.MOBILE)
                .idCard(ocrPlusResponse.getOcrPlusInfoResponse().getData().getCitizenInfo().getCitizenPid())
                .dsCert(ocrPlusResponse.getOcrPlusInfoResponse().getData().getCitizenInfo().getDocSigningCertificate())
                .code(Constants.LPBANK_UAT)
                .province(ocrPlusResponse.getOcrPlusInfoResponse().getData().getCitizenInfo().getHomeTown())
                .build();
        if(ocrPlusResponse.getOcrPlusInfoResponse().getData().getCitizenInfo().getHomeTown() == null){
            verifyDsCertRequest.setProvince(ocrPlusResponse.getOcrPlusInfoResponse().getData().getCitizenInfo().getRegPlaceAddress());
        }
        EidCardInfoDetail eidCardInfoDetail = eidCardInfoDetailRepository.findFirstByIdNumberOrderByCreateByDesc(nfcInfoRequest.getIdNumber());
        if (ObjectUtils.isEmpty(eidCardInfo)) {
            String isVerify = verifyDsCertProcess.validateDsCert(verifyDsCertRequest, nfcInfoRequest.getRefNo());
            if (isVerify.equals(Constants.isVeriFyDsFail)) {
                log.info("CompareOcrNfc : is verify rar fail by user  ----> {},refNo ---->{}", jwtDto.getCustomerNo(), nfcInfoRequest.getRefNo());
                throw new ExceptionHandler(ErrorMessage.ERR_40);
            }
            saveEidCardInfo(ocrPlusResponse.getCompareResponse(), jwtDto, validateNFCInfoRequest, isVerify, null);
        } else {
            String isVerify = verifyDsCertProcess.validateDsCert(verifyDsCertRequest, nfcInfoRequest.getRefNo());
            if (isVerify.equals(Constants.isVeriFyDsFail)) {
                log.info("CompareOcrNfc : is verify rar fail by user  ----> {},refNo ---->{}", jwtDto.getCustomerNo(), nfcInfoRequest.getRefNo());
                throw new ExceptionHandler(ErrorMessage.ERR_40);
            }
            saveEidCardInfo(ocrPlusResponse.getCompareResponse(), jwtDto, validateNFCInfoRequest, isVerify, eidCardInfo);
        }
        if (ObjectUtils.isEmpty(eidCardInfoDetail)) {
            saveEidCardInfoDetail(ocrPlusResponse.getCompareResponse(), jwtDto, null);
        } else {
            saveEidCardInfoDetail(ocrPlusResponse.getCompareResponse(), jwtDto, eidCardInfoDetail);
        }
        return ResponseDto.builder()
                .type(ErrorMessage.ERR_000.type)
                .statusCode(ErrorMessage.ERR_000.code)
                .description(ErrorMessage.ERR_000.message)
                .data(ocrPlusResponse.getCompareResponse()).build();

    }

    private OCRPlusResponse merge(OCRPlusWithAdvanceDataInfoResponse fd1Value, CompareOcrNfcResponse fd2Value) {
        OCRPlusResponse ocrPlusResponse = new OCRPlusResponse();
        ocrPlusResponse.setOcrPlusInfoResponse(fd1Value);
        ocrPlusResponse.setCompareResponse(fd2Value);
        return ocrPlusResponse;
    }


    private void saveEidCardInfoDetail(CompareOcrNfcResponse compareOcrNfcResponse, JWTDto jwtDto, EidCardInfoDetail eidCardInfoDetail1) {
        try {
            if (eidCardInfoDetail1 == null) {
                EidCardInfoDetail eidCardInfoDetail = new EidCardInfoDetail();
                saveEidDetail(compareOcrNfcResponse, jwtDto, eidCardInfoDetail);
            } else {
                saveEidDetail(compareOcrNfcResponse, jwtDto, eidCardInfoDetail1);
            }

        } catch (Exception e) {
            log.error("saveEidCardInfoDetail : errr ---->{}", e.getMessage());
        }
    }

    private void saveEidDetail(CompareOcrNfcResponse compareOcrNfcResponse, JWTDto jwtDto, EidCardInfoDetail eidCardInfoDetail1) {
        eidCardInfoDetail1.setIdNumber(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getCitizenPid());
        eidCardInfoDetail1.setFc(compareOcrNfcResponse.getNfcCheckValidResult().getFc());
        eidCardInfoDetail1.setFm(compareOcrNfcResponse.getNfcCheckValidResult().getFm());
        eidCardInfoDetail1.setDg1(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getDg1());
        eidCardInfoDetail1.setDg2(GzipCompression.encodeSha256(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getDg2()));
        eidCardInfoDetail1.setDg13(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getDg13());
        eidCardInfoDetail1.setSod(GzipCompression.encodeSha256(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getSod()));
        eidCardInfoDetail1.setCreateBy(jwtDto.getCustomerNo());
        eidCardInfoDetail1.setCreateDate(new Date());
        eidCardInfoDetailRepository.saveAndFlush(eidCardInfoDetail1);

        log.info("saveEidCardInfoDetail : save saveEidCardInfoDetail success by idNumber ----> {}", compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getCitizenPid());
    }

    private void saveEidCardInfo(CompareOcrNfcResponse compareOcrNfcResponse, JWTDto jwtDto, ValidateNFCInfoRequest request, String isVerify, EidCardInfo eidCardInfo1) {
        try {
            if (eidCardInfo1 == null) {
                EidCardInfo eidCardInfo = new EidCardInfo();
                saveEidInfo(compareOcrNfcResponse, jwtDto, request, isVerify, eidCardInfo);
            } else {
                saveEidInfo(compareOcrNfcResponse, jwtDto, request, isVerify, eidCardInfo1);
            }

        } catch (Exception exception) {
            log.error("saveEidCardInfo : err -----> {}", exception.getMessage());
        }
    }

    private void saveEidInfo(CompareOcrNfcResponse compareOcrNfcResponse, JWTDto jwtDto, ValidateNFCInfoRequest request, String isVerify, EidCardInfo eidCardInfo1) {
        eidCardInfo1.setCustomerNo(jwtDto.getCustomerNo());
        eidCardInfo1.setChannel("LV24");
        eidCardInfo1.setIdNumber(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getCitizenPid());
        eidCardInfo1.setFullName(request.getFullName());
        eidCardInfo1.setCustomerType(request.getNfcType());
        eidCardInfo1.setImage(GzipCompression.encodeSha256(compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getImage()));
        eidCardInfo1.setCreatedBy(jwtDto.getCustomerNo());
        eidCardInfo1.setCreatedDate(new Date());
        eidCardInfo1.setRefNo(request.getRefNo());
        eidCardInfo1.setUdf1(request.getEkycBiometricId());
        eidCardInfo1.setUdf2(request.getEkycDeviceId());
        eidCardInfo1.setUdf3(request.getEkycSessionId());
        eidCardInfo1.setUdf4(request.getEkycType());
        eidCardInfo1.setUdf5(request.getNfcSession());
        eidCardInfo1.setUdf6(request.getNfcType());
        eidCardInfo1.setIsVerify(isVerify);
        eidCardInfo1.setDateVerify(new Date());
        eidCardInfoRepository.saveAndFlush(eidCardInfo1);
        log.info("saveEidCardInfo : save saveEidCardInfo success by idNumber ----> {}", compareOcrNfcResponse.getNfcCheckValidResult().getCitizenInfo().getCitizenPid());
    }

}
