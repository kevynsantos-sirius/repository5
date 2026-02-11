package com.totaldocs.controle;

import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public abstract class AbstractController {

    /**
     * Verifica se há usuário autenticado na sessão HTTP.
     * Se não houver, lança RuntimeException de sessão expirada.
     *
     * @param session HttpSession do request
     */
    protected void getUserFromSession(HttpSession session) {
        if (session == null) {
            throw new RuntimeException("Sessão expirada");
        }

        // Pega o SecurityContext salvo na sessão pelo Spring Security
        Object contextObj = session.getAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );

        if (contextObj == null) {
            throw new RuntimeException("Sessão expirada");
        }

        // Faz cast seguro para Authentication
        Authentication auth = ((org.springframework.security.core.context.SecurityContext) contextObj).getAuthentication();

        if (auth == null || !auth.isAuthenticated() || isAnonymous(auth)) {
            throw new RuntimeException("Sessão expirada");
        }
    }

    private boolean isAnonymous(Authentication auth) {
        Object principal = auth.getPrincipal();
        return principal == null || principal.equals("anonymousUser");
    }
}
