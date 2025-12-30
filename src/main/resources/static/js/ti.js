/***************************************************
 * CONSTRUIR OBJETO TI PARA O BACKEND
 ***************************************************/
window.buildTI = function () {

    const ti = {
        temLayout:  document.getElementById("hasLayout").checked,
        viaServico: document.getElementById("envioServico").checked,
        viaTxt:     document.getElementById("envioTxt").checked,
        layouts: [],
        _filesLayout: [],
        _filesMassas: []
    };

    // ============================
    // 1) LAYOUTS
    // ============================

    const layoutCards = document.querySelectorAll(".layout-card");

    layoutCards.forEach(card => {

        const layoutObj = {
            nomeLayout: card.querySelector(".nomeLayout")?.value || null,
            observacao: card.querySelector(".obsLayoutHidden")?.value || "",
            massasDados: []
        };

        // arquivos do layout
        const fileLayoutInput = card.querySelector(".fileLayout");
        if (fileLayoutInput && fileLayoutInput.files.length > 0) {
            Array.from(fileLayoutInput.files).forEach(f => ti._filesLayout.push(f));
        }

        // ============================
        // 2) MASSAS DENTRO DO LAYOUT
        // ============================
        const massaCards = card.querySelectorAll(".massa-card");

        massaCards.forEach(mc => {

            layoutObj.massasDados.push({
                observacao: mc.querySelector(".obsMassaHidden")?.value || ""
            });

            const fileMassaInput = mc.querySelector(".fileMassa");

            if (fileMassaInput && fileMassaInput.files.length > 0) {
                Array.from(fileMassaInput.files).forEach(f => ti._filesMassas.push(f));
            }
        });

        ti.layouts.push(layoutObj);
    });

    return ti;
};



/***************************************************
 * INICIALIZAR EVENTOS DO FORMULÁRIO TI
 ***************************************************/
window.initTiEvents = function () {

    console.log("⚙ TI carregado");

    const hasLayout       = document.getElementById("hasLayout");
    const layoutDiv       = document.getElementById("inputLayoutDiv");
    const layoutContainer = document.getElementById("inputsLayoutContainer");
    const btnAddLayout    = document.getElementById("btnAddLayout");

    if (!hasLayout || !layoutContainer) return;

    hasLayout.onchange = () => {
        layoutDiv.style.display = hasLayout.checked ? "block" : "none";
    };

    btnAddLayout.onclick = () => criarLayoutCard();

    /***************************************************
     * Criar um layout
     ***************************************************/
    function criarLayoutCard() {

        const card = document.createElement("div");
        card.className = "card p-4 mb-3 layout-card";

        card.innerHTML = `
            <div class="d-flex justify-content-end align-items-start mb-0">
                <button class="btn btn-sm btn-outline-danger" type="button"
                    onclick="this.closest('.layout-card').remove()">
                    - Remover
                </button>
            </div>

            <label>Layout</label>
            <input class="form-control fileLayout" type="file">

            <label class="form-label mt-2">Observação</label>
            <div class="quillLayout" style="height:120px;background:white;"></div>
            <input type="hidden" class="obsLayoutHidden">

            <hr>

            <div class="d-flex justify-content-between align-items-center">
                <h6 class="mb-0">Massas de Dados</h6>
                <button class="btn btn-sm btn-secondary" type="button" onclick="addMassa(this)">
                    + Adicionar Massa
                </button>
            </div>

            <div class="massasContainer mt-2"></div>
        `;

        layoutContainer.appendChild(card);

        // Inicializar Quill
        setTimeout(() => {
            const quill = new Quill(card.querySelector(".quillLayout"), { theme: "snow" });
            quill.on("text-change", () => {
                card.querySelector(".obsLayoutHidden").value = quill.root.innerHTML;
            });
        }, 0);
    }

    /***************************************************
     * Criar massa
     ***************************************************/
    window.addMassa = function (btn) {

        const layoutCard = btn.closest(".layout-card");
        const container = layoutCard.querySelector(".massasContainer");

        const massaCard = document.createElement("div");
        massaCard.className = "card p-2 mb-2 massa-card";

        massaCard.innerHTML = `
            <label>Arquivo da Massa</label>
            <input class="form-control fileMassa" type="file">

            <label class="form-label mt-2">Observação</label>
            <div class="quillMassa" style="height:120px;background:white;"></div>
            <input type="hidden" class="obsMassaHidden">

            <div class="d-flex justify-content-end">
                <button class="btn btn-sm btn-outline-danger" type="button"
                    onclick="this.closest('.massa-card').remove()">
                    - Remover
                </button>
            </div>
        `;

        container.appendChild(massaCard);

        // Inicializar Quill
        setTimeout(() => {
            const quill = new Quill(massaCard.querySelector(".quillMassa"), { theme: "snow" });
            quill.on("text-change", () => {
                massaCard.querySelector(".obsMassaHidden").value = quill.root.innerHTML;
            });
        }, 10);
    };
};

/***************************************************
 * Reset total do formulário TI
 ***************************************************/
function limparTI() {

    console.log("🔄 Limpando TI...");

    const hasLayout       = document.getElementById("hasLayout");
    const envioServico    = document.getElementById("envioServico");
    const envioTxt        = document.getElementById("envioTxt");
    const layoutDiv       = document.getElementById("inputLayoutDiv");
    const layoutContainer = document.getElementById("inputsLayoutContainer");

    if (!hasLayout) return;

    hasLayout.checked = false;
    envioServico.checked = false;
    envioTxt.checked = false;

    layoutContainer.innerHTML = "";
    layoutDiv.style.display = "none";
}
