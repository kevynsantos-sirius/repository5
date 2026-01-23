package com.totaldocs.service;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.PageResponse;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.ChecklistVersao;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChecklistVersaoService {
	@Value("${backend.api.base-url}" + "Checklists")
	private String API_URL;
    
    public Checklist salvarChecklist(ChecklistVersao checklistVersao) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            LocalDateTime dataSistema = LocalDateTime.now();
            checklistVersao.setDataAtualizacao(dataSistema);
            checklistVersao.setDataCadastro(dataSistema);
            
            return restTemplate.postForObject(API_URL, checklistVersao, Checklist.class);
        } catch (Exception e) {
            System.err.println("Erro ao salvar Checklist: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erro ao salvar Checklist", e);
        }
    }
    
    
    public PageResponse<ChecklistDTO> listarPaginado(int page, int size){
    	RestTemplate restTemplate = new RestTemplate();
    	
    	String url = API_URL + "/page?page=" + page + "&size=" + size;    	

    	ResponseEntity<PageResponse<ChecklistDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageResponse<ChecklistDTO>>() {}
                );

        return response.getBody();
    }
}
