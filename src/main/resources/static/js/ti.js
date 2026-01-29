/***************************************************
 * CONTROLE DE INICIALIZAÇÃO (ANTI DUPLICAÇÃO)
 ***************************************************/
window.tiEventsInicializados = false;

/**************************************************
 * INIT TI (executa apenas uma vez)
 **************************************************/
document.addEventListener("DOMContentLoaded", () => {
	initTiEvents();
});

/**************************************************
 * Eventos iniciais TI
 **************************************************/
function initTiEvents() {

	if (window.tiEventsInicializados) return;
	window.tiEventsInicializados = true;

	const chkHasLayout = document.getElementById("hasLayout");
	const layoutDiv = document.getElementById("inputLayoutDiv");
	const btnAddLayout = document.getElementById("btnAddLayout");

	if (chkHasLayout) {
		chkHasLayout.onchange = () => {
			layoutDiv.style.display = chkHasLayout.checked ? "block" : "none";

			if (!chkHasLayout.checked) {
				limparLayoutsTela();
			}
		};
	}

	if (btnAddLayout) {
		// conclick substitui handler anterior (não duplica)
		btnAddLayout.onclick = () => window.criarLayoutCard();
	}
}

/**************************************************
 * Criar Layout Card
 **************************************************/
window.criarLayoutCard = function() {

	const layoutContainer = document.getElementById("inputsLayoutContainer");

	const card = document.createElement("div");
	card.className = "card p-4 mb-3 layout-card";

	card.innerHTML = `
	    <!-- 🔑 ID DO LAYOUT (EDIÇÃO) -->
	    <input type="hidden" class="layoutIdHidden" value="0">

	    <div class="d-flex justify-content-between align-items-center mb-2">
	        <span class="nomeArquivoLayout text-muted">
	            <i class="bi bi-file-earmark-text me-1"></i>
	        </span>

	        <button class="btn btn-sm btn-outline-danger"
	            type="button"
	            onclick="this.closest('.layout-card').remove()">
	            - Remover
	        </button>
	    </div>

	    <label>Arquivo Layout</label>
	    <input class="form-control fileLayout" type="file">

	    <label class="form-label mt-2">Observação</label>
	    <div class="quillLayout" style="height:120px;background:white;"></div>
	    <input type="hidden" class="obsLayoutHidden">

	    <hr>

	    <div class="d-flex justify-content-between align-items-center">
	        <h6 class="mb-0">Massas de Dados</h6>
	        <button class="btn btn-sm btn-secondary"
	            type="button"
	            onclick="addMassa(this)">
	            + Adicionar Massa
	        </button>
	    </div>
	    <div class="massasContainer mt-2"></div>
	`;


	layoutContainer.appendChild(card);

	/* Quill */
	const quill = new Quill(card.querySelector(".quillLayout"), { theme: "snow" });
	card._quillLayout = quill;

	quill.on("text-change", () => {
		card.querySelector(".obsLayoutHidden").value = quill.root.innerHTML;
	});

	/*Input file*/ 
	const fileInput = card.querySelector(".fileLayout");
	const nomeSpan = card.querySelector(".nomeArquivoLayout");

	fileInput.addEventListener("change", () => {
		if (fileInput.files.length > 0) {
			card._arquivoAlterado = true;

			nomeSpan.textContent =
				`Novo arquivo: ${fileInput.files[0].name}`;

			nomeSpan.classList.remove("text-primary");
			nomeSpan.style.cursor = "default";
			nomeSpan.onclick = null; // 🔒 bloqueia download		
		}
	});

	return card;
};

/**************************************************
 * Adicionar Massa
 **************************************************/
window.addMassa = function(btn,massaObj = null) {
	var idMassa = "0";
	if(massaObj != null)
	{
		idMassa = massaObj.id;
	}

	const layoutCard = btn.closest(".layout-card");
	const container = layoutCard.querySelector(".massasContainer");

	const massa = document.createElement("div");
	massa.className = "card p-2 mb-2 massa-card";

	massa.innerHTML = `
        <div class="d-flex justify-content-between align-items-center mb-2">
            <span class="nomeArquivoMassa text-muted"></span>

            <button class="btn btn-sm btn-outline-danger"
                type="button"
                onclick="this.closest('.massa-card').remove()">
                - Remover
            </button>
        </div>
		
		<input type="hidden" class="massaIdHidden" value="${idMassa}">

        <label>Arquivo da Massa</label>
        <input class="form-control fileMassa" type="file">

        <label class="form-label mt-2">Observação</label>
        <div class="quillMassa" style="height:120px;background:white;"></div>
        <input type="hidden" class="obsMassaHidden">
    `;

	container.appendChild(massa);

	/* ===== QUILL ===== */
	const quillDiv = massa.querySelector(".quillMassa");
	const quill = new Quill(quillDiv, { theme: "snow" });
	massa._quillMassa = quill;

	quill.on("text-change", () => {
		massa.querySelector(".obsMassaHidden").value =
			quill.root.innerHTML;
	});

	 /*===== INPUT FILE CHANGE ===== */
	const fileInput = massa.querySelector(".fileMassa");
	const nomeSpan = massa.querySelector(".nomeArquivoMassa");

	fileInput.addEventListener("change", () => {
		if (fileInput.files.length > 0) {
			/*card._arquivoAlterado = true;*/
			massa._arquivoAlterado = true;
			nomeSpan.textContent = `Novo arquivo: ${fileInput.files[0].name}`;
			nomeSpan.classList.remove("text-primary");
			nomeSpan.onclick = null;
		}
	});

	return massa;
};

