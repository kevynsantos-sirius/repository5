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
import java.time.format.DateTimeFormatter;
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

        	String rootFolder = nameFolderOrZip(checklist,false);

            String layoutsRoot = rootFolder + "layouts/";

            // Cria pasta layouts/
            zos.putNextEntry(new ZipEntry(layoutsRoot));
            zos.closeEntry();

            if (checklist.getLayouts() != null) {

                for (LayoutDTO layout : checklist.getLayouts()) {

                    boolean temMassas = layout.getMassasDados() != null
                            && !layout.getMassasDados().isEmpty();

                    String layoutNome = sanitize(layout.getNomeLayout());

                    Layout layoutEntity = buscarArquivoLayout(layout.getId());
                    byte[] conteudoLayout = layoutEntity.getConteudoLayout();

                    // 🔹 Layout SEM massas → arquivo direto em layouts/
                    if (!temMassas) {

                        if (conteudoLayout != null && conteudoLayout.length > 0) {

                            String nomeArquivo = sanitize(layoutEntity.getNomeLayout());

                            addBytesToZip(
                                    zos,
                                    conteudoLayout,
                                    layoutsRoot + nomeArquivo
                            );
                        }
                    }

                    // 🔹 Layout COM massas → vira pasta
                    if (temMassas) {

                        String layoutFolder = layoutsRoot + layoutNome + "/";
                        zos.putNextEntry(new ZipEntry(layoutFolder));
                        zos.closeEntry();

                        // Arquivo do layout dentro da pasta
                        if (conteudoLayout != null && conteudoLayout.length > 0) {

                            String nomeArquivo = sanitize(layoutEntity.getNomeLayout());

                            addBytesToZip(
                                    zos,
                                    conteudoLayout,
                                    layoutFolder + nomeArquivo
                            );
                        }

                        // Pasta massas/
                        String massasFolder = layoutFolder + "massas/";
                        zos.putNextEntry(new ZipEntry(massasFolder));
                        zos.closeEntry();

                        // Arquivos das massas (SEM criar pasta com nome do arquivo)
                        for (MassaDTO massa : layout.getMassasDados()) {

                            MassaDados massaEntity = buscarArquivoMassa(massa.getId());
                            byte[] conteudoMassa = massaEntity.getConteudoMassaDados();

                            if (conteudoMassa != null && conteudoMassa.length > 0) {

                                String nomeArquivo = sanitize(massaEntity.getNomeMassaDados());

                                addBytesToZip(
                                        zos,
                                        conteudoMassa,
                                        massasFolder + nomeArquivo
                                );
                            }
                        }
                    }
                }
            }

            zos.finish();
            return baos.toByteArray();
        }
    }


	public String nameFolderOrZip(ChecklistVersaoDTO checklist, boolean isZip) {
		LocalDateTime agora = LocalDateTime.now();

		// Formato: ano-mes-dia_hora-minuto-segundo
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");

		String dataHoraFormatada = agora.format(formatter);
		
		String folderOrNot = isZip ? "" : "/";

		String rootFolder = "checklist-"
		        + sanitize(checklist.getNomeDocumento())
		        + "-"
		        + dataHoraFormatada
		        + folderOrNot;
		return rootFolder;
	}
    
    private void addBytesToZip(ZipOutputStream zos, byte[] fileBytes, String zipPath)
            throws IOException {

        zos.putNextEntry(new ZipEntry(zipPath));
        zos.write(fileBytes);
        zos.closeEntry();
    }
}
