package com.totaldocs.service;

import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.ChecklistVersaoResumoDTO;
import com.totaldocs.dto.LayoutDTO;
import com.totaldocs.dto.MassaDTO;
import com.totaldocs.dto.UsuarioDTO;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.ChecklistVersao;
import com.totaldocs.modelo.Layout;
import com.totaldocs.modelo.MassaDados;
import com.totaldocs.modelo.Ramo;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.ChecklistRepository;
import com.totaldocs.repository.ChecklistVersaoRepository;
import com.totaldocs.repository.LayoutRepository;
import com.totaldocs.repository.MassaDadoRepository;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Service
public class ChecklistVersaoServiceAPI {
	private final ChecklistVersaoRepository checklistVersaoRepository;
	private final ChecklistRepository checklistRepository;
	private final LayoutRepository layoutRepository;
	private final MassaDadoRepository arquivoRepository;
	private final UsuarioService usuarioService;

	public ChecklistVersaoServiceAPI(ChecklistVersaoRepository checklistVersaoRepository,
			ChecklistRepository checklistRepository, LayoutRepository layoutRepository,
			MassaDadoRepository arquivoRepository, UsuarioService usuarioService) {
		this.checklistVersaoRepository = checklistVersaoRepository;
		this.checklistRepository = checklistRepository;
		this.layoutRepository = layoutRepository;
		this.arquivoRepository = arquivoRepository;
		this.usuarioService = usuarioService;
	}

	@Transactional(rollbackFor = Exception.class)
	public ChecklistVersaoDTO criar(ChecklistVersaoDTO dto, List<MultipartFile> filesLayout,
			List<MultipartFile> filesMassas) throws IOException {

		// ===============================
		// IDENTIFICAÇÃO DO DOCUMENTO
		// ===============================
		Checklist checklist = new Checklist();
		checklist.setNomeDocumento(dto.getNomeDocumento());
		checklist.setCentroCusto(dto.getCentroCusto());

		// Ramo
		Ramo ramo = new Ramo();
		ramo.setIdRamo(dto.getIdRamo());
		checklist.setRamo(ramo);

		checklist = checklistRepository.save(checklist);

		ChecklistVersao checklistVersao = new ChecklistVersao();
		checklistVersao.setChecklist(checklist);
		checklistVersao.setStatus(dto.getStatus());
		checklistVersao.setIcatu(dto.isIcatu());
		checklistVersao.setCaixa(dto.isCaixa());
		checklistVersao.setRioGrande(dto.isRioGrande());
		checklistVersao.setIdDemanda(dto.getIdDemanda());
		checklistVersao.setDataCadastro(LocalDateTime.now());
		checklistVersao.setDataAtualizacao(LocalDateTime.now());

		// Usuário
		Usuario usuario = new Usuario();
		usuario.setId(dto.getIdUsuario());
		checklistVersao.setUsuario(usuario);

		// ===============================
		// SALVAR CHECKLIST PRIMEIRO
		// ===============================
		Integer versao = checklistVersaoRepository.findMaxVersaoByChecklistId(checklistVersao.getIdChecklistVersao())
				.orElse(0);
		checklistVersao.setVersao(versao + 1);
		checklistVersao = checklistVersaoRepository.save(checklistVersao);

		// ===============================
		// TI → LAYOUTS
		// ===============================
		List<Layout> listaLayouts = new ArrayList<>();
		int fileLayoutIndex = 0;
		int fileMassaIndex = 0;

		if (dto.getLayouts() != null) {

			for (LayoutDTO layoutDTO : dto.getLayouts()) {

				Layout layout = new Layout();
				layout.setChecklistVersao(checklistVersao);
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
						}

						massa = arquivoRepository.save(massa);
						listaMassas.add(massa);
					}
				}

