/***************************************************
 * RESET do formulário de identificação
 ***************************************************/
window.limparIdentificacao = function() {

	const form = document.getElementById("formIdentificacao");

	if (!form) {
		console.warn("Formulário Identificação não está no DOM.");
		return;
	}

	console.log("Limpando Identificação...");

	// Limpa todos os campos automaticamente
	form.reset();

	// Força o select de Ramo voltar ao primeiro
	const ramo = document.getElementById("ramo");
	if (ramo) ramo.selectedIndex = 0;
}

// Constrói o objeto da aba Identificação
window.buildIdentificacao = function() {

	return {
		nomeDocumento: document.getElementById("nomeDocumento")?.value || "",
		centroCusto: document.getElementById("centroCusto")?.value || "",
		idRamo: document.getElementById("ramo")?.value || "",
		status: document.getElementById("statusDocumento")?.value || "",
		idUsuario: document.getElementById("responsavel")?.value || "",
		icatu: document.getElementById("isIcatu")?.checked || false,
		caixa: document.getElementById("isCaixa")?.checked || false,
		rioGrande: document.getElementById("isRG")?.checked || false,
		idDemanda: document.getElementById("demanda")?.value || "",

	};
};

// Validação da aba Identificação
window.validarIdentificacao = function() {
	const erros = [];

	const nomeDocumento = document.getElementById("nomeDocumento")?.value.trim();
	const idRamo = document.getElementById("ramo")?.value;
	const status = document.getElementById("statusDocumento")?.value;
	const centroCusto = document.getElementById("centroCusto")?.value.trim();
	const idUsuario = document.getElementById("responsavel")?.value;

	const chkIcatu = document.getElementById("isIcatu")?.checked;
	const chkCaixa = document.getElementById("isCaixa")?.checked;
	const chkRioGrande = document.getElementById("isRG")?.checked;
	const idDemanda = document.getElementById("demanda")?.value.trim();

	// Campos obrigatórios
	if (!nomeDocumento) {
		erros.push("• Informe o Nome do documento.");
	}

	if (!idRamo) {
		erros.push("• Selecione o Ramo.");
	}

	if (!status) {
		erros.push("• Selecione o Status do documento.");
	}

	if (!centroCusto) {
		erros.push("• Informe o Centro de custo.");
	} else {
		if (!/^[0-9]+$/.test(centroCusto)) {
			erros.push("• Centro de custo deve conter apenas números.");
		}
	}

	if (!idUsuario) {
		erros.push("• Responsável não identificado (usuário logado).");
	}

	// Pelo menos um checkbox marcado
	if (!chkIcatu && !chkCaixa && !chkRioGrande) {
		erros.push("• Marque pelo menos uma opção (Icatu, Caixa ou Rio Grande).");
	}

	return erros;

	// Campos obrigatórios
	if (!idDemanda) {
		erros.push("• Informe a identificação da demanda.");
	}
};

window.preencherIdentificacao = function(dto) {
	window.modoEdicao = true;

	console.log("Preenchendo Identificação com DTO:", dto);

	document.getElementById("nomeDocumento").value = dto.nomeDocumento || "";
	document.getElementById("centroCusto").value = dto.centroCusto || "";
	document.getElementById("ramo").value = dto.idRamo || "";
	document.getElementById("statusDocumento").value = dto.status || "";

	document.getElementById("isIcatu").checked = !!dto.icatu;
	document.getElementById("isCaixa").checked = !!dto.caixa;
	document.getElementById("isRG").checked = !!dto.rioGrande;

	const spanNome = document.getElementById("responsavelNome");
	const inputId = document.getElementById("responsavel");

	if (window.modoEdicao) {
		// ✏️ edição → dados do banco
		if (spanNome) spanNome.textContent = dto.usuario?.nomeUsuario || "";
		if (inputId) inputId.value = dto.idUsuario || "";
	} else {
		// 🆕 criação → usuário logado
		if (spanNome) spanNome.textContent = window.USUARIO_LOGADO_NOME;
		if (inputId) inputId.value = window.USUARIO_LOGADO_ID;
	}
	
	document.getElementById("demanda").value = dto.idDemanda || "";
};
