package com.totaldocs.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.totaldocs.modelo.LogoCapa;
import com.totaldocs.repository.LogoCapaRepository;

@Service
public class LogoCapaService {
	private final LogoCapaRepository logoCapaRepository;
	
	public LogoCapaService(LogoCapaRepository logoCapaRepository) {
		this.logoCapaRepository = logoCapaRepository;
	}
	
	public List<LogoCapa> ListarTodos(){
		return logoCapaRepository.findAll();
	}
}
