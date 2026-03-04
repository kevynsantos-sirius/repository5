package com.totaldocs.controle;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.totaldocs.dto.Usuario;
import com.totaldocs.exception.UserNotExists;
import com.totaldocs.service.UsuarioService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/admin/users")
public class UsuarioControle extends AbstractController {

    private final UsuarioService usuarioService;

    public UsuarioControle(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    private void verificarAdmin(HttpSession session) {

        Boolean isAdmin = (Boolean) session.getAttribute(AbstractController.USER_IS_ADMIN);

        if (isAdmin == null || !isAdmin) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado");
        }
    }

    // ✅ LISTAR
    @GetMapping
    public List<Usuario> listarUsuarios(HttpSession session) {
        verificarAdmin(session);
        return usuarioService.listarTodos();
    }

//    // ✅ CRIAR
//    @PostMapping
//    public ResponseEntity<?> criarUsuario(@RequestBody Usuario usuario, HttpSession session) {
//        verificarAdmin(session);
//        try
//        {
//        	Usuario u = usuarioService.criarUsuario(usuario);
//        	return ResponseEntity.ok(u);
//        }
//        catch(Exception e)
//        {
//        	return ResponseEntity
//                    .badRequest()
//                    .body(e.getMessage()); // 400
//        }
//    }
//
//    // ✅ ATUALIZAR
//    @PutMapping("/{id}")
//    public ResponseEntity<?> atualizarUsuario(
//            @PathVariable String id,
//            @RequestBody Usuario usuario, HttpSession session) throws UserNotExists {
//
//        verificarAdmin(session);
//        try
//        {
//        	Usuario u = usuarioService.atualizarUsuario(id, usuario);
//        	return ResponseEntity.ok(u);
//        }
//        catch(UserNotExists e)
//        {
//        	return ResponseEntity.notFound().build(); // 404
//        }
//    }
//
//    // ✅ DELETAR
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deletarUsuario(@PathVariable String id, HttpSession session) {
//        verificarAdmin(session);
//        try
//        {
//        	usuarioService.deletarUsuario(id);
//        	return ResponseEntity.noContent().build(); // 204
//        }
//        catch(UserNotExists e)
//        {
//        	return ResponseEntity.notFound().build(); // 404
//        }
//    }
}