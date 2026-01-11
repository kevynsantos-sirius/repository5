package com.totaldocs.service;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.PageResponse;
import com.totaldocs.modelo.Checklist;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChecklistService {
	@Value("${backend.api.base-url}" + "Checklists")
	private String API_URL;
    
    public Checklist salvarChecklist(Checklist checklist) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            LocalDateTime dataSistema = LocalDateTime.now();
            checklist.setDataAtualizacao(dataSistema);
            checklist.setDataCadastro(dataSistema);
            
            return restTemplate.postForObject(API_URL, checklist, Checklist.class);
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