/**************************************************
 * Limpar Layouts
 **************************************************/
window.limparLayoutsTela = function() {
	const container = document.getElementById("inputsLayoutContainer");
	if (container) container.innerHTML = "";
};

/**************************************************
 * Preencher TI (flags)
 **************************************************/
window.preencherTI = function(dto) {

	const hasLayout = document.getElementById("hasLayout");
	const layoutDiv = document.getElementById("inputLayoutDiv");
	const envioServico = document.getElementById("envioServico");
	const envioTxt = document.getElementById("envioTxt");

	const possuiLayouts = dto.layouts && dto.layouts.length > 0;

	// força layout se veio do banco
	if (hasLayout) {
		hasLayout.checked = !!dto.temLayout || possuiLayouts;
	}

	if (layoutDiv) {
		layoutDiv.style.display =
			(dto.temLayout || possuiLayouts) ? "block" : "none";
	}

	if (envioServico) envioServico.checked = !!dto.viaServico;
	if (envioTxt) envioTxt.checked = !!dto.viaTxt;
};

window.carregarLayouts = function(layouts) {

	if (!layouts || layouts.length === 0) return;

	layouts.forEach(layout => {

		const layoutCard = window.criarLayoutCard();
		
		const idHidden = layoutCard.querySelector(".layoutIdHidden");
		if (idHidden && layout.id) {
		    idHidden.value = layout.id;
		}
		
		if (layout.massasDados && layout.massasDados.length > 0) {
			layout.massasDados.forEach(massa => {
				
				const idHiddenMassa = layoutCard.querySelector(".massaIdHidden");
				if (idHiddenMassa && massa.id) {
					idHiddenMassa.value = massa.id;
				}

			});
		}
	
		 /*=================================================
		 * NOME DO ARQUIVO — LINK SÓ NA EDIÇÃO
		 * ================================================= */
		const nomeSpan = layoutCard.querySelector(".nomeArquivoLayout");

		if (nomeSpan && layout.nomeLayout && window.modoEdicao && layout.id) {

			// cria o link
			const link = document.createElement("a");
			link.href = `${API}layouts/${layout.id}/download`;
			link.className = "nomeArquivoLayout text-primary text-decoration-none";
			link.style.cursor = "pointer";

			link.innerHTML =
				`<i class="bi bi-file-earmark-text me-1"></i> ${layout.nomeLayout}`;

			// substituição segura (remove span e adiciona link)
			const parent = nomeSpan.parentNode;
			parent.insertBefore(link, nomeSpan);
			parent.removeChild(nomeSpan);
		}
		else if (nomeSpan && layout.nomeLayout) {
			// modo novo → apenas texto
			nomeSpan.innerHTML =
				`<i class="bi bi-file-earmark-text me-1"></i> ${layout.nomeLayout}`;
		}

		if (layoutCard._quillLayout && layout.observacao) {
			layoutCard._quillLayout.root.innerHTML = layout.observacao;
			layoutCard.querySelector(".obsLayoutHidden").value =
				layout.observacao;
		}

		if (layout.massasDados && layout.massasDados.length > 0) {

			layout.massasDados.forEach(massa => {

				const fakeBtn = { closest: () => layoutCard };
				const massaCard = window.addMassa(fakeBtn,massa);

				const nomeSpan = massaCard.querySelector(".nomeArquivoMassa");

				if (nomeSpan && massa.nomeMassaDados) {

					// texto base (sempre)
					nomeSpan.innerHTML =
						`<i class="bi bi-file-earmark-text me-1"></i> ${massa.nomeMassaDados}`;

					// 👉 SOMENTE NA EDIÇÃO cria LINK REAL
					if (
						window.modoEdicao &&
						massa.id &&
						!massaCard._arquivoAlterado
					) {
						const link = document.createElement("a");
						link.href = `${API}massas/${massa.id}/download`;
						link.className =
							"nomeArquivoMassa text-primary text-decoration-none";
						link.style.cursor = "pointer";

						link.innerHTML =
							`<i class="bi bi-file-earmark-text me-1"></i> ${massa.nomeMassaDados}`;

						// substitui o span pelo link
						const parent = nomeSpan.parentNode;
						parent.insertBefore(link, nomeSpan);
						parent.removeChild(nomeSpan);
					}
				}

				 /*===== OBSERVAÇÃO ===== */
				if (massaCard._quillMassa && massa.observacao) {
					massaCard._quillMassa.root.innerHTML = massa.observacao;
					massaCard.querySelector(".obsMassaHidden").value =
						massa.observacao;
				}
			});
		}
	});
};


