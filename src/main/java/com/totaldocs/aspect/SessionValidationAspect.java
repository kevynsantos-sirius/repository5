package com.totaldocs.aspect;

import com.totaldocs.annotation.CheckSession;
import com.totaldocs.exception.SessaoExpiradaException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
public class SessionValidationAspect {

    @Before("@annotation(checkSession)")
    public void validateSession(CheckSession checkSession) {

        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attributes == null) {
            throw new SessaoExpiradaException();
        }

        HttpServletRequest request = attributes.getRequest();
        HttpSession session = request.getSession(false);

        if (session == null) {
            throw new SessaoExpiradaException();
        }

        Object contextObj = session.getAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );

        if (contextObj == null) {
            throw new SessaoExpiradaException();
        }

        SecurityContext context = (SecurityContext) contextObj;
        Authentication auth = context.getAuthentication();

        if (auth == null
                || !auth.isAuthenticated()
                || auth instanceof AnonymousAuthenticationToken) {

            throw new SessaoExpiradaException();
        }
    }
}