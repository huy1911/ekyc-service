package com.lpb.mid.ekyc.service;

import com.lpb.mid.dto.ResponseDto;
import com.lpb.mid.ekyc.dto.request.BindingOnboardRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface BindingOnboardingService {
    ResponseDto<?> bindingOnboard(HttpServletRequest request, BindingOnboardRequest bindingOnboardRequest) throws IOException;
}
