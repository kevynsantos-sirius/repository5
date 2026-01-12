/***************************************************
 * CONTROLE DE INICIALIZAÇÃO (ANTI DUPLICAÇÃO)
 ***************************************************/
window.tiEventsInicializados = false;

/***************************************************
 * INIT TI (executa apenas uma vez)
 ***************************************************/
document.addEventListener("DOMContentLoaded", () => {
    initTiEvents();
});

/***************************************************
 * Eventos iniciais TI
 ***************************************************/
function initTiEvents() {

    if (window.tiEventsInicializados) return;
    window.tiEventsInicializados = true;

    const chkHasLayout = document.getElementById("hasLayout");
    const layoutDiv    = document.getElementById("inputLayoutDiv");
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
        // ⚠️ onclick substitui handler anterior (não duplica)
        btnAddLayout.onclick = () => window.criarLayoutCard();
    }
}

/***************************************************
 * Criar Layout Card
 ***************************************************/
window.criarLayoutCard = function () {

    const layoutContainer = document.getElementById("inputsLayoutContainer");

    const card = document.createElement("div");
    card.className = "card p-4 mb-3 layout-card";

    card.innerHTML = `
        <div class="d-flex justify-content-end mb-2">
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

    /* 🔥 CRIA O QUILL AGORA (SEM TIMEOUT) */
    const quillLayoutDiv = card.querySelector(".quillLayout");
    const quillLayout = new Quill(quillLayoutDiv, { theme: "snow" });

    /* guarda a instância no próprio card */
    card._quillLayout = quillLayout;

    quillLayout.on("text-change", () => {
        card.querySelector(".obsLayoutHidden").value =
            quillLayout.root.innerHTML;
    });

    return card;
};


/***************************************************
 * Adicionar Massa
 ***************************************************/
window.addMassa = function (btn) {

    const layoutCard = btn.closest(".layout-card");
    const container  = layoutCard.querySelector(".massasContainer");

    const massa = document.createElement("div");
    massa.className = "card p-2 mb-2 massa-card";

    massa.innerHTML = `
        <label>Arquivo da Massa</label>
        <input class="form-control fileMassa" type="file">

        <label class="form-label mt-2">Observação</label>
        <div class="quillMassa" style="height:120px;background:white;"></div>
        <input type="hidden" class="obsMassaHidden">

        <div class="d-flex justify-content-end">
            <button class="btn btn-sm btn-outline-danger"
                type="button"
                onclick="this.closest('.massa-card').remove()">
                - Remover
            </button>
        </div>
    `;

    container.appendChild(massa);

    const quillMassaDiv = massa.querySelector(".quillMassa");
    const quillMassa = new Quill(quillMassaDiv, { theme: "snow" });

    massa._quillMassa = quillMassa;

    quillMassa.on("text-change", () => {
        massa.querySelector(".obsMassaHidden").value =
            quillMassa.root.innerHTML;
    });
};


/***************************************************
 * Limpar Layouts
 ***************************************************/
window.limparLayoutsTela = function () {
    const container = document.getElementById("inputsLayoutContainer");
    if (container) container.innerHTML = "";
};

/***************************************************
 * Preencher TI (flags)
 ***************************************************/
window.preencherTI = function (dto) {

    const hasLayout = document.getElementById("hasLayout");
    const layoutDiv = document.getElementById("inputLayoutDiv");
    const envioServico = document.getElementById("envioServico");
    const envioTxt     = document.getElementById("envioTxt");

    if (hasLayout) hasLayout.checked = !!dto.temLayout;
    if (layoutDiv) layoutDiv.style.display = dto.temLayout ? "block" : "none";
    if (envioServico) envioServico.checked = !!dto.viaServico;
    if (envioTxt) envioTxt.checked = !!dto.viaTxt;
};

window.carregarLayouts = function (layouts) {

    if (!layouts || layouts.length === 0) return;

    layouts.forEach(layout => {

        const layoutCard = window.criarLayoutCard();

        /* OBS LAYOUT */
        if (layoutCard._quillLayout && layout.observacao) {
            layoutCard._quillLayout.root.innerHTML = layout.observacao;
            layoutCard.querySelector(".obsLayoutHidden").value =
                layout.observacao;
        }

        /* MASSAS */
        if (layout.massasDados) {
            layout.massasDados.forEach(massa => {

                const fakeBtn = { closest: () => layoutCard };
                window.addMassa(fakeBtn);

                const ultimaMassa =
                    layoutCard.querySelector(".massa-card:last-child");

                if (ultimaMassa && ultimaMassa._quillMassa && massa.observacao) {
                    ultimaMassa._quillMassa.root.innerHTML = massa.observacao;
                    ultimaMassa.querySelector(".obsMassaHidden").value =
                        massa.observacao;
                }
            });
        }
    });
};


/***************************************************
 * Limpar TI COMPLETO
 ***************************************************/
window.limparTI = function () {

    const hasLayout = document.getElementById("hasLayout");
    const layoutDiv = document.getElementById("inputLayoutDiv");
    const envioServico = document.getElementById("envioServico");
    const envioTxt     = document.getElementById("envioTxt");

    if (hasLayout) hasLayout.checked = false;
    if (layoutDiv) layoutDiv.style.display = "none";
    if (envioServico) envioServico.checked = false;
    if (envioTxt) envioTxt.checked = false;

    limparLayoutsTela();
};

/***************************************************
 * BUILD TI (JSON + arquivos para backend)
 ***************************************************/
window.buildTI = function () {

    const hasLayout    = document.getElementById("hasLayout");
    const envioServico = document.getElementById("envioServico");
    const envioTxt     = document.getElementById("envioTxt");

    const layouts = [];
    const filesLayout = [];
    const filesMassas = [];

    document.querySelectorAll(".layout-card").forEach(layoutCard => {

        const layoutFileInput = layoutCard.querySelector(".fileLayout");
        if (layoutFileInput && layoutFileInput.files.length > 0) {
            filesLayout.push(layoutFileInput.files[0]);
        }

        const obsLayout =
            layoutCard.querySelector(".obsLayoutHidden")?.value || "";

        const massas = [];

        layoutCard.querySelectorAll(".massa-card").forEach(massaCard => {

            const massaFileInput = massaCard.querySelector(".fileMassa");
            if (massaFileInput && massaFileInput.files.length > 0) {
                filesMassas.push(massaFileInput.files[0]);
            }

            const obsMassa =
                massaCard.querySelector(".obsMassaHidden")?.value || "";

            massas.push({
                observacao: obsMassa
            });
        });

        layouts.push({
            observacao: obsLayout,
            massasDados: massas
        });
    });

    return {
        temLayout: hasLayout?.checked || false,
        viaServico: envioServico?.checked || false,
        viaTxt: envioTxt?.checked || false,
        layouts: layouts,

        // campos internos (removidos antes do envio)
        _filesLayout: filesLayout,
        _filesMassas: filesMassas
    };
};
// Validação da aba TI (layouts)
window.validarTi = function () {
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
        if (!fileLayout || fileLayout.files.length === 0) {
            erros.push(`• Selecione o arquivo do Layout ${numLayout}.`);
        }

        // massas dentro deste layout
        const massaCards = card.querySelectorAll(".massa-card");

        // Agora regra nova:
        // Massa só é obrigatória SE usuario adicionou pelo menos uma
        massaCards.forEach((massaCard, massaIndex) => {
            const fileMassa = massaCard.querySelector(".fileMassa");
            const numMassa = massaIndex + 1;

            if (!fileMassa || fileMassa.files.length === 0) {
                erros.push(
                    `• Massa ${numMassa} do Layout ${numLayout} está sem arquivo.`
                );
            }
        });
    });

    return erros;
};