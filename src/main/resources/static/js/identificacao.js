/***************************************************
 * RESET do formulário de identificação
 ***************************************************/
window.limparIdentificacao = function () {

    const form = document.getElementById("formIdentificacao");

    if (!form) {
        console.warn("⚠ Formulário Identificação não está no DOM.");
        return;
    }

    console.log("🔄 Limpando Identificação...");

    // Limpa todos os campos automaticamente
    form.reset();

    // Força o select de Ramo voltar ao primeiro
    const ramo = document.getElementById("ramo");
    if (ramo) ramo.selectedIndex = 0;
}

// Constrói o objeto da aba Identificação
window.buildIdentificacao = function () {

    return {
        nomeDocumento: document.getElementById("nomeDocumento")?.value || "",
        centroCusto: document.getElementById("centroCusto")?.value || "",
        idRamo: document.getElementById("ramo")?.value || "",
        status: document.getElementById("statusDocumento")?.value || "",
        idUsuario: document.getElementById("responsavel")?.value || "",
        icatu: document.getElementById("isIcatu")?.checked || false,
        caixa: document.getElementById("isCaixa")?.checked || false,
        rioGrande: document.getElementById("isRG")?.checked || false
    };
};

