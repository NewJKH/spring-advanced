package org.example.expert.config.logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
@RequiredArgsConstructor
public class LoggerAOP {
    private final HttpServletRequest request;
    private final ObjectMapper objectMapper;

    @Around(
            "execution(* org.example.expert.domain.comment.controller.CommentAdminController.*(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.*(..))"
    )
    public Object logRequest(ProceedingJoinPoint joinPoint) throws Throwable {
        Object result = joinPoint.proceed();
        String uri = request.getRequestURI();
        String query = request.getQueryString();
        Object[] args = joinPoint.getArgs();
        String fullUrl = uri + (query != null ? "?" + query : "");
        LocalDateTime requestTime = LocalDateTime.now();

        Claims claims = (Claims) request.getAttribute("claims");
        long userId = Long.parseLong(claims.getSubject());

        log.info("AOP 요청 ID {}",userId);
        log.info("AOP 요청 URL: {}", fullUrl);
        for (Object arg : args) {
            log.info("파라미터: {}", objectMapper.writeValueAsString(arg));
        }

        log.info("AOP 응답 결과: {}", objectMapper.writeValueAsString(result));
        log.info("AOP 요청 시각: {}", requestTime);
        return result;
    }
}
