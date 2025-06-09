package org.example.expert.config.logger;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoggerInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI();

        if (url.startsWith("/auth")) {
            return true;
        }

        Claims claims = (Claims) request.getAttribute("claims");

        if (claims == null) {
            // 이건 JwtFilter 에서 이미 막았어야 하니 거의 안 뜨긴 할 거임
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT 검증 실패");
            return false;
        }

        UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

        request.setAttribute("userId", Long.parseLong(claims.getSubject()));
        request.setAttribute("email", claims.get("email"));
        request.setAttribute("userRole", claims.get("userRole"));

        if (url.startsWith("/admin")) {
            if (!UserRole.ADMIN.equals(userRole)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "관리자 권한이 없습니다.");
                return false;
            }
        }
        return true;
    }

}
