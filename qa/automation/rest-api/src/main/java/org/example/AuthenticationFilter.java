package org.example;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;

public class AuthenticationFilter implements Filter {

    private final String authToken;

    public AuthenticationFilter(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx
    ) {
        requestSpec.header("Authorization", "Bearer " + authToken);
        return ctx.next(requestSpec, responseSpec);
    }
}

