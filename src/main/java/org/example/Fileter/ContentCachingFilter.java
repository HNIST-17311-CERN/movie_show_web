package org.example.Fileter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

@Component
public class ContentCachingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        // 把请求包一层，让 body 可以被多次读取
        ContentCachingRequestWrapper wrapper = new ContentCachingRequestWrapper(
                (HttpServletRequest) request);
        chain.doFilter(wrapper, response);
    }
}
