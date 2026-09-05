package com.example.hotalproject.AI;

import io.modelcontextprotocol.client.transport.HttpClientStreamableHttpTransport;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ai.mcp.customizer.McpClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class McpAuthConfig {

    @Bean
    McpClientCustomizer<HttpClientStreamableHttpTransport.Builder> forwardUserJwtToMcp() {
        return (name, builder) -> builder.httpRequestCustomizer(
                (requestBuilder, method, endpoint, body, context) -> {
                    RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

                    if (!(attributes instanceof ServletRequestAttributes servletAttributes)) {
                        return;
                    }

                    HttpServletRequest request = servletAttributes.getRequest();
                    String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);

                    if (authorization != null && authorization.startsWith("Bearer ")) {
                        requestBuilder.header(HttpHeaders.AUTHORIZATION, authorization);
                    }
                });
    }
}
