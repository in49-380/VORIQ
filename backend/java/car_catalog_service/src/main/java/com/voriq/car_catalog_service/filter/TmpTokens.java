package com.voriq.car_catalog_service.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class TmpTokens {

    @Value("${tmp-token.1}")
    private  String token1;

    @Value("${tmp-token.2}")
    private  String token2;

    @Value("${tmp-token.3}")
    private  String token3;

    public  Map<String, String> getTokens() {
        return Map.of(
                token1, "Joe Biden",
                token2, "Donald Trump",
                token3, "Barack Obama"
        );
    }
}
