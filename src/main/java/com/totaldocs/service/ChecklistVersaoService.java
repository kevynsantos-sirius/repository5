package com.totaldocs.service;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.LayoutDTO;
import com.totaldocs.dto.MassaDTO;
import com.totaldocs.dto.PageResponse;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.ChecklistVersao;
import com.totaldocs.modelo.Layout;
import com.totaldocs.modelo.MassaDados;
import com.totaldocs.repository.LayoutRepository;
import com.totaldocs.repository.MassaDadoRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
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
	
	@Autowired
    private LayoutRepository layoutRepository;
	
	@Autowired
	private MassaDadoRepository massaDadoRepository;
    
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
    
    private Layout buscarArquivoLayout(String layoutId) {

        return layoutRepository.findById(Integer.valueOf(layoutId))
                .orElseThrow(() ->
                        new RuntimeException("Layout não encontrado id " + layoutId)
                );
    }

    private MassaDados buscarArquivoMassa(String massaId) {

        return massaDadoRepository.findById(Integer.valueOf(massaId))
                .orElseThrow(() ->
                        new RuntimeException("Massa não encontrada id " + massaId)
                );
    }
    
    private String sanitize(String nome) {
        if (nome == null) return "sem-nome";
        return nome.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }
    
    private String getExtensaoPorMime(String mime) {

        if (mime == null || mime.isBlank()) {
            return ".dat";
        }

        switch (mime.toLowerCase()) {
            case "application/pdf":
                return ".pdf";
            case "text/plain":
                return ".txt";
            case "application/json":
                return ".json";
            case "text/csv":
                return ".csv";
            case "application/xml":
            case "text/xml":
                return ".xml";
            case "application/vnd.ms-excel":
                return ".xls";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                return ".xlsx";
            case "application/zip":
                return ".zip";
            default:
                return ".dat";
        }
    }


    public byte[] generateZipFromCheckList(ChecklistVersaoDTO checklist) throws IOException {

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ZipOutputStream zos = new ZipOutputStream(baos)) {

            String rootFolder = "checklist-" + checklist.getIdChecklistVersao() + "/";
            String layoutsRoot = rootFolder + "layouts/";

            zos.putNextEntry(new ZipEntry(layoutsRoot));
            zos.closeEntry();

            if (checklist.getLayouts() != null) {

                for (LayoutDTO layout : checklist.getLayouts()) {

                    String layoutFolder = layoutsRoot + sanitize(layout.getNomeLayout()) + "/";
                    zos.putNextEntry(new ZipEntry(layoutFolder));
                    zos.closeEntry();

                    // ---------------- ARQUIVO REAL LAYOUT ----------------
                    if (layout.isTemArquivo()) {

                        Layout layoutEntity = buscarArquivoLayout(layout.getId());

                        byte[] conteudo = layoutEntity.getConteudoLayout();

                        String nomeBase = sanitize(layoutEntity.getNomeLayout());
                        String extensao = getExtensaoPorMime(layoutEntity.getTipoMIME());

                        String nomeArquivo = nomeBase.endsWith(extensao)
                                ? nomeBase
                                : nomeBase + extensao;

                        addBytesToZip(zos, conteudo, layoutFolder + nomeArquivo);
                    }

                    // ---------------- MASSAS ----------------
                    if (layout.getMassasDados() != null && !layout.getMassasDados().isEmpty()) {

                        String massasFolder = layoutFolder + "massas/";
                        zos.putNextEntry(new ZipEntry(massasFolder));
                        zos.closeEntry();

                        for (MassaDTO massa : layout.getMassasDados()) {

                            String massaFolder = massasFolder + sanitize(massa.getNomeMassaDados()) + "/";
                            zos.putNextEntry(new ZipEntry(massaFolder));
                            zos.closeEntry();

                            if (massa.isTemArquivo()) {

                                MassaDados massaEntity = buscarArquivoMassa(massa.getId());

                                byte[] conteudo = massaEntity.getConteudoMassaDados();

                                String nomeBase = sanitize(massaEntity.getNomeMassaDados());
                                String extensao = getExtensaoPorMime(massaEntity.getTipoMIME());

                                String nomeArquivo = nomeBase.endsWith(extensao)
                                        ? nomeBase
                                        : nomeBase + extensao;

                                addBytesToZip(zos, conteudo, massaFolder + nomeArquivo);
                            }
                        }
                    }
                }
            }

            zos.finish();
            return baos.toByteArray();
        }
    }
    
    private void addBytesToZip(ZipOutputStream zos, byte[] fileBytes, String zipPath)
            throws IOException {

        zos.putNextEntry(new ZipEntry(zipPath));
        zos.write(fileBytes);
        zos.closeEntry();
    }
}
