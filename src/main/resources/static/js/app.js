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
 * SALVAR EDIÇÃO (gera NOVA versão)
 ***************************************************/
function salvarEdicaoChecklist() {

    if (!window.currentChecklistId) {
        alert("Checklist não identificado para edição.");
        return;
    }

    const erros = validarChecklist();
    if (erros.length > 0) {
        alert("Corrija os seguintes pontos:\n\n" + erros.join("\n"));
        return;
    }

    if (!confirm("Deseja realmente salvar as alterações?\nUma nova versão será criada.")) {
        return;
    }

    const dados = buildChecklist();

    // 🔒 remove campos internos
    const dadosParaEnviar = { ...dados };
    delete dadosParaEnviar._filesLayout;
    delete dadosParaEnviar._filesMassas;

    const formData = new FormData();

    // ⚠️ NOME DO RequestPart = "dto"
    formData.append(
        "dto",
        new Blob([JSON.stringify(dadosParaEnviar)], { type: "application/json" })
    );

    // Arquivos Layout
    if (dados._filesLayout) {
        dados._filesLayout.forEach(f =>
            formData.append("filesLayout", f)
        );
    }

    // Arquivos Massas
    if (dados._filesMassas) {
        dados._filesMassas.forEach(f =>
            formData.append("filesMassas", f)
        );
    }

    fetch(`${API}Checklists/${window.currentChecklistId}/editar`, {
	
        method: "POST",
        body: formData
    })
        .then(async res => {
            if (!res.ok) {
                console.error(await res.text());
                throw new Error("Erro ao salvar edição");
            }
            return res.json();
        })
        .then(dto => {
            console.log("✔ Nova versão criada:", dto);
            alert("✔ Alterações salvas! Nova versão criada com sucesso.");
            window.location.href = "/";
        })
        .catch(err => {
            console.error("❌ Erro:", err);
            alert("Erro ao salvar edição do checklist.");
        });
}

/***************************************************
 * ABRIR CHECKLIST (EDIÇÃO)
 ***************************************************/
window.abrirChecklist = function (id) {

    console.log("Abrindo checklist id =", id);
	
	window.currentChecklistId = id;
    window.modoEdicao = true;

    mostrarAba("aba-identificacao");

    const submenu     = document.getElementById("submenu");
    const btnNovo     = document.getElementById("btnNovo");
    const btnSalvar   = document.getElementById("btnSalvar");
    const btnCancelar = document.getElementById("btnCancelar");

    if (submenu) submenu.style.display = "flex";

    if (btnNovo) btnNovo.classList.add("d-none");
	if (btnSalvar) btnSalvar.classList.remove("d-none");
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

            window.currentChecklistId = dto.idChecklistVersao;

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

/***************************************************
 * Controle inteligente do botão SALVAR
 ***************************************************/
document.addEventListener("DOMContentLoaded", () => {

    const btnSalvar = document.getElementById("btnSalvar");

    if (!btnSalvar) return;

    btnSalvar.onclick = () => {

        if (window.modoEdicao === true && window.currentChecklistId) {
            console.log("✏️ Salvando edição (nova versão)");
            salvarEdicaoChecklist();
        } else {
            console.log("🆕 Salvando novo checklist");
            salvarChecklist();
        }
    };
});
