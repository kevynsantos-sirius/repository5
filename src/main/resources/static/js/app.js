/* API */
const API = window.API_BASE_URL;

/***************************************************
 * Build Checklist (JSON base)
 ***************************************************/
function buildChecklist() {
    return {
        ...window.buildIdentificacao(),
        ...window.buildTI()
    };
}

/***************************************************
 * Alternar exibição das abas
 ***************************************************/
/*function mostrarAba(abaId) {

    document.querySelectorAll(".aba").forEach(aba => {
        aba.style.display = "none";
    });

    document.getElementById(abaId).style.display = "block";

    if (abaId === "aba-ti" && window.initTiEvents) {
        setTimeout(() => window.initTiEvents(), 20);
    }

    if (abaId === "aba-modelo" && window.initTiEvents) {
        setTimeout(() => window.initTiEvents(), 20);
    }
}*/
function mostrarAba(abaId) {

    document.querySelectorAll(".aba").forEach(aba => {
        aba.style.display = "none";
    });

    document.getElementById(abaId).style.display = "block";
}


/***************************************************
 * NOVO DOCUMENTO
 ***************************************************/
function NovoDocumento() {

    window.modoEdicao = false;

    console.log("Novo Documento — Reset total");

    // Responsável = usuário logado
    const spanNome = document.getElementById("responsavelNome");
    const inputId  = document.getElementById("responsavel");

    if (spanNome) spanNome.textContent = window.USUARIO_LOGADO_NOME;
    if (inputId)  inputId.value = window.USUARIO_LOGADO_ID;

    // Limpa formulários
    if (window.limparIdentificacao) {
        window.limparIdentificacao();
    }
    if (window.limparTI) {
        window.limparTI();
    }

    // Volta para identificação
    mostrarAba("aba-identificacao");

    // UI botões
    const submenu     = document.getElementById("submenu");
    const btnNovo     = document.getElementById("btnNovo");
    const btnSalvar   = document.getElementById("btnSalvar");
    const btnCancelar = document.getElementById("btnCancelar");

    if (submenu) submenu.style.display = "flex";

    if (btnNovo)     btnNovo.classList.add("d-none");
    if (btnSalvar)   btnSalvar.classList.remove("d-none");
    if (btnCancelar) btnCancelar.classList.remove("d-none");
}

/***************************************************
 * CANCELAR CRIAÇÃO / EDIÇÃO
 ***************************************************/
function cancelarCriacaoChecklist() {

    const confirmar = confirm(
        "Tem certeza que deseja cancelar?\nTodos os dados preenchidos serão perdidos."
    );

    if (!confirmar) return;

    console.log("❌ Cancelando checklist");

    window.modoEdicao = false;
    window.currentChecklistId = null;

    if (window.limparIdentificacao) {
        window.limparIdentificacao();
    }
    if (window.limparTI) {
        window.limparTI();
		limparLayoutsTela();
    }

    mostrarAba("aba-home");

    const submenu     = document.getElementById("submenu");
    const btnNovo     = document.getElementById("btnNovo");
    const btnSalvar   = document.getElementById("btnSalvar");
    const btnCancelar = document.getElementById("btnCancelar");

    if (submenu) submenu.style.display = "none";

    if (btnNovo)     btnNovo.classList.remove("d-none");
    if (btnSalvar)   btnSalvar.classList.add("d-none");
    if (btnCancelar) btnCancelar.classList.add("d-none");
}

/***************************************************
 * Validação geral
 ***************************************************/
function validarChecklist() {

    let erros = [];

    if (window.validarIdentificacao) {
        erros = erros.concat(window.validarIdentificacao());
    }

    if (window.validarTi) {
        erros = erros.concat(window.validarTi());
    }

    return erros;
}

/***************************************************
 * SALVAR (CREATE)
 ***************************************************/
function salvarChecklist() {

    const erros = validarChecklist();

    if (erros.length > 0) {
        alert("Corrija os seguintes pontos:\n\n" + erros.join("\n"));
        return;
    }

    if (!confirm("Deseja realmente salvar este documento?")) {
        return;
    }

    const dados = buildChecklist();

    // Remove campos internos
    const dadosParaEnviar = { ...dados };
    delete dadosParaEnviar._filesLayout;
    delete dadosParaEnviar._filesMassas;

    const formData = new FormData();

    formData.append(
        "dados",
        new Blob([JSON.stringify(dadosParaEnviar)], { type: "application/json" })
    );

    if (dados._filesLayout) {
        dados._filesLayout.forEach(f =>
            formData.append("filesLayout", f)
        );
    }

    if (dados._filesMassas) {
        dados._filesMassas.forEach(f =>
            formData.append("filesMassas", f)
        );
    }

    fetch(`${API}Checklists/salvar`, {
        method: "POST",
        body: formData
    })
        .then(async res => {

            if (!res.ok) {
                console.error(await res.text());
                throw new Error("Erro ao salvar checklist");
            }

            return res.json();
        })
        .then(() => {
            alert("✔ Documento salvo com sucesso!");
            window.location.href = "/";
        })
        .catch(err => {
            console.error("❌ Erro:", err);
            alert("Erro ao salvar o documento");
        });
}

