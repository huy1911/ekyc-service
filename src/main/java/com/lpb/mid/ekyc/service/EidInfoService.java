package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.DetailEidCardRequest;
import com.lpb.mid.ekyc.dto.request.EidInfoRequest;

import javax.servlet.http.HttpServletRequest;

public interface EidInfoService {
    ResponseDto<?> addEidInfo(HttpServletRequest request, EidInfoRequest eidInfoRequest);

    ResponseDto<?> detailEidCard(HttpServletRequest request, DetailEidCardRequest detailEidInfoRequest);
}
