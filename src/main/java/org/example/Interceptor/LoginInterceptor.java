package org.example.Interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.util.Map;

public class LoginInterceptor implements HandlerInterceptor {

    private static final String PATTERN = "^[a-zA-Z0-9]+$";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (!"POST".equalsIgnoreCase(request.getMethod())) return true;

        ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
        byte[] body = wrapper.getContentAsByteArray();
        if (body.length == 0) return true;

        Map<?, ?> map = objectMapper.readValue(body, Map.class);
        String username = (String) map.get("username");
        String password = (String) map.get("password");

        if (username != null && password != null
                && username.matches(PATTERN) && password.matches(PATTERN)) {
            return true;
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(400);
        objectMapper.writeValue(response.getWriter(),
                Map.of("code", 400, "msg", "用户名和密码只能包含大小写字母和数字"));
        return false;
    }
}
