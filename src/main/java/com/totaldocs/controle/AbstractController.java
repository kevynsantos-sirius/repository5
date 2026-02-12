package com.totaldocs.controle;

import jakarta.servlet.http.HttpSession;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.totaldocs.dto.UsuarioDTO;
import com.totaldocs.dto.UsuarioLogado;
import com.totaldocs.modelo.Usuario;

public abstract class AbstractController {

    /**
     * Verifica se há usuário autenticado na sessão HTTP.
     * Se não houver, lança RuntimeException de sessão expirada.
     *
     * @param session HttpSession do request
     */
	public static String USER_ID = "USER_ID";
	
	protected void setUserIdSession(Usuario usuario, HttpSession session)
	{
		session.setAttribute(USER_ID, usuario.getId());
	}
	
	protected Integer getUserIdSession(HttpSession session)
	{
		Object obj = session.getAttribute(USER_ID);
		return (Integer) obj;
	}
	
    protected void checkExistsSession(HttpSession session) {
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
    
    protected String getUsernameUserLogged(HttpSession session) {

        checkExistsSession(session);

        SecurityContext context = (SecurityContext) session.getAttribute(
            HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY
        );

        Authentication auth = context.getAuthentication();

        return auth.getName();
    }
    
    protected ResponseEntity<UsuarioDTO> getUserLogged(HttpSession session)
    {
        Integer userId = getUserIdSession(session);
        String userName = getUsernameUserLogged(session);

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(userId);
        dto.setNomeUsuario(userName);

        return ResponseEntity.ok(dto);
    }


}
