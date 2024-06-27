package com.lpb.mid.ekyc.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
public class TokenRsaUtils {
    @Value("${key.private}")
    private String privateKey;

    public  String getPrivateKey() {
        try {
            String rsaPrivateKey = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCHLw+oq45LwR8sf4De1rQXZg8Xl4LH+FGjOxq8PbbSA+wshKnAjREwqxKyS2Gk+oN/GPiuUsjKYziyQkXPkmZ2+jgFgflgSUQWxQljP1/dibiD6TavaWUXKhBshzDkk4odT8l3qu3BNsD9YLo5WyhPa8LDEbxVEuXdhK54q9OSwzYSgCqjiRGgLaXUhrMf1a0fOJjgFOlM6yw6IV/pVHIEuLIf+RF6iBItWyHWwxbk13NfFjyJOb8KHg1Qmkmp4bGbD5f5+9gSrKxe1IHi2HIIf2e61LqZpXX+ImrP5lwHPZ5U1y4+NU/1L9FHuZkwcIhXB+AcyFMVAo+/IaWZyG5TAgMBAAECggEAURl9pm4NdwsL/bEcihN6hVMYGMovjnI939R0jJvlkwcBaA/KmxbGSlFHbSlmEgisXNKJVmOZzDlMaTzzDr1AtX4Vn9BC4G3z6EkbAayigZC+3nxJ72AmYog8xC8yuN5+jGkyb5Ve/3wuuZOPBRvYChWXCVoZh1xIBOY1mXVT/4YzYy8ers+2vASGuf4kBPd8Sn10YwXtxlwshkp76V/IMk0+89G/P/OnMy2dTtVkvzParLx4aGOl2dGdNz0I8NmYsvQ5I83zVb8duFLEn3Qv6mjwNw/c/eqv+nS2GYZ0bu2VO8kxMslVSP0QG42/4t6dJz70fk6wSu9HSSJlY0+DMQKBgQDk40/EqX4rVAGGo+s7NbEMM8FpTOCEpa9NTErTZWYCPpTuXY3aHsdoO80/7gSy3jBMwh6x3iFajynZ0l0lOcd0oWCffUPqLAJkrijFna5BKEUsTkzT5vkZnj2M0MkIpUELVJv6MSu0qz2RGzjPyhiVXilr3diuqiHkGEyKyyMF2QKBgQCXMk+ld7mrSdpI1P1HfYOIqnJUTSzFGE6tBJGMcdUSZYl3twhgSHrqRMMEW1yndwMuPYtxMOZOlIUxHGnLefajU9TMzAfSD6tsR7Nx7JGFWsMlJjJUtlWr9AnSLV3JYeHgmUYTvHBOlBzkSmPrd/+k9MQmjywTOihCbajsR73eCwKBgQCQBhCteNFxRhznscFUsoZDXVW4gq1MCk1yYC09M7KqeuKP0sJtm6xyBB80uPqv6muOAR/7YACw0SDOdTYzNFYzJ1B7SwEvGp4u5/+zyXJagZJ1PJfPRqk723xzXPUCwalaDdVP6xzLRG0rH2vyJAKwCl4aB7BOx7MnqbqQIx5YkQKBgGfsRt5e+pmHe6gP7b9TwDMRpN/Y4+rdGEbIGxrDq0anz+MfRXyYlROtC7ZU7cyVevQvjUbR65sbbqkg9H7NZ/8/Qcc6fi2eY59bcTXe+u8EQZmpNXMQmLZGWPHECPDAfLcZ4xbxsOfx2iH4F9Hjey3wp1oxkoPst3thUHiHnzOdAoGBAJtNXi+40KBS4lwVOrg9FUIEnzpndKIvF5jEYf9f0XtGzsoCybLMiDaxAoAtQmksXo97Shc2Gjc9RumLqkrJxSQsyhADICYYa6brC/cILS8lr1PzMj1HgvmpM6o0eaC0sBPTyNLOS77sHIpJ3xDWOagzG8r1f8RA37T3xSp5EdX8";

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
            KeyFactory kf = KeyFactory.getInstance("RSA");
            PrivateKey privKey = kf.generatePrivate(keySpec);
            Date dateExp = new Date(System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60));
            return Jwts.builder()
                    .claim("scope", "lpb/ekycms/read")
                    .setIssuedAt(new Date())
                    .setExpiration(dateExp)
                    .signWith(SignatureAlgorithm.RS256,privKey)
                    .compact();
        } catch (Exception e) {
            log.error("getPrivateKey : get token fail");
            return null;
        }


    }
}
