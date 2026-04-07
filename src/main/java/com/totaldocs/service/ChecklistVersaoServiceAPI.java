package com.totaldocs.service;

import com.totaldocs.dto.CamposBuscaDTO;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.ChecklistVersaoResumoDTO;
import com.totaldocs.dto.ItemArquivoDTO;
import com.totaldocs.dto.LayoutDTO;
import com.totaldocs.dto.MassaDTO;
import com.totaldocs.dto.ModeloDTO;
import com.totaldocs.dto.UsuarioDTO;
import com.totaldocs.enums.LogomodeloTipoCodigo;
import com.totaldocs.exception.UmTipoDeImpressaoException;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.ChecklistVersao;
import com.totaldocs.modelo.Layout;
import com.totaldocs.modelo.Logomodelo;
import com.totaldocs.modelo.LogomodeloTipo;
import com.totaldocs.modelo.MassaDados;
import com.totaldocs.modelo.ModeloDocumento;
import com.totaldocs.modelo.Ramo;
import com.totaldocs.modelo.Usuario;
import com.totaldocs.repository.ChecklistRepository;
import com.totaldocs.repository.ChecklistVersaoRepository;
import com.totaldocs.repository.LayoutRepository;
import com.totaldocs.repository.LogoCapaRepository;
import com.totaldocs.repository.LogomodeloRepository;
import com.totaldocs.repository.LogomodeloTipoRepository;
import com.totaldocs.repository.MassaDadoRepository;
import com.totaldocs.repository.ModeloDocumentoRepository;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ChecklistVersaoServiceAPI {
	private final ChecklistVersaoRepository checklistVersaoRepository;
	private final ChecklistRepository checklistRepository;
	private final LayoutRepository layoutRepository;
	private final MassaDadoRepository arquivoRepository;
	private final UsuarioService usuarioService;
	private final ModeloDocumentoRepository modeloDocumentoRepository;
	private final LogomodeloRepository logomodeloRepository;
	private final LogomodeloTipoRepository logomodeloTipoRepository;

	public ChecklistVersaoServiceAPI(ChecklistVersaoRepository checklistVersaoRepository,
			ChecklistRepository checklistRepository, LayoutRepository layoutRepository,
			MassaDadoRepository arquivoRepository, UsuarioService usuarioService,
			ModeloDocumentoRepository modeloDocumentoRepository,
			LogomodeloRepository logomodeloRepository,
			LogomodeloTipoRepository logomodeloTipoRepository) {
		this.checklistVersaoRepository = checklistVersaoRepository;
		this.checklistRepository = checklistRepository;
		this.layoutRepository = layoutRepository;
		this.arquivoRepository = arquivoRepository;
		this.usuarioService = usuarioService;
		this.modeloDocumentoRepository = modeloDocumentoRepository;
		this.logomodeloRepository =  logomodeloRepository;
		this.logomodeloTipoRepository =  logomodeloTipoRepository;
	}
	
	public void hasChangesForm(String idChecklistVersao, ChecklistVersaoDTO dto,
			List<MultipartFile> filesLayout, List<MultipartFile> filesMassas) throws IllegalStateException
	{
		Integer idCheckList = temporalCryptoIdUtil.extractId(idChecklistVersao);
		ChecklistVersao versaoAtual = checklistVersaoRepository.findById(idCheckList)
				.orElseThrow(() -> new IllegalStateException("Versão não encontrada"));
		
		Checklist checklist = versaoAtual.getChecklist();
		
		//IDENTIFICAÇÃO
		
		boolean centroCustoIsEqual = checklist.getCentroCusto().equals(dto.getCentroCusto());
		boolean icatuIsEqual = versaoAtual.isIcatu() == dto.isIcatu();
		boolean caixaIsEqual = versaoAtual.isCaixa() == dto.isCaixa();
		boolean rioGrandeIsEqual = versaoAtual.isRioGrande() == dto.isRioGrande();
		boolean idDemandaIsEqual = versaoAtual.getIdDemanda().equals(dto.getIdDemanda());
		boolean statusIsEqual = versaoAtual.getStatus().equals(dto.getStatus());
		
		boolean identificationIsEqual = centroCustoIsEqual && icatuIsEqual 
				&& caixaIsEqual && rioGrandeIsEqual && idDemandaIsEqual && statusIsEqual;
		
		if(!identificationIsEqual)
		{
			return;
		}
		
		//LAYOUT E FORMA DE ENVIO - TI
		
		ChecklistVersao lastVersion = versaoAtual;
		
		
		boolean qtdMassasIsDifference = false;
		
		for (LayoutDTO dtoLayout : dto.getLayouts()) {
			String token = dtoLayout.getId();
			boolean nameLayoutIsEqual = false;
			boolean observationLayoutIsEqual = false;
			if (!Strings.isBlank(token) && temporalCryptoIdUtil.extractId(token) != null) {
				
				Integer layoutId = temporalCryptoIdUtil.extractId(token);
				
				if(layoutId != null) {
					
					Layout layoutOrigem = layoutRepository.findById(layoutId)
							.orElseThrow(() -> new IllegalStateException("Layout não encontrado"));
					
					nameLayoutIsEqual = layoutOrigem.getNomeLayout().equals(dtoLayout.getNomeLayout());
					
					observationLayoutIsEqual = layoutOrigem.getObservacao().equals(dtoLayout.getObservacao());
					
					qtdMassasIsDifference = layoutOrigem.getMassasDados().size() != dtoLayout.getMassasDados().size();
					
					if((!nameLayoutIsEqual || !observationLayoutIsEqual) || qtdMassasIsDifference)
					{
						return;
					}
					
					
				}
				
			}
			
			for (MassaDTO dtoMassa : dtoLayout.getMassasDados()) {
				boolean nameMassaIsEqual = false;
				boolean observationMassaIsEqual = false;
				boolean exitsRegistry = !temporalCryptoIdUtil.isUUID(dtoMassa.getId());
				if (exitsRegistry) {
					Integer massaId = temporalCryptoIdUtil.extractId(dtoMassa.getId());
					MassaDados massa = arquivoRepository.findById(massaId)
							.orElseThrow(() -> new IllegalStateException("Massa não encontrada"));
					
					nameMassaIsEqual = massa.getNomeMassaDados().equals(dtoMassa.getNomeMassaDados());
					observationMassaIsEqual = massa.getObservacao().equals(dtoMassa.getObservacao());
					
					if(!nameMassaIsEqual || !observationMassaIsEqual)
					{
						return;
					}
				}
			}
		}
		
		boolean qtdLayoutsIsDifference = lastVersion.getLayouts().size() != dto.getLayouts().size();
		
		if(qtdLayoutsIsDifference)
		{
			return;
		}
		
		throw new IllegalStateException("Formulário sem nenhuma alteração");
		
	}

	@Transactional(rollbackFor = Exception.class)
	public ChecklistVersaoDTO criar(ChecklistVersaoDTO dto,
	                                List<MultipartFile> filesLayout,
	                                List<MultipartFile> filesMassas,
	                                Map<String, MultipartFile> filesModelos) throws IOException, UmTipoDeImpressaoException {

	    // Criação checklist e versão (igual ao seu código original)
	    Checklist checklist = new Checklist();
	    checklist.setNomeDocumento(dto.getNomeDocumento());
	    checklist.setCentroCusto(dto.getCentroCusto());
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

	    Usuario usuario = new Usuario();
	    usuario.setId(dto.getIdUsuario());
	    checklistVersao.setUsuario(usuario);

	    Integer versao = checklistVersaoRepository.findMaxVersaoByChecklistId(checklistVersao.getIdChecklistVersao())
	            .orElse(0);
	    checklistVersao.setVersao(versao + 1);
	    checklistVersao = checklistVersaoRepository.save(checklistVersao);

	    // Layouts e Massas (igual ao seu código)

	    checklistVersao.setLayouts(addOrUpdateLayout(dto.getLayouts(), checklistVersao, filesLayout, filesMassas, dto));

	    // Modelos com arquivos identificados por chave
	    checklistVersao = addOrUpdateModel(dto.getModelos(), checklistVersao, filesModelos);

	    return dto;
	}
	
	private List<Layout> addOrUpdateLayout(
	        List<LayoutDTO> layoutDTOs,
	        ChecklistVersao checklistVersao,
	        List<MultipartFile> filesLayout,
	        List<MultipartFile> filesMassas, ChecklistVersaoDTO dto) throws IOException {

	    List<Layout> listaLayouts = new ArrayList<>();
	    int fileLayoutIndex = 0;
	    int fileMassaIndex = 0;

	    if (layoutDTOs != null) {
	        for (LayoutDTO layoutDTO : layoutDTOs) {
	            Layout layoutNovo = new Layout();
	            layoutNovo.setChecklistVersao(checklistVersao);
	            layoutNovo.setObservacao(layoutDTO.getObservacao());
	            layoutNovo.setDataAtualizacao(LocalDateTime.now());
	            layoutNovo.setViaServico(dto.isViaServico());
	            layoutNovo.setViaTxt(dto.isViaTxt());

	            // Arquivo do layout
	            if (filesLayout != null && fileLayoutIndex < filesLayout.size()) {
	                MultipartFile fl = filesLayout.get(fileLayoutIndex++);
	                layoutNovo.setTipoMIME(fl.getContentType());
	                layoutNovo.setConteudoLayout(fl.getBytes());
	                layoutNovo.setNomeLayout(fl.getOriginalFilename());
	            }

	            layoutNovo = layoutRepository.save(layoutNovo);

	            // Massas do layout
	            List<MassaDados> listaMassas = new ArrayList<>();
	            if (layoutDTO.getMassasDados() != null) {
	                for (MassaDTO massaDTO : layoutDTO.getMassasDados()) {
	                    MassaDados massa = new MassaDados();
	                    massa.setLayout(layoutNovo);
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

	            layoutNovo.setMassasDados(listaMassas);
	            listaLayouts.add(layoutNovo);
	        }
	    }

	    return listaLayouts;
	}
	
	private ChecklistVersao addOrUpdateModel(
	        List<ModeloDTO> models,
	        ChecklistVersao checklistVersao,
	        Map<String, MultipartFile> arquivosModelos) throws IOException, UmTipoDeImpressaoException {

	    List<ModeloDocumento> list = new ArrayList<>();

	    if (models != null && !models.isEmpty()) {
	        for (int i = 0; i < models.size(); i++) {
	            ModeloDTO m = models.get(i);
	            ModeloDocumento modeloDocumento;
	            boolean novoModelo = temporalCryptoIdUtil.isUUID(m.getId());
	            modeloDocumento = new ModeloDocumento();
	            
	            modeloDocumento.setChecklistVersao(checklistVersao);
	            modeloDocumento.setDataAtualizacao(LocalDateTime.now());
	            modeloDocumento.setObservacao(m.getObservacao());
	            modeloDocumento.setCRC(false);
	            modeloDocumento.setArmazenamento(m.isTemArquivo());
	            modeloDocumento.setTempoArmazenamento(0);

	            // --- Arquivo principal ---
	            MultipartFile filePrincipal = arquivosModelos != null ? arquivosModelos.get("modelo-" + i + "-principal"): null;
	            
	            if(novoModelo)
	            {
		            if (filePrincipal == null) throw new RuntimeException("Arquivo principal do modelo não enviado");
	
		            modeloDocumento.setNomeRecurso(filePrincipal.getOriginalFilename());
		            modeloDocumento.setTipoMIME(filePrincipal.getContentType());
		            modeloDocumento.setConteudoRecurso(filePrincipal.getBytes());
	            }
	            else
	            {
	            	Integer id = temporalCryptoIdUtil.extractId(m.getId());
	            	ModeloDocumento modeloDocumentoAtual = modeloDocumentoRepository.getById(id);
	            	
	            	modeloDocumento.setNomeRecurso(modeloDocumentoAtual.getNomeRecurso());
		            modeloDocumento.setTipoMIME(modeloDocumentoAtual.getTipoMIME());
		            modeloDocumento.setConteudoRecurso(modeloDocumentoAtual.getConteudoRecurso());
	            }

	            modeloDocumento = modeloDocumentoRepository.save(modeloDocumento);
	            
	            // --- Logos ---
	            if (m.getLogos() != null) {
	                for (int j = 0; j < m.getLogos().size(); j++) {
	                    var logo = m.getLogos().get(j);

	                    if (logo.isTemArquivo()) {
	                        MultipartFile file = arquivosModelos.get("modelo-" + i + "-logo-" + j);
	                        if (file == null) throw new RuntimeException("Arquivo de logo não enviado");

	                        LogomodeloTipo tipo = logomodeloTipoRepository.findByCodigo(LogomodeloTipoCodigo.LOGO.getCodigo())
	                                .orElseThrow(() -> new RuntimeException("Tipo LOGO não encontrado"));

	                        Logomodelo lm = new Logomodelo();
	                        lm.setModeloDocumento(modeloDocumento); // modelo atualizado no laço
	                        lm.setCodigo(LogomodeloTipoCodigo.LOGO.getCodigo()); // ou outro critério
	                        lm.setTipo(tipo);
	                        lm.setArquivo(file.getBytes());
	                        lm.setTipoMIME(file.getContentType());
	                        lm.setChecklistVersao(checklistVersao);
	                        lm.setNomeRecurso(file.getOriginalFilename());

	                        logomodeloRepository.save(lm);
	                    }
	                }
	            }

	            // --- Assinaturas ---
	            if (m.getAssinaturas() != null) {
	                for (int j = 0; j < m.getAssinaturas().size(); j++) {
	                    var ass = m.getAssinaturas().get(j);

	                    if (ass.isTemArquivo()) {
	                        MultipartFile file = arquivosModelos.get("modelo-" + i + "-assinatura-" + j);
	                        if (file == null) throw new RuntimeException("Arquivo de assinatura não enviado");

	                        LogomodeloTipo tipo = logomodeloTipoRepository.findByCodigo(LogomodeloTipoCodigo.ASSINATURA.getCodigo())
	                                .orElseThrow(() -> new RuntimeException("Tipo ASSINATURA não encontrado"));

	                        Logomodelo lm = new Logomodelo();
	                        lm.setModeloDocumento(modeloDocumento);
	                        lm.setCodigo(LogomodeloTipoCodigo.ASSINATURA.getCodigo());
	                        lm.setTipo(tipo);
	                        lm.setArquivo(file.getBytes());
	                        lm.setTipoMIME(file.getContentType());
	                        lm.setChecklistVersao(checklistVersao);
	                        lm.setNomeRecurso(file.getOriginalFilename());

	                        logomodeloRepository.save(lm);
	                    }
	                }
	            }

	            // --- Arquivos adicionais ---
	            if (m.getArquivosAdicionais() != null) {
	                for (int j = 0; j < m.getArquivosAdicionais().size(); j++) {
	                    var arq = m.getArquivosAdicionais().get(j);

	                    if (arq.isTemArquivo()) {
	                        MultipartFile file = arquivosModelos.get("modelo-" + i + "-adicional-" + j);
	                        if (file == null) throw new RuntimeException("Arquivo adicional não enviado");

	                        LogomodeloTipo tipo = logomodeloTipoRepository.findByCodigo(LogomodeloTipoCodigo.ARQUIVO_ADICIONAL.getCodigo())
	                                .orElseThrow(() -> new RuntimeException("Tipo ARQUIVO_ADICIONAL não encontrado"));

	                        Logomodelo lm = new Logomodelo();
	                        lm.setModeloDocumento(modeloDocumento);
	                        lm.setCodigo(LogomodeloTipoCodigo.ARQUIVO_ADICIONAL.getCodigo());
	                        lm.setTipo(tipo);
	                        lm.setArquivo(file.getBytes());
	                        lm.setTipoMIME(file.getContentType());
	                        lm.setChecklistVersao(checklistVersao);
	                        lm.setNomeRecurso(file.getOriginalFilename());

	                        logomodeloRepository.save(lm);
	                    }
	                }
	            }

	            //Formatação & Impressão
	            boolean duplex = m.getTipoImpressao() != null && m.getTipoImpressao().contains("DUPLEX");
	            boolean isImpresso = m.getTipoImpressao() != null && m.getTipoImpressao().contains("simples");
	            
	            if(duplex && isImpresso)
	            {
	            	throw new UmTipoDeImpressaoException();
	            }
	            
	            modeloDocumento.setDuplex(duplex);
				modeloDocumento.setImpresso(isImpresso);
	            
	            //Tipo de Acabamento
	            modeloDocumento.setAcabamentoAutoEnvelope(m.getTipoAcabamento() != null && m.getTipoAcabamento().contains("autoEnvelope"));
	            modeloDocumento.setAcabamentoManuseio(m.getTipoAcabamento() != null && m.getTipoAcabamento().contains("manuseio"));
	            modeloDocumento.setAcabamentoInsercao(m.getTipoAcabamento() != null && m.getTipoAcabamento().contains("insercao"));
	            
	            //Disponibilização
	            modeloDocumento.setDisponibilizacaoCorreioSimples(m.getDisponibilizacao() != null && m.getDisponibilizacao().contains("correiosSimples"));
	            modeloDocumento.setDisponibilizacaoCorreioSimplesAR(m.getDisponibilizacao() != null && m.getDisponibilizacao().contains("correiosSimplesAR"));
	            modeloDocumento.setCRC(m.getDisponibilizacao() != null && m.getDisponibilizacao().contains("impressaoSobDemanda"));
	            modeloDocumento.setDisponibilizacaoMeusDocumentosPDF(m.getDisponibilizacao() != null && m.getDisponibilizacao().contains("meusDocumentosPdf"));
	            modeloDocumento.setDisponibilizacaoSMS(m.getDisponibilizacao() != null && m.getDisponibilizacao().contains("sms"));
	            
	            
	            //Email
	            modeloDocumento.setEmailComDocumentoAnexo(m.getEmailOpcoes() != null && m.getEmailOpcoes().contains("anexo"));
	            modeloDocumento.setEmailComDocumentoAnexoEarmazenamento(m.getEmailOpcoes() != null && m.getEmailOpcoes().contains("anexoArmazenamento"));
	            modeloDocumento.setEmailComDocumentoAnexoEcorpoEmail(m.getEmailOpcoes() != null && m.getEmailOpcoes().contains("corpoEmail"));
	            modeloDocumento.setEmailComDocumentoAnexoEarmazenamentoEemail(m.getEmailOpcoes() != null && m.getEmailOpcoes().contains("corpoEmailArmazenamento"));
	            modeloDocumento.setEmailComDocumentoAnexoECarimbo(m.getEmailOpcoes() != null && m.getEmailOpcoes().contains("anexoCarimboTempo"));
	            
	            //Regras de acesso
	            modeloDocumento.setRegrasAcesso(m.getRegrasAcesso());
	            
	            // Campos de busca
	            if (m.getCamposBusca() != null) {
	                modeloDocumento.setAcessoBackOffice(m.getCamposBusca().getBackoffice() != null);
	                modeloDocumento.setCamposBuscaBackOffice(m.getCamposBusca().getBackoffice());
	                modeloDocumento.setAcessoCliente(m.getCamposBusca().getCliente() != null);
	                modeloDocumento.setCamposBuscaCliente(m.getCamposBusca().getCliente());
	                modeloDocumento.setAcessoCorretor(m.getCamposBusca().getCorretor() != null);
	                modeloDocumento.setCamposBuscaCorretor(m.getCamposBusca().getCorretor());
	                modeloDocumento.setAcessoEstipulante(m.getCamposBusca().getEstipulante() != null);
	                modeloDocumento.setCamposBuscaEstipulante(m.getCamposBusca().getEstipulante());
	                modeloDocumento.setAcessoSubEstipulante(m.getCamposBusca().getSubestipulante() != null);
	                modeloDocumento.setCamposBuscaSubEstipulante(m.getCamposBusca().getSubestipulante());
	            }

	           
	            
	            modeloDocumentoRepository.save(modeloDocumento);
	            
	            list.add(modeloDocumento);
	        }
	    }

	    checklistVersao.setModelosDocumento(list);

	    return checklistVersao;
	}
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private TemporalCryptoIdUtil temporalCryptoIdUtil;

	@Transactional(rollbackFor = Exception.class)
	public ChecklistVersaoDTO salvarVersao(String idChecklistVersao, ChecklistVersaoDTO dto,
			List<MultipartFile> filesLayout, List<MultipartFile> filesMassas, Map<String, MultipartFile> arquivosModelos) throws Exception {

		// =========================
		// VERSÃO ATUAL
		// =========================
		Integer idCheckList = temporalCryptoIdUtil.extractId(idChecklistVersao);
		ChecklistVersao versaoAtual = checklistVersaoRepository.findById(idCheckList)
				.orElseThrow(() -> new IllegalStateException("Versão não encontrada"));

		// =========================
		// NOVA VERSÃO
		// =========================
		
		if(!dto.isIcatu() && !dto.isCaixa() && !dto.isRioGrande())
		{
			throw new IllegalStateException("Selecione um tipo de documento");
		}
		
		ChecklistVersao checklistVersaoNova = new ChecklistVersao();
		Checklist checklist = versaoAtual.getChecklist();
		checklistVersaoNova.setChecklist(checklist);
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
				if (!Strings.isBlank(token) && temporalCryptoIdUtil.extractId(token) != null) {
					
					Integer layoutId = temporalCryptoIdUtil.extractId(token);
					
					if(layoutId != null) {

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

				}
				// -------- LAYOUT NOVO --------
				else {
					boolean exitsRegistry = !temporalCryptoIdUtil.isUUID(dtoLayout.getId());
					if (!exitsRegistry) {
						dtoLayout.setTemArquivo(true);
					}
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
						boolean exitsRegistry = !temporalCryptoIdUtil.isUUID(dtoMassa.getId());
						if (exitsRegistry) {
							Integer massaId = temporalCryptoIdUtil.extractId(dtoMassa.getId());
							massa = arquivoRepository.findById(massaId)
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
							if (!exitsRegistry) {
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
		
		checklistVersaoNova = addOrUpdateModel(dto.getModelos(), checklistVersaoNova, arquivosModelos);
		
		return dto;
	}
	
	private ChecklistVersaoDTO converterParaDTO(ChecklistVersao c) {

	    ChecklistVersaoDTO dto = new ChecklistVersaoDTO();

	    String uuidGenerateTokenVersion =
	            temporalCryptoIdUtil.generateToken(c.getIdChecklistVersao());

	    String uuidGenerateToken =
	            temporalCryptoIdUtil.generateToken(c.getChecklist().getId());

	    dto.setIdChecklist(uuidGenerateToken);
	    dto.setIdChecklistVersao(uuidGenerateTokenVersion);
	    dto.setNomeDocumento(c.getChecklist().getNomeDocumento());
	    dto.setIdRamo(c.getChecklist().getRamo().getIdRamo());
	    dto.setNomeRamo(c.getChecklist().getRamo().getNomeRamo());
	    dto.setCentroCusto(c.getChecklist().getCentroCusto());
	    dto.setStatus(c.getStatus());
	    dto.setIdDemanda(c.getIdDemanda());

	    Usuario user = c.getUsuario();
	    UsuarioDTO usuarioDTO = new UsuarioDTO();
	    usuarioDTO.setId(user.getId());
	    usuarioDTO.setNomeUsuario(user.getNome());

	    dto.setUsuario(usuarioDTO);

	    dto.setIcatu(c.isIcatu());
	    dto.setCaixa(c.isCaixa());
	    dto.setRioGrande(c.isRioGrande());

	    return dto;
	}

	public Page<ChecklistVersaoDTO> listarPaginadoDTO(
			org.springframework.data.domain.Pageable pageable,
	        boolean isAdmin,
	        Integer idUser) {

	    if (isAdmin) {
	        return checklistVersaoRepository
	                .findUltimasVersoes(pageable)
	                .map(this::converterParaDTO);
	    }

	    return checklistVersaoRepository
	            .findByUsuarioIdOrderByDataAtualizacaoDesc(idUser, pageable)
	            .map(this::converterParaDTO);
	}

	public List<ChecklistVersaoResumoDTO> listarVersoesChecklist(Integer idChecklist) {

		List<ChecklistVersao> versoes = checklistVersaoRepository.findByChecklistIdOrderByVersaoDesc(idChecklist);

		Integer versaoAtual = versoes.isEmpty() ? null : versoes.get(0).getVersao();

		return versoes.stream().map(v -> {

			ChecklistVersaoResumoDTO dto = new ChecklistVersaoResumoDTO();
			dto.setIdChecklistVersao(temporalCryptoIdUtil.generateToken(v.getIdChecklistVersao()));
			dto.setIdDemanda(v.getIdDemanda());
			dto.setVersao(v.getVersao());
			dto.setDataCadastro(v.getDataCadastro());
			dto.setDataAtualizacao(v.getDataAtualizacao());
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
	    String uuidGenerateTokenVersion = temporalCryptoIdUtil.generateToken(c.getIdChecklistVersao());
	    String uuidGenerateToken = temporalCryptoIdUtil.generateToken(c.getChecklist().getId());

	    dto.setIdChecklist(uuidGenerateToken);
	    dto.setIdChecklistVersao(uuidGenerateTokenVersion);
	    dto.setNomeDocumento(c.getChecklist().getNomeDocumento());
	    dto.setCentroCusto(c.getChecklist().getCentroCusto());

	    Ramo ramo = c.getChecklist().getRamo();
	    dto.setIdRamo(ramo.getIdRamo());
	    dto.setNomeRamo(ramo.getNomeRamo());

	    dto.setStatus(c.getStatus());
	    dto.setIcatu(c.isIcatu());
	    dto.setCaixa(c.isCaixa());
	    dto.setRioGrande(c.isRioGrande());
	    dto.setIdUsuario(c.getUsuario().getId());
	    dto.setIdDemanda(c.getIdDemanda());

	    // -------- Usuário DTO --------
	    UsuarioDTO usuarioDTO = new UsuarioDTO();
	    usuarioDTO.setId(c.getUsuario().getId());
	    usuarioDTO.setNomeUsuario(c.getUsuario().getNome());
	    dto.setUsuario(usuarioDTO);

	    // ===============================================================
	    // -------- Layouts + Massas ------------------------------------
	    // ===============================================================

	    List<LayoutDTO> layoutDtos = new ArrayList<>();

	    boolean temLayout = c.getLayouts() != null && !c.getLayouts().isEmpty();
	    boolean viaServico = false;
	    boolean viaTxt = false;

	    if (c.getLayouts() != null) {
	        for (Layout layout : c.getLayouts()) {

	            if (layout.isViaServico()) {
	                viaServico = true;
	            }
	            if (layout.isViaTxt()) {
	                viaTxt = true;
	            }

	            LayoutDTO layoutDTO = new LayoutDTO();
	            layoutDTO.setId(temporalCryptoIdUtil.generateToken(layout.getId()));
	            layoutDTO.setNomeLayout(layout.getNomeLayout());
	            layoutDTO.setObservacao(layout.getObservacao());

	            List<MassaDTO> massaDtos = new ArrayList<>();
	            if (layout.getMassasDados() != null) {
	                for (MassaDados massa : layout.getMassasDados()) {

	                    MassaDTO massaDTO = new MassaDTO();
	                    massaDTO.setId(temporalCryptoIdUtil.generateToken(massa.getId()));
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

	    // ===============================================================
	    // -------- MODELOS ----------------------------------------------
	    // ===============================================================

	    List<ModeloDTO> modeloDtos = new ArrayList<>();

	    if (c.getModelosDocumento() != null) {
	        for (ModeloDocumento modelo : c.getModelosDocumento()) {

	            ModeloDTO modeloDTO = new ModeloDTO();
	            modeloDTO.setId(temporalCryptoIdUtil.generateToken(modelo.getId()));
	            modeloDTO.setNomeRecurso(modelo.getNomeRecurso());
	            modeloDTO.setObservacao(modelo.getObservacao());
	            modeloDTO.setTemArquivo(modelo.getNomeRecurso() != null);

	         // -------- LISTAS DE DESTINO --------
	            List<ItemArquivoDTO> logosDto = new ArrayList<>();
	            List<ItemArquivoDTO> arquivosAdicionaisDto = new ArrayList<>();
	            List<ItemArquivoDTO> assinaturasDto = new ArrayList<>();

	            // -------- LOOP ÚNICO --------
	            if (c.getLogos() != null) {
	                for (Logomodelo lm : c.getLogos()) {

	                    LogomodeloTipoCodigo tipoEnum = lm.getTipo().getEnum(); // já pega uma vez

	                    // Prepara DTO (evita duplicação)
	                    ItemArquivoDTO dto2 = new ItemArquivoDTO();
	                    dto2.setId(temporalCryptoIdUtil.generateToken(lm.getId()));
	                    dto2.setCodigo(lm.getCodigo());
	                    dto2.setTipo(lm.getTipo().getCodigo());
	                    dto2.setDescricaoTipo(lm.getTipo().getDescricao());
	                    dto2.setArquivo(lm.getArquivo());
	                    dto2.setMimeType(lm.getTipoMIME());
	                    dto2.setName(lm.getNomeRecurso());

	                    // Direciona conforme o tipo
	                    switch (tipoEnum) {
	                        case LOGO:
	                            logosDto.add(dto2);
	                            break;

	                        case ARQUIVO_ADICIONAL:
	                            arquivosAdicionaisDto.add(dto2);
	                            break;

	                        case ASSINATURA:
	                            assinaturasDto.add(dto2);
	                            break;

	                        default:
	                            // se aparecer outro tipo no futuro
	                            break;
	                    }
	                    
	                    
	                }
	            }

	            // -------- SETA NOS DTOs --------
	            modeloDTO.setLogos(logosDto);
	            modeloDTO.setArquivosAdicionais(arquivosAdicionaisDto);
	            modeloDTO.setAssinaturas(assinaturasDto);
	            modeloDtos.add(modeloDTO);
	            
	            CamposBuscaDTO camposBuscaDTO = new CamposBuscaDTO();
	            
	            camposBuscaDTO.setBackoffice(modelo.getCamposBuscaBackOffice());
	            camposBuscaDTO.setCliente(modelo.getCamposBuscaCliente());
	            camposBuscaDTO.setCorretor(modelo.getCamposBuscaCorretor());
	            camposBuscaDTO.setEstipulante(modelo.getCamposBuscaEstipulante());
	            camposBuscaDTO.setEstipulante(modelo.getCamposBuscaEstipulante());
	            
	            modeloDTO.setCamposBusca(camposBuscaDTO);
	        }
	    }

	    dto.setModelos(modeloDtos);

	    return dto;
	}
}