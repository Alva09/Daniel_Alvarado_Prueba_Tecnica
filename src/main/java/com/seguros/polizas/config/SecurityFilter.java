package com.seguros.polizas.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro de seguridad mínimo que valida el header x-api-key.
 * Excluye rutas internas como /h2-console y /core-mock.
 */
@Component
@Order(1)
public class SecurityFilter implements Filter {

    @Value("${negocio.api-key}")
    private String apiKey;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();

        // Excluir rutas internas del filtro de seguridad
        if (path.startsWith("/h2-console") || path.startsWith("/core-mock")) {
            chain.doFilter(request, response);
            return;
        }

        String requestApiKey = httpRequest.getHeader("x-api-key");

        if (requestApiKey == null || !requestApiKey.equals(apiKey)) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(
                    "{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"Header x-api-key inválido o ausente\"}"
            );
            return;
        }

        chain.doFilter(request, response);
    }
}
