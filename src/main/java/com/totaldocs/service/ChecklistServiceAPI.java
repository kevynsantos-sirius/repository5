package com.totaldocs.service;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.LayoutDTO;
import com.totaldocs.dto.MassaDTO;
import com.totaldocs.dto.UsuarioDTO;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.Layout;
import com.totaldocs.modelo.MassaDados;
import com.totaldocs.modelo.Ramo;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.ChecklistRepository;
import com.totaldocs.repository.LayoutRepository;
import com.totaldocs.repository.MassaDadoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChecklistServiceAPI {
	private final ChecklistRepository checklistRepository;
	private final LayoutRepository layoutRepository;
	private final MassaDadoRepository arquivoRepository;
	private final UsuarioService usuarioService;

	public ChecklistServiceAPI(ChecklistRepository checklistRepository, LayoutRepository layoutRepository,
			MassaDadoRepository arquivoRepository, UsuarioService usuarioService) {
		this.checklistRepository = checklistRepository;
		this.layoutRepository = layoutRepository;
		this.arquivoRepository = arquivoRepository;
		this.usuarioService = usuarioService;
	}

	/**
	 * Salva o documento e realiza o upload dos arquivos no mesmo POST.
	 */
	@Transactional(rollbackFor = Exception.class)
	public ChecklistDTO criar(ChecklistDTO dto, List<MultipartFile> filesLayout, List<MultipartFile> filesMassas)
			throws IOException {

		Checklist checklist = new Checklist();

		// ===============================
		// IDENTIFICAÇÃO DO DOCUMENTO
		// ===============================

		checklist.setNomeDocumento(dto.getNomeDocumento());
		checklist.setCentroCusto(dto.getCentroCusto());
		checklist.setStatus(dto.getStatus());
		checklist.setIcatu(dto.isIcatu());
		checklist.setCaixa(dto.isCaixa());
		checklist.setRioGrande(dto.isRioGrande());
		checklist.setDataCadastro(LocalDateTime.now());
		checklist.setDataAtualizacao(LocalDateTime.now());

		// Ramo
		Ramo ramo = new Ramo();
		ramo.setIdRamo(dto.getIdRamo());
		checklist.setRamo(ramo);

		// Usuário
		Usuario usuario = new Usuario();
		usuario.setId(dto.getIdUsuario());
		checklist.setUsuario(usuario);

		// ===============================
		// SALVAR CHECKLIST PRIMEIRO
		// ===============================
		checklist = checklistRepository.save(checklist);

		// ===============================
		// TI → LAYOUTS
		// ===============================
		List<Layout> listaLayouts = new ArrayList<>();
		int fileLayoutIndex = 0;
		int fileMassaIndex = 0;

		if (dto.getLayouts() != null) {

			for (LayoutDTO layoutDTO : dto.getLayouts()) {

				Layout layout = new Layout();
				layout.setChecklist(checklist);
				layout.setObservacao(layoutDTO.getObservacao());
				layout.setDataAtualizacao(LocalDateTime.now());
				layout.setViaServico(dto.isViaServico());
				layout.setViaTxt(dto.isViaTxt());

				// arquivo do layout
				if (filesLayout != null && fileLayoutIndex < filesLayout.size()) {
					MultipartFile fl = filesLayout.get(fileLayoutIndex++);

					layout.setTipoMIME(fl.getContentType());
					layout.setConteudoLayout(fl.getBytes());
					layout.setNomeLayout(fl.getOriginalFilename());
				}

				layout = layoutRepository.save(layout);

				// ===============================
				// MASSAS DO LAYOUT
				// ===============================
				List<MassaDados> listaMassas = new ArrayList<>();

				if (layoutDTO.getMassasDados() != null) {
					for (MassaDTO massaDTO : layoutDTO.getMassasDados()) {

						MassaDados massa = new MassaDados();
						massa.setLayout(layout);
						massa.setObservacao(massaDTO.getObservacao());
						massa.setDataAtualizacao(LocalDateTime.now());

						if (filesMassas != null && fileMassaIndex < filesMassas.size()) {
							MultipartFile fm = filesMassas.get(fileMassaIndex++);
							massa.setNomeMassaDados(fm.getOriginalFilename());
							massa.setTipoMIME(fm.getContentType());
							massa.setConteudoMassaDados(fm.getBytes());
//                            massa.setViaServico(layoutDTO.isViaServico());
//                            massa.setViaTxt(layoutDTO.isViaTxt());
						}

						massa = arquivoRepository.save(massa);
						listaMassas.add(massa);
					}
				}

				layout.setMassasDados(listaMassas);
				listaLayouts.add(layout);
			}
		}

		checklist.setLayouts(listaLayouts);

		return dto;
	}

	public Page<ChecklistDTO> listarPaginadoDTO(Pageable pageable) {

		return checklistRepository.findAll(pageable).map(c -> {

			ChecklistDTO dto = new ChecklistDTO();
			dto.setId(c.getId());
			dto.setNomeDocumento(c.getNomeDocumento());
			dto.setIdRamo(c.getRamo().getIdRamo());
			dto.setNomeRamo(c.getRamo().getNomeRamo());
			dto.setCentroCusto(c.getCentroCusto());
			dto.setStatus(c.getStatus());

			dto.setIdUsuario(c.getUsuario().getId());

			UsuarioDTO usuarioDTO = new UsuarioDTO();
			Optional<Usuario> usuarioEntity = usuarioService.getUsuario(1);

			if (usuarioEntity.isPresent()) {
				Usuario user = usuarioEntity.get();
				usuarioDTO.setId(user.getId());
				usuarioDTO.setNomeUsuario(user.getNome());
			}

			dto.setUsuario(usuarioDTO);

			// Flags
			dto.setIcatu(c.isIcatu());
			dto.setCaixa(c.isCaixa());
			dto.setRioGrande(c.isRioGrande());

			/*
			 * dto.setTemLayout(c.isTemLayout()); dto.setViaServico(c.isViaServico());
			 * dto.setViaTxt(c.isViaTxt());
			 */

			// NÃO carregamos layouts/massas aqui (para evitar lentidão)

			return dto;
		});
	}

	public Checklist getDocumentoById(Integer id) {
		Checklist checkList = new Checklist();

		checkList = checklistRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Documento não localizado id " + id));

//    	ArquivoLayout arquivoLayout = new ArquivoLayout();
//    	arquivoLayout = arquivoService.getArquivoById(id);

		return checkList;
	}

	
	@Transactional(readOnly = true)
	public ChecklistDTO getChecklistDTOById(Integer id) {

	    Checklist c = checklistRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Documento não localizado id " + id));

	    ChecklistDTO dto = new ChecklistDTO();

	    // -------- Identificação --------
	    dto.setId(c.getId());
	    dto.setNomeDocumento(c.getNomeDocumento());
	    dto.setCentroCusto(c.getCentroCusto());
	    dto.setStatus(c.getStatus());
	    dto.setIcatu(c.isIcatu());
	    dto.setCaixa(c.isCaixa());
	    dto.setRioGrande(c.isRioGrande());
	    dto.setIdRamo(c.getRamo().getIdRamo());
	    dto.setIdUsuario(c.getUsuario().getId());

	    // (Opcional) preencher usuarioDTO se você usa:
	    UsuarioDTO usuarioDTO = new UsuarioDTO();
	    usuarioDTO.setId(c.getUsuario().getId());
	    usuarioDTO.setNomeUsuario(c.getUsuario().getNome());
	    dto.setUsuario(usuarioDTO);

	    // -------- Layouts + Massas --------
	    List<LayoutDTO> layoutDtos = new ArrayList<>();

	    boolean temLayout   = c.getLayouts() != null && !c.getLayouts().isEmpty();
	    boolean viaServico  = false;
	    boolean viaTxt      = false;

	    if (c.getLayouts() != null) {
	        for (Layout layout : c.getLayouts()) {

	            // acumula flags globais para o DTO principal
	            if (layout.isViaServico()) {
	                viaServico = true;
	            }
	            if (layout.isViaTxt()) {
	                viaTxt = true;
	            }

	            LayoutDTO layoutDTO = new LayoutDTO();
	            layoutDTO.setNomeLayout(layout.getNomeLayout());
	            layoutDTO.setObservacao(layout.getObservacao());

	            List<MassaDTO> massaDtos = new ArrayList<>();
	            if (layout.getMassasDados() != null) {
	                for (MassaDados massa : layout.getMassasDados()) {
	                    MassaDTO massaDTO = new MassaDTO();
	                    massaDTO.setNomeMassaDados(massa.getNomeMassaDados());
	                    massaDTO.setObservacao(massa.getObservacao());

	                    // Se quiser ainda usar viaServico/viaTxt em MassaDTO,
	                    // pode espelhar o valor do Layout:
						/*
						 * massaDTO.setViaServico(layout.isViaServico());
						 * massaDTO.setViaTxt(layout.isViaTxt());
						 */

	                    massaDtos.add(massaDTO);
	                }
	            }

	            layoutDTO.setMassasDados(massaDtos);
	            layoutDtos.add(layoutDTO);
	        }
	    }

	    dto.setTemLayout(temLayout);
	    dto.setViaServico(viaServico);
	    dto.setViaTxt(viaTxt);
	    dto.setLayouts(layoutDtos);

	    return dto;
	}
}