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
import com.totaldocs.utils.TemporalCryptoIdUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.util.Strings;
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
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;
    
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
    
    private Layout buscarArquivoLayout(String layoutIdStr) {

    	Integer layoutId = temporalCryptoIdUtil.extractId(layoutIdStr);
        return layoutRepository.findById(Integer.valueOf(layoutId))
                .orElseThrow(() ->
                        new RuntimeException("Layout não encontrado id " + layoutId)
                );
    }

    private MassaDados buscarArquivoMassa(String massaIdStr) {
    	
    	Integer massaId = temporalCryptoIdUtil.extractId(massaIdStr);
        return massaDadoRepository.findById(Integer.valueOf(massaId))
                .orElseThrow(() ->
                        new RuntimeException("Massa não encontrada id " + massaId)
                );
    }
    
    private String sanitize(String nome) {
        if (nome == null) return "sem-nome";
        return nome.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
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

                    boolean temMassas = layout.getMassasDados() != null
                            && !layout.getMassasDados().isEmpty();

                    String layoutNome = sanitize(layout.getNomeLayout());

                    // 🔹 Se NÃO tiver massas → arquivo direto em layouts/
                    if (!temMassas && !Strings.isBlank(layout.getNomeLayout())) {

                        Layout layoutEntity = buscarArquivoLayout(layout.getId());

                        byte[] conteudo = layoutEntity.getConteudoLayout();

                        String nomeArquivo = sanitize(layoutEntity.getNomeLayout());

                        addBytesToZip(zos, conteudo, layoutsRoot + nomeArquivo);
                    }

                    // 🔹 Se tiver massas → vira pasta
                    if (temMassas) {

                        String layoutFolder = layoutsRoot + layoutNome + "/";
                        zos.putNextEntry(new ZipEntry(layoutFolder));
                        zos.closeEntry();

                        // Arquivo do layout dentro da pasta
                        if (!Strings.isBlank(layout.getNomeLayout())) {

                            Layout layoutEntity = buscarArquivoLayout(layout.getId());

                            byte[] conteudo = layoutEntity.getConteudoLayout();

                            String nomeArquivo = sanitize(layoutEntity.getNomeLayout());

                            addBytesToZip(zos, conteudo, layoutFolder + nomeArquivo);
                        }

                        // Massas
                        String massasFolder = layoutFolder + "massas/";
                        zos.putNextEntry(new ZipEntry(massasFolder));
                        zos.closeEntry();

                        for (MassaDTO massa : layout.getMassasDados()) {

                            String massaFolder = massasFolder + sanitize(massa.getNomeMassaDados()) + "/";
                            zos.putNextEntry(new ZipEntry(massaFolder));
                            zos.closeEntry();

                            if (!Strings.isBlank(massa.getNomeMassaDados())) {

                                MassaDados massaEntity = buscarArquivoMassa(massa.getId());

                                byte[] conteudo = massaEntity.getConteudoMassaDados();

                                String nomeArquivo = sanitize(massaEntity.getNomeMassaDados());

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