/***************************************************
 * ABRIR CHECKLIST (EDIÇÃO)
 ***************************************************/
window.abrirChecklist = function (id) {

    console.log("Abrindo checklist id =", id);

    window.modoEdicao = true;

    mostrarAba("aba-identificacao");

    const submenu     = document.getElementById("submenu");
    const btnNovo     = document.getElementById("btnNovo");
    const btnSalvar   = document.getElementById("btnSalvar");
    const btnCancelar = document.getElementById("btnCancelar");

    if (submenu) submenu.style.display = "flex";

    if (btnNovo)     btnNovo.classList.add("d-none");
    if (btnSalvar)   btnSalvar.classList.remove("d-none");
    if (btnCancelar) btnCancelar.classList.remove("d-none");

    fetch(`${API}Checklists/${id}`)
        .then(res => {
            if (!res.ok) {
                throw new Error("Erro ao carregar checklist");
            }
            return res.json();
        })
        .then(dto => {

            console.log("✔ Checklist carregado:", dto);

            window.currentChecklistId = dto.id;

            if (window.preencherIdentificacao) {
                window.preencherIdentificacao(dto);
            }

            if (window.preencherTI) {
                window.preencherTI(dto);
            }

            if (window.limparLayoutsTela) {
                window.limparLayoutsTela();
            }

            if (window.carregarLayouts) {
                window.carregarLayouts(dto.layouts);
            }
        })
        .catch(err => {
            console.error("❌ Erro:", err);
            alert("Erro ao abrir documento");
        });
};

