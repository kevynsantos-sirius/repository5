package com.totaldocs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totaldocs.modelo.Ramo;
import com.totaldocs.repository.RamoRepository;

@Service
public class RamoService {
	private final RamoRepository ramoRepository;
	
	public RamoService(RamoRepository ramoRepository) {
		this.ramoRepository = ramoRepository;
	}
	
	public List<Ramo> ListarTodos(){
		return ramoRepository.findAll();
	}
}