				layout.setMassasDados(listaMassas);
				listaLayouts.add(layout);
			}
		}

		checklistVersao.setLayouts(listaLayouts);

		return dto;
	}
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;

	@Transactional(rollbackFor = Exception.class)
	public ChecklistVersaoDTO salvarVersao(String idChecklistVersao, ChecklistVersaoDTO dto,
			List<MultipartFile> filesLayout, List<MultipartFile> filesMassas) throws IOException {

		// =========================
		// VERSÃO ATUAL
		// =========================
		Integer idCheckList = temporalCryptoIdUtil.extractId(idChecklistVersao);
		ChecklistVersao versaoAtual = checklistVersaoRepository.findById(idCheckList)
				.orElseThrow(() -> new IllegalStateException("Versão não encontrada"));

		// =========================
		// NOVA VERSÃO
		// =========================
		ChecklistVersao checklistVersaoNova = new ChecklistVersao();
		checklistVersaoNova.setChecklist(versaoAtual.getChecklist());
		checklistVersaoNova.setStatus(dto.getStatus());
		checklistVersaoNova.setIcatu(dto.isIcatu());
		checklistVersaoNova.setCaixa(dto.isCaixa());
		checklistVersaoNova.setRioGrande(dto.isRioGrande());
		checklistVersaoNova.setVersao(versaoAtual.getVersao() + 1);
		checklistVersaoNova.setDataCadastro(versaoAtual.getDataCadastro());
		checklistVersaoNova.setDataAtualizacao(LocalDateTime.now());
		checklistVersaoNova.setIdDemanda(dto.getIdDemanda() != null ? dto.getIdDemanda() : versaoAtual.getIdDemanda());

		Usuario usuario = new Usuario();
		usuario.setId(dto.getIdUsuario());
		checklistVersaoNova.setUsuario(usuario);

		checklistVersaoNova = checklistVersaoRepository.save(checklistVersaoNova);

		// =========================
		// ITERATORS DE ARQUIVOS
		// =========================
		Iterator<MultipartFile> itLayouts = filesLayout != null ? filesLayout.iterator() : Collections.emptyIterator();

		Iterator<MultipartFile> itMassas = filesMassas != null ? filesMassas.iterator() : Collections.emptyIterator();

		List<Layout> layoutsNovos = new ArrayList<>();

		// =========================
		// LAYOUTS
		// =========================
		if (dto.getLayouts() != null) {
			Layout layoutNovo;

			for (LayoutDTO dtoLayout : dto.getLayouts()) {
				layoutNovo = new Layout();

				// -------- LAYOUT EXISTENTE --------
				String token = dtoLayout.getId();
				if (!Strings.isBlank(token)) {
					
					Integer layoutId = temporalCryptoIdUtil.extractId(token);

					Layout layoutOrigem = layoutRepository.findById(layoutId)
							.orElseThrow(() -> new IllegalStateException("Layout não encontrado"));

//	                

					layoutNovo.setChecklistVersao(checklistVersaoNova);
					layoutNovo.setNomeLayout(layoutOrigem.getNomeLayout());
					layoutNovo.setTipoMIME(layoutOrigem.getTipoMIME());
					layoutNovo.setObservacao(dtoLayout.getObservacao());
					layoutNovo.setConteudoLayout(layoutOrigem.getConteudoLayout());
					layoutNovo.setDataAtualizacao(LocalDateTime.now());

					if (dtoLayout.isTemArquivo()) {
						if (!itLayouts.hasNext()) {
							throw new IllegalStateException("Arquivo de layout esperado e não enviado");
						}

						MultipartFile fl = itLayouts.next();
						layoutNovo.setNomeLayout(fl.getOriginalFilename());
						layoutNovo.setTipoMIME(fl.getContentType());
						layoutNovo.setConteudoLayout(fl.getBytes());
					}

				}
				// -------- LAYOUT NOVO --------
				else {

					if (!dtoLayout.isTemArquivo()) {
						throw new IllegalStateException("Layout novo exige arquivo");
					}

					if (!itLayouts.hasNext()) {
						throw new IllegalStateException("Arquivo de layout não enviado");
					}

					MultipartFile fl = itLayouts.next();

					layoutNovo = new Layout();
					layoutNovo.setChecklistVersao(checklistVersaoNova);
					layoutNovo.setObservacao(dtoLayout.getObservacao());
					layoutNovo.setDataAtualizacao(LocalDateTime.now());
					layoutNovo.setNomeLayout(fl.getOriginalFilename());
					layoutNovo.setTipoMIME(fl.getContentType());
					layoutNovo.setConteudoLayout(fl.getBytes());
				}

				layoutNovo = layoutRepository.save(layoutNovo);
				layoutsNovos.add(layoutNovo);

				// =========================
				// MASSAS DO LAYOUT
				// =========================
				if (dtoLayout.getMassasDados() != null) {

					List<MassaDados> massas = new ArrayList<>();

					for (MassaDTO dtoMassa : dtoLayout.getMassasDados()) {

						MassaDados massa;

						// ---- MASSA EXISTENTE ----
						boolean exitsRegistry = dtoMassa.getId() > 0;
						if (exitsRegistry) {

							massa = arquivoRepository.findById(dtoMassa.getId())
									.orElseThrow(() -> new IllegalStateException("Massa não encontrada"));

							massa.setLayout(layoutNovo);
							massa.setObservacao(dtoMassa.getObservacao());
							massa.setDataAtualizacao(LocalDateTime.now());

							if (dtoMassa.isTemArquivo()) {
								if (!itMassas.hasNext()) {
									throw new IllegalStateException("Arquivo de massa esperado");
								}

								MultipartFile fm = itMassas.next();
								massa.setNomeMassaDados(fm.getOriginalFilename());
								massa.setTipoMIME(fm.getContentType());
								massa.setConteudoMassaDados(fm.getBytes());
							}

						}
						// ---- MASSA NOVA ----
						else {
							if (exitsRegistry) {
								dtoMassa.setTemArquivo(true);
							}
							if (!dtoMassa.isTemArquivo()) {
								throw new IllegalStateException("Massa nova exige arquivo");
							}

							if (!itMassas.hasNext()) {
								throw new IllegalStateException("Arquivo de massa não enviado");
							}

							MultipartFile fm = itMassas.next();

							massa = new MassaDados();
							massa.setLayout(layoutNovo);
							massa.setObservacao(dtoMassa.getObservacao());
							massa.setDataAtualizacao(LocalDateTime.now());
							massa.setNomeMassaDados(fm.getOriginalFilename());
							massa.setTipoMIME(fm.getContentType());
							massa.setConteudoMassaDados(fm.getBytes());
						}

						arquivoRepository.save(massa);
						massas.add(massa);
					}

					// apenas para navegação em memória / retorno
					layoutNovo.setMassasDados(massas);
				}
			}
		}

		checklistVersaoNova.setLayouts(layoutsNovos);
		return dto;
	}

	public Page<ChecklistVersaoDTO> listarPaginadoDTO(Pageable pageable) {
		return checklistVersaoRepository.findUltimasVersoes(pageable).map(c -> {
			ChecklistVersaoDTO dto = new ChecklistVersaoDTO();
			String uuidGenerateTokenVersion = temporalCryptoIdUtil.generateToken(c.getIdChecklistVersao());
			String uuidGenerateToken = temporalCryptoIdUtil.generateToken(c.getChecklist().getId());
			dto.setIdChecklist(uuidGenerateToken); // ID DO CHECKLIST
			dto.setIdChecklistVersao(uuidGenerateTokenVersion);
			dto.setNomeDocumento(c.getChecklist().getNomeDocumento());
			dto.setIdRamo(c.getChecklist().getRamo().getIdRamo());
			dto.setNomeRamo(c.getChecklist().getRamo().getNomeRamo());
			dto.setCentroCusto(c.getChecklist().getCentroCusto());
			dto.setStatus(c.getStatus());
			dto.setIdDemanda(c.getIdDemanda());

			UsuarioDTO usuarioDTO = new UsuarioDTO();
			Usuario user = c.getUsuario();
			usuarioDTO.setId(user.getId());
			usuarioDTO.setNomeUsuario(user.getNome());

			dto.setUsuario(usuarioDTO);

			// Flags
			dto.setIcatu(c.isIcatu());
			dto.setCaixa(c.isCaixa());
			dto.setRioGrande(c.isRioGrande());

			return dto;
		});
	}

	public List<ChecklistVersaoResumoDTO> listarVersoesChecklist(Integer idChecklist) {

		List<ChecklistVersao> versoes = checklistVersaoRepository.findByChecklistIdOrderByVersaoDesc(idChecklist);

		Integer versaoAtual = versoes.isEmpty() ? null : versoes.get(0).getVersao();

		return versoes.stream().map(v -> {

			ChecklistVersaoResumoDTO dto = new ChecklistVersaoResumoDTO();
			dto.setIdChecklistVersao(v.getIdChecklistVersao());
			dto.setIdDemanda(v.getIdDemanda());
			dto.setVersao(v.getVersao());
			dto.setDataCadastro(v.getDataCadastro());
			dto.setNomeUsuario(v.getUsuario().getNome());
			dto.setStatus(v.getStatus());
			dto.setAtual(v.getVersao() == versaoAtual);

			return dto;
		}).toList();
	}

	public Checklist getDocumentoById(Integer id) {
		Checklist checkList = new Checklist();

		checkList = checklistRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Documento não localizado id " + id));

		return checkList;
	}

	@Transactional(readOnly = true)
	public ChecklistVersaoDTO getChecklistVersaoDTOById(Integer idChecklistVersao) {

		ChecklistVersao c = checklistVersaoRepository.findById(idChecklistVersao)
				.orElseThrow(() -> new RuntimeException("Documento não localizado id " + idChecklistVersao));

		ChecklistVersaoDTO dto = new ChecklistVersaoDTO();

		// -------- Identificação --------
		// Checklist
		String uuidGenerateTokenVersion = temporalCryptoIdUtil.generateToken(c.getIdChecklistVersao());
		String uuidGenerateToken = temporalCryptoIdUtil.generateToken(c.getChecklist().getId());
		dto.setIdChecklist(uuidGenerateToken);
		dto.setNomeDocumento(c.getChecklist().getNomeDocumento());
		dto.setCentroCusto(c.getChecklist().getCentroCusto());
		dto.setIdRamo(c.getChecklist().getRamo().getIdRamo());

		// ChecklistVersao
		dto.setIdChecklistVersao(uuidGenerateTokenVersion);
		dto.setStatus(c.getStatus());
		dto.setIcatu(c.isIcatu());
		dto.setCaixa(c.isCaixa());
		dto.setRioGrande(c.isRioGrande());
		dto.setIdUsuario(c.getUsuario().getId());
		dto.setIdDemanda(c.getIdDemanda());

		// (Opcional) preencher usuarioDTO se você usa:
		UsuarioDTO usuarioDTO = new UsuarioDTO();
		usuarioDTO.setId(c.getUsuario().getId());
		usuarioDTO.setNomeUsuario(c.getUsuario().getNome());
		dto.setUsuario(usuarioDTO);

		// -------- Layouts + Massas --------
		List<LayoutDTO> layoutDtos = new ArrayList<>();

		boolean temLayout = c.getLayouts() != null && !c.getLayouts().isEmpty();
		boolean viaServico = false;
		boolean viaTxt = false;

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
				String token = temporalCryptoIdUtil.generateToken(layout.getId());
				layoutDTO.setId(token);
				layoutDTO.setNomeLayout(layout.getNomeLayout());
				layoutDTO.setObservacao(layout.getObservacao());

				List<MassaDTO> massaDtos = new ArrayList<>();
				if (layout.getMassasDados() != null) {
					for (MassaDados massa : layout.getMassasDados()) {
						MassaDTO massaDTO = new MassaDTO();
						massaDTO.setId(massa.getId());
						massaDTO.setNomeMassaDados(massa.getNomeMassaDados());
						massaDTO.setObservacao(massa.getObservacao());
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