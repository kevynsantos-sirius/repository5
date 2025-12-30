package com.totaldocs.controle;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.totaldocs.modelo.Ramo;
import com.totaldocs.service.RamoService;

@RestController
@RequestMapping("/api/ramo")
public class RamoControle {
	private final RamoService ramoService;

    public RamoControle(RamoService ramoService) {
        this.ramoService = ramoService;
    }

    @GetMapping
    public List<Ramo> listarTodos() {
        return ramoService.ListarTodos();
    }

}