/* Sempre que clicar em NOVO DOCUMENTO → reset total */
/*const API = window.API_BASE_URL;


function buildChecklist() {
	return {
		...window.buildIdentificacao(),
		...window.buildTI()
	};
}

**************************************************
 * Alternar exibição das abas
 **************************************************
function mostrarAba(abaId) {

	// Esconde todas
	document.querySelectorAll(".aba").forEach(aba => {
		aba.style.display = "none";
	});

	// Mostra a aba selecionada
	document.getElementById(abaId).style.display = "block";

	// Se for TI, recriar Quill e eventos
	if (abaId === "aba-ti" && window.initTiEvents) {
		setTimeout(() => window.initTiEvents(), 20);
	}

	// Se for modelo, recriar
	if (abaId === "aba-modelo" && window.initTiEvents) {
		setTimeout(() => window.initTiEvents(), 20);
	}
}

**************************************************
 * Botão NOVO DOCUMENTO — reset TOTAL do sistema
 **************************************************
function NovoDocumento() {
	window.modoEdicao = false;

	const spanNome = document.getElementById("responsavelNome");
	const inputId = document.getElementById("responsavel");

	if (spanNome) spanNome.textContent = window.USUARIO_LOGADO_NOME;
	if (inputId) inputId.value = window.USUARIO_LOGADO_ID;

	console.log("Novo Documento — Resetando todos os formulários...");

	// Reset Identificação
	window.limparIdentificacao();

	// Reset TI
	limparTI();

	// Voltar para aba Identificação
	mostrarAba("aba-identificacao");

	// Abrir submenu
	const submenu = document.getElementById("submenu");
	const btnNovo = document.getElementById("btnNovo");
	const btnSalvar = document.getElementById("btnSalvar");
	const btnCancelar = document.getElementById("btnCancelar");

	submenu.style.display = "flex";
	btnNovo.classList.add("active");

	// some o NOVO e mostra salvar/cancelar
	btnNovo.classList.add("d-none");
	btnSalvar.classList.remove("d-none");
	btnCancelar.classList.remove("d-none");
}

function cancelarCriacaoChecklist() {
	const btnSalvar = document.getElementById("btnSalvar");
	const btnCancelar = document.getElementById("btnCancelar");

	console.log("❌ Cancelando criação do checklist...");
	// Popup de confirmação
	const confirmar = confirm("Tem certeza que deseja cancelar a criação do documento?\nTodos os dados preenchidos serão perdidos.");

	if (!confirmar) {
		return; // usuário desistiu de cancelar
	}

	criandoChecklist = false;

	// Reabilita botão NOVO
	const btnNovo = document.getElementById("btnNovo");
	btnNovo.disabled = false;
	btnNovo.classList.remove("disabled");

	// Fecha submenu
	const submenu = document.getElementById("submenu");
	submenu.style.display = "none";

	// Volta para Home
	mostrarAba("aba-home");

	// Limpa formulários
	window.limparIdentificacao();
	limparTI();

	btnNovo.classList.remove("d-none");
	btnSalvar.classList.add("d-none");
	btnCancelar.classList.add("d-none");
}

function validarChecklist() {
	let erros = [];

	if (window.validarIdentificacao) {
		erros = erros.concat(window.validarIdentificacao());
	}

	if (window.validarTi) {
		erros = erros.concat(window.validarTi());
	}

	return erros;
}

function salvarChecklist() {
	const erros = validarChecklist();

	if (erros.length > 0) {
		alert("Corrija os seguintes pontos antes de salvar:\n\n" + erros.join("\n"));
		return;
	}

	// Confirmação antes de salvar
	if (!confirm("Deseja realmente salvar este documento?")) {
		return; // ❌ Cancela o salvar
	}

	const dados = buildChecklist();

	// --- REMOVE CAMPOS PROIBIDOS DO JSON ---
	const dadosParaEnviar = { ...dados };
	delete dadosParaEnviar._filesLayout;
	delete dadosParaEnviar._filesMassas;

	const formData = new FormData();

	// JSON LIMPO
	formData.append(
		"dados",
		new Blob([JSON.stringify(dadosParaEnviar)], { type: "application/json" })
	);

	// --- ARQUIVOS DE LAYOUT ---
	if (dados._filesLayout) {
		dados._filesLayout.forEach(f => formData.append("filesLayout", f));
	}

	// --- ARQUIVOS DE MASSAS ---
	if (dados._filesMassas) {
		dados._filesMassas.forEach(f => formData.append("filesMassas", f));
	}

	fetch(`${API}Checklists/salvar`, {
		method: "POST",
		body: formData
	})
		.then(async res => {

			const contentType = res.headers.get("content-type") || "";

			if (!res.ok) {
				console.error("Erro HTTP:", res.status);
				console.error(await res.text());
				throw new Error("Erro HTTP");
			}

			if (!contentType.includes("application/json")) {
				console.warn("Backend não retornou JSON");
				return {};
			}

			return res.json();
		})
		.then(data => {
			console.log("✔ Salvo com sucesso:", data);
			alert("Salvo com sucesso!");
			window.location.href = "/";
		})
		.catch(err => { console.error("❌ Erro:", err); alert(err); });
}

window.abrirChecklist = function(id) {

	console.log("Abrindo checklist id=", id);

	// 1) Esconder aba HOME e mostrar formulário
	mostrarAba("aba-identificacao"); // vai esconder home e mostrar a aba ident.

	// Mostrar submenu / modo criação/edição
	const submenu = document.getElementById("submenu");
	if (submenu) submenu.style.display = "flex";

	// Alternar botões (Novo x Salvar/Cancelar)
	const btnNovo = document.getElementById("btnNovo");
	const btnSalvar = document.getElementById("btnSalvar");
	const btnCancelar = document.getElementById("btnCancelar");

	if (btnNovo) btnNovo.classList.add("d-none");
	if (btnSalvar) btnSalvar.classList.remove("d-none");
	if (btnCancelar) btnCancelar.classList.remove("d-none");

	// 2) Buscar dados no backend
	fetch(`${API}Checklists/${id}`)
		.then(res => {
			if (!res.ok) {
				throw new Error("Erro ao carregar documento. Código: " + res.status);
			}
			return res.json();
		})
		.then(dto => {
			console.log("Checklist carregado:", dto);

			// Guarda id em memória global se quiser usar depois (edição)
			window.currentChecklistId = dto.id;

			// 3) Preencher formulários
			if (window.preencherIdentificacao) {
				window.preencherIdentificacao(dto);
			}
			if (window.preencherTI) {
				window.preencherTI(dto);
			}
			
			preencherTI(dto);
			limparLayoutsTela();
			carregarLayouts(dto.layouts);
			
			
		})
		.catch(err => {
			console.error("❌ Erro ao abrir checklist:", err);
			alert("Erro ao abrir o documento. Tente novamente.");
		});
};
*/