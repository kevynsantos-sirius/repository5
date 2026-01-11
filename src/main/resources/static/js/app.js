/* Sempre que clicar em NOVO DOCUMENTO → reset total */
const API = window.API_BASE_URL;


function buildChecklist() {
	return {
		...window.buildIdentificacao(),
		...window.buildTI()
	};
}

/***************************************************
 * Alternar exibição das abas
 ***************************************************/
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

/***************************************************
 * Botão NOVO DOCUMENTO — reset TOTAL do sistema
 ***************************************************/
function toggleMenu() {

	console.log("🆕 Novo Documento — Resetando todos os formulários...");

	// Reset Identificação
	window.limparIdentificacao();

	// Reset TI
	limparTI();

	// Voltar para aba Identificação
	mostrarAba("aba-identificacao");

	// Abrir submenu
	const submenu = document.getElementById("submenu");
	const btnNovo = document.getElementById("btnNovo");

	submenu.style.display = "flex";
	btnNovo.classList.add("active");
}

function cancelarCriacaoChecklist() {

	console.log("❌ Cancelando criação do checklist...");

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

function cancelarCriacao() {
    console.log("❌ Cancelando criação");

    // Voltar à tela dos checklists
    window.location.href = "/index"; 
    // ou qualquer rota que você usa como inicial
}