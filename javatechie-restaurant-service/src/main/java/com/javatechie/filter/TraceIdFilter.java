package com.javatechie.filter;

import java.io.IOException;
import java.util.Enumeration;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        Enumeration<String> headerNames = httpRequest.getHeaderNames();

        if (headerNames != null) {
            String traceId = httpRequest.getHeader("b3");
            if (traceId != null && !traceId.isBlank()) {
                log.info("TraceId Filter: b3 header propagated: " + traceId);
            }
        }
        String url = getFullURL(httpRequest);
        log.info("TraceId Filter: full url: " + url);

        chain.doFilter(request, response);

    }

    private static String getFullURL(HttpServletRequest request) {
        StringBuilder requestURL = new StringBuilder(request.getRequestURL().toString());
        String queryString = request.getQueryString();

        if (queryString == null) {
            return requestURL.toString();
        } else {
            return requestURL.append('?').append(queryString).toString();
        }
    }

}
