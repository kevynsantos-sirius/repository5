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
	window.currentChecklistVersaoId = null;

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
	/*const painel = document.getElementById("painel-versoes");*/
	
    if (submenu) submenu.style.display = "none";

    if (btnNovo)     btnNovo.classList.remove("d-none");
    if (btnSalvar)   btnSalvar.classList.add("d-none");
    if (btnCancelar) btnCancelar.classList.add("d-none");
	
	/*if (painel) painel.style.display = "none";*/
	/*const painelVersoes = document.getElementById("painel-versoes");
	if (painelVersoes) painelVersoes.classList.add("collapse");*/
	
	const btnCarregarVersoes = document.getElementById("btnCarregarVersoes");
	if (btnCarregarVersoes) btnCarregarVersoes.classList.add("collapse");
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

    if (!window.currentChecklistVersaoId) {
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

    // 🔹 buildChecklist já chama buildTI()
    const dados = buildChecklist();

    const formData = new FormData();

    // 🔹 DTO COMPLETO (inclui layouts + temArquivo)
    formData.append(
        "dto",
        new Blob([JSON.stringify(dados)], { type: "application/json" })
    );

    // 🔹 ARQUIVOS DE LAYOUT
    // (somente os que realmente existem)
    if (dados._filesLayout && dados._filesLayout.length > 0) {
        dados._filesLayout.forEach(file => {
            formData.append("filesLayout", file);
        });
    }

    // 🔹 ARQUIVOS DE MASSAS
    if (dados._filesMassas && dados._filesMassas.length > 0) {
        dados._filesMassas.forEach(file => {
            formData.append("filesMassas", file);
        });
    }

    fetch(`${API}Checklists/${window.currentChecklistVersaoId}/editar`, {
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
        .then(() => {
            alert("✔ Nova versão criada com sucesso!");
            window.location.href = "/";
        })
        .catch(err => {
            console.error("❌ Erro:", err);
            alert("Erro ao salvar edição.");
        });
}

/***************************************************
 * ABRIR CHECKLIST (EDIÇÃO)
 ***************************************************/
window.abrirChecklist = function (id) {

    console.log("Abrindo checklist id =", id);
	
	window.modoEdicao = true;
	
	const painel = document.getElementById("btnCarregarVersoes");
	if (painel) painel.classList.remove("collapse");
	
    mostrarAba("aba-identificacao");

    const submenu = document.getElementById("submenu");
    const Novo = document.getElementById("btnNovo");
    const btnSalvar = document.getElementById("btnSalvar");
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
			
			window.currentChecklistVersaoId = dto.idChecklistVersao;
			window.currentChecklistId       = dto.idChecklist;
            
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
			
			// 🆕 carregar versões na lateral
			/*carregarVersoesChecklist(dto.idChecklist);*/
        })
        .catch(err => {
            console.error("❌ Erro:", err);
            alert("Erro ao abrir documento");
        });
};

function carregarVersoesChecklist(idChecklist) {

    fetch(`${API}Checklists/${idChecklist}/versoes`)
        .then(res => res.json())
        .then(versoes => {

            /*const painel = document.getElementById("btnCarregarVersoes");*/
            const lista  = document.getElementById("listaVersoesLateral");

            /*painel.style.display = "block";*/
			
			lista.innerHTML = "";

            versoes.forEach(v => {

                const li = document.createElement("li");

                if (v.atual) {
                    li.classList.add("atual");
                    li.textContent = `${v.idDemanda}`;
                } else {
                    li.innerHTML = `
					    <small>${v.idDemanda}</small>
                        <small>Versão ${v.versao} - ${formatarData(v.dataCadastro)}</small>
                    `;

                    li.onclick = () => abrirVersaoChecklist(v.idChecklistVersao);
                }

                lista.appendChild(li);
            });
        });
}


/***************************************************
 * Controle inteligente do botão SALVAR
 ***************************************************/
document.addEventListener("DOMContentLoaded", () => {

    const btnSalvar = document.getElementById("btnSalvar");

    if (!btnSalvar) return;

    btnSalvar.onclick = () => {

        if (window.modoEdicao === true && window.currentChecklistVersaoId) {
            console.log("✏️ Salvando edição (nova versão)");
            salvarEdicaoChecklist();
        } else {
            console.log("🆕 Salvando novo checklist");
            salvarChecklist();
        }
    };
});

document.addEventListener("DOMContentLoaded", () => {

    const btnVersoes = document.getElementById("btnCarregarVersoes");

    if (btnVersoes) {
        btnVersoes.addEventListener("click", () => {
	
            if (!window.currentChecklistId) {
                console.warn("Checklist ainda não carregado");
                return;
            }
			
			 
			/*const painelVersoes = document.getElementById("painel-versoes");
			if (painelVersoes) {
				//(document.getElementsByClassName("btn-close")[0]).click();
				painelVersoes.classList.add("show");
			}*/

            // 🆕 carregar versões na lateral
            carregarVersoesChecklist(window.currentChecklistId);
        });
    }
});


function formatarData(dataIso) {
    if (!dataIso) return "";
    return new Date(dataIso).toLocaleDateString("pt-BR");
}