/**************************************************
 * Limpar TI COMPLETO
 **************************************************/
window.limparTI = function() {

	const hasLayout = document.getElementById("hasLayout");
	const layoutDiv = document.getElementById("inputLayoutDiv");
	const envioServico = document.getElementById("envioServico");
	const envioTxt = document.getElementById("envioTxt");

	if (hasLayout) hasLayout.checked = false;
	if (layoutDiv) layoutDiv.style.display = "none";
	if (envioServico) envioServico.checked = false;
	if (envioTxt) envioTxt.checked = false;

	limparLayoutsTela();
};

/***************************************************
 * BUILD TI (Layouts + Massas + Arquivos)
 ***************************************************/
window.buildTI = function () {

    const layouts = [];
    const filesLayout = [];
    const filesMassas = [];

    const viaServico = document.getElementById("envioServico")?.checked || false;
    const viaTxt = document.getElementById("envioTxt")?.checked || false;

    document.querySelectorAll(".layout-card").forEach(layoutCard => {

		const idLayout = Number(layoutCard.querySelector(".layoutIdHidden")?.value) || 0;
        const temArquivoLayout = layoutCard._arquivoAlterado === true;
		
		const obsLayout =
            layoutCard.querySelector(".obsLayoutHidden")?.value || "";

        const fileLayoutInput =
            layoutCard.querySelector(".fileLayout");

        if (temArquivoLayout && fileLayoutInput?.files?.length > 0) {
            filesLayout.push(fileLayoutInput.files[0]);
        }

        const massas = [];

        layoutCard.querySelectorAll(".massa-card").forEach(massaCard => {
			
			const idMassa = Number(massaCard.querySelector(".massaIdHidden")?.value) || 0;
			
            const temArquivoMassa = massaCard._arquivoAlterado === true;
			
            const obsMassa =
                massaCard.querySelector(".obsMassaHidden")?.value || "";

            const fileMassaInput =
                massaCard.querySelector(".fileMassa");

            if (temArquivoMassa && fileMassaInput?.files?.length > 0) {
                filesMassas.push(fileMassaInput.files[0]);
            }

            massas.push({
                id: idMassa,
                observacao: obsMassa,
                temArquivo: temArquivoMassa
            });
        });

        layouts.push({
            id: idLayout,
            observacao: obsLayout,
            temArquivo: temArquivoLayout,
            massasDados: massas
        });
    });

    return {
        viaServico,
        viaTxt,
        layouts,
        _filesLayout: filesLayout,
        _filesMassas: filesMassas
    };
};

// Validação da aba TI (layouts)
window.validarTi = function() {
	const erros = [];

	const hasLayout = document.getElementById("hasLayout")?.checked;

	// Se não tem layout marcado, ignoramos validação dessa parte
	if (!hasLayout) {
		return erros;
	}

	const layoutCards = document.querySelectorAll(".layout-card");

	// Pelo menos um layout se checkbox marcado
	if (layoutCards.length === 0) {
		erros.push("• Marcou 'Tem layout?', mas não adicionou nenhum layout.");
		return erros;
	}

	// Validação por layout
	layoutCards.forEach((card, layoutIndex) => {
		const numLayout = layoutIndex + 1;

		// arquivo do layout obrigatório
		const fileLayout = card.querySelector(".fileLayout");
		const nomeArquivoLayoutLink = card.querySelector(".nomeArquivoLayout");

		const hasFile =
			fileLayout?.files?.length > 0;

		// Como agora é <a>, usamos textContent
		const hasArquivoExistente =
			(nomeArquivoLayoutLink?.textContent || "").trim() !== "";

		if (!hasFile && !hasArquivoExistente) {
			erros.push(`• Selecione o arquivo do Layout ${numLayout}.`);
		}
		

		// massas dentro deste layout
		const massaCards = card.querySelectorAll(".massa-card");

		// Agora regra nova:
		// Massa só é obrigatória SE usuario adicionou pelo menos uma
		massaCards.forEach((massaCard, massaIndex) => {
			const fileMassa = massaCard.querySelector(".fileMassa");
			const numMassa = massaIndex + 1;

			if (!hasArquivoExistente && fileMassa.files.length === 0) {
				erros.push(
					`• Massa ${numMassa} do Layout ${numLayout} está sem arquivo.`
				);
			}
		});
	});

	return erros;
};