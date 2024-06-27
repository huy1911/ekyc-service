package com.lpb.mid.ekyc.util;

import com.lpb.mid.config.OKHttpFactory;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OkApi {
    @Autowired
    private TokenRsaUtils tokenRsaUtils;
    public Response restApi(String jsonRequest, String ekycUrl) throws IOException {
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), jsonRequest);
        Request.Builder builder = new Request.Builder();
        Request requestValidateEkyc = builder
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Authorization", "Bearer " + tokenRsaUtils.getPrivateKey())
                .url(ekycUrl)
                .post(requestBody)
                .build();
        OkHttpClient client = OKHttpFactory.getInstance().getHttpClient();
        return client.newCall(requestValidateEkyc).execute();
    }
}
