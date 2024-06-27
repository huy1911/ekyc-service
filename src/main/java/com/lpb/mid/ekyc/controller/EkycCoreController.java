package com.lpb.mid.ekyc.controller;

import com.lpb.mid.ekyc.dto.request.*;
import com.lpb.mid.ekyc.dto.response.VerifyEKYCTransactionRequest;
import com.lpb.mid.ekyc.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;

@RestController
@Log4j2
@RequiredArgsConstructor
public class EkycCoreController {
    @Autowired
    private NfcInfoService nfcInfoService;
    @Autowired
    private EkycInfoService ekycInfoService;
    private final EkycTransactionService ekycTransactionService;
    @Autowired
    private VerifyEKYCTransactionService verifyEKYCTransactionService;
    @Autowired
    private BindingOnboardingService bindingOnboardingService;

    private final EidInfoService eidInfoService;
    @Autowired
    private VerifyEidCardService verifyEidCardService;

    @PostMapping(value = "/compare-ocr-nfc")
    public ResponseEntity<?> compareOcrNfc(HttpServletRequest request, @RequestBody @Valid NfcInfoRequest nfcInfoRequest) {

        return ResponseEntity.ok().body(nfcInfoService.compareOcrNfc(request, nfcInfoRequest));
    }

    @PostMapping(value = "/authen-ekyc-onboarding")
    public ResponseEntity<?> authenEkycOnBoarding(HttpServletRequest request, @RequestBody @Valid EkycInfoRequest ekycInfoRequest) {
        return ResponseEntity.ok().body(ekycInfoService.authEkycOnBoarding(request, ekycInfoRequest));
    }

    @PostMapping(value = "/binding-onboard")
    public ResponseEntity<?> bindingOnboard(HttpServletRequest request, @RequestBody @Valid BindingOnboardRequest bindingOnboardRequest) throws IOException {
        return ResponseEntity.ok().body(bindingOnboardingService.bindingOnboard(request, bindingOnboardRequest));
    }

    @PostMapping(value = "/verify-ekyc-transaction")
    public ResponseEntity<?> verifyEKYCTransaction(HttpServletRequest request, @RequestBody  @Valid VerifyEKYCTransactionRequest verifyEKYCTransactionRequest) throws IOException {

        return ResponseEntity.ok().body(verifyEKYCTransactionService.verifyEKYCTransaction(request, verifyEKYCTransactionRequest));
    }


    @PostMapping("/binding-ekyc-transaction")
    public ResponseEntity<?> bindEkycTransaction(HttpServletRequest request, @RequestBody @Valid EkycTransactionRequest ekycTransactionRequest) throws IOException {

        return ResponseEntity.ok().body(ekycTransactionService.bindEkycTransaction(request, ekycTransactionRequest));
    }

    @GetMapping("/getInfo")
    public ResponseEntity<?> getInfoEkyc(HttpServletRequest request,@RequestParam(required=false) String idNumber) {
        return ResponseEntity.ok().body(ekycInfoService.getInfo(request,idNumber));
    }

    @PostMapping("/add-info-eid")
    public ResponseEntity<?> addEidInfo(HttpServletRequest request,@RequestBody @Valid EidInfoRequest eidInfoRequest) {
        return ResponseEntity.ok().body(eidInfoService.addEidInfo(request,eidInfoRequest));
    }

    @PostMapping("/detail-eid-card")
    public ResponseEntity<?> getDetailEidCard(HttpServletRequest request,@RequestBody @Valid DetailEidCardRequest detailEidInfoRequest) {
        return ResponseEntity.ok().body(eidInfoService.detailEidCard(request,detailEidInfoRequest));
    }
    @PostMapping(value = "/verify-eid-card")
    public ResponseEntity<?> verifyEKYCTransaction(HttpServletRequest request, @RequestBody  @Valid VerifyEidCardRequest verifyEidCardRequest) {

        return ResponseEntity.ok().body(verifyEidCardService.verifyEidCard(request, verifyEidCardRequest));
    }

}
