package com.totaldocs.service;

import com.totaldocs.dto.ChecklistDTO;
import com.totaldocs.dto.ChecklistVersaoDTO;
import com.totaldocs.dto.LayoutDTO;
import com.totaldocs.dto.MassaDTO;
import com.totaldocs.dto.PageResponse;
import com.totaldocs.modelo.Checklist;
import com.totaldocs.modelo.ChecklistVersao;
import com.totaldocs.modelo.Layout;
import com.totaldocs.modelo.MassaDados;
import com.totaldocs.repository.LayoutRepository;
import com.totaldocs.repository.MassaDadoRepository;
import com.totaldocs.utils.TemporalCryptoIdUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChecklistVersaoService {

    @Value("${backend.api.base-url}" + "Checklists")
    private String API_URL;

    @Autowired
    private LayoutRepository layoutRepository;

    @Autowired
    private MassaDadoRepository massaDadoRepository;

    @Autowired
    private TemporalCryptoIdUtil temporalCryptoIdUtil;

    public Checklist salvarChecklist(ChecklistVersao checklistVersao) {

        try {

            RestTemplate restTemplate = new RestTemplate();

            LocalDateTime dataSistema = LocalDateTime.now();

            checklistVersao.setDataAtualizacao(dataSistema);
            checklistVersao.setDataCadastro(dataSistema);

            return restTemplate.postForObject(
                    API_URL,
                    checklistVersao,
                    Checklist.class
            );

        } catch (Exception e) {

            System.err.println("Erro ao salvar Checklist: " + e.getMessage());

            e.printStackTrace();

            throw new RuntimeException("Erro ao salvar Checklist", e);
        }
    }

    public PageResponse<ChecklistDTO> listarPaginado(int page, int size) {

        RestTemplate restTemplate = new RestTemplate();

        String url = API_URL + "/page?page=" + page + "&size=" + size;

        ResponseEntity<PageResponse<ChecklistDTO>> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<PageResponse<ChecklistDTO>>() {}
                );

        return response.getBody();
    }

    private Layout buscarArquivoLayout(
            String layoutIdStr,
            Pair<String, Integer> controle
    ) {

        Integer layoutId = temporalCryptoIdUtil.extractId(
                layoutIdStr,
                controle
        );

        return layoutRepository.findById(layoutId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Layout não encontrado id " + layoutId
                        )
                );
    }

    private MassaDados buscarArquivoMassa(
            String massaIdStr,
            Pair<String, Integer> controle
    ) {

        Integer massaId = temporalCryptoIdUtil.extractId(
                massaIdStr,
                controle
        );

        return massaDadoRepository.findById(massaId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Massa não encontrada id " + massaId
                        )
                );
    }

    private String sanitize(String nome) {

        if (nome == null) {
            return "sem-nome";
        }

        return nome.replaceAll("[^a-zA-Z0-9-_\\.]", "_");
    }

    public byte[] generateZipFromCheckList(
            ChecklistVersaoDTO checklist,
            Pair<String, Integer> controle
    ) throws IOException {

        try (
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ZipOutputStream zos = new ZipOutputStream(baos)
        ) {

            String rootFolder = nameFolderOrZip(checklist, false);

            /*
             * =========================================================
             * HTML
             * =========================================================
             */

            String html = gerarHtmlChecklist(checklist);

            addBytesToZip(
                    zos,
                    html.getBytes(StandardCharsets.UTF_8),
                    rootFolder + "index.html"
            );

            /*
             * =========================================================
             * LAYOUTS
             * =========================================================
             */

            String layoutsRoot = rootFolder + "layouts/";

            criarPasta(zos, layoutsRoot);

            if (checklist.getLayouts() != null) {

                for (LayoutDTO layout : checklist.getLayouts()) {

                    boolean temMassas =
                            layout.getMassasDados() != null
                                    && !layout.getMassasDados().isEmpty();

                    String layoutNome =
                            sanitize(layout.getNomeLayout());

                    Layout layoutEntity =
                            buscarArquivoLayout(
                                    layout.getId(),
                                    controle
                            );

                    byte[] conteudoLayout =
                            layoutEntity.getConteudoLayout();

                    /*
                     * =====================================================
                     * Layout SEM massas
                     * =====================================================
                     */

                    if (!temMassas) {

                        if (conteudoLayout != null
                                && conteudoLayout.length > 0) {

                            String nomeArquivo =
                                    sanitize(
                                            layoutEntity.getNomeLayout()
                                    );

                            addBytesToZip(
                                    zos,
                                    conteudoLayout,
                                    layoutsRoot + nomeArquivo
                            );
                        }
                    }

                    /*
                     * =====================================================
                     * Layout COM massas
                     * =====================================================
                     */

                    if (temMassas) {

                        String layoutFolder =
                                layoutsRoot + layoutNome + "/";

                        criarPasta(zos, layoutFolder);

                        /*
                         * Arquivo layout
                         */

                        if (conteudoLayout != null
                                && conteudoLayout.length > 0) {

                            String nomeArquivo =
                                    sanitize(
                                            layoutEntity.getNomeLayout()
                                    );

                            addBytesToZip(
                                    zos,
                                    conteudoLayout,
                                    layoutFolder + nomeArquivo
                            );
                        }

                        /*
                         * Pasta massas
                         */

                        String massasFolder =
                                layoutFolder + "massas/";

                        criarPasta(zos, massasFolder);

                        /*
                         * Arquivos massas
                         */

                        for (MassaDTO massa : layout.getMassasDados()) {

                            MassaDados massaEntity =
                                    buscarArquivoMassa(
                                            massa.getId(),
                                            controle
                                    );

                            byte[] conteudoMassa =
                                    massaEntity.getConteudoMassaDados();

                            if (conteudoMassa != null
                                    && conteudoMassa.length > 0) {

                                String nomeArquivo =
                                        sanitize(
                                                massaEntity.getNomeMassaDados()
                                        );

                                addBytesToZip(
                                        zos,
                                        conteudoMassa,
                                        massasFolder + nomeArquivo
                                );
                            }
                        }
                    }
                }
            }

            /*
             * =========================================================
             * NOVAS PASTAS
             * =========================================================
             */

            String logosFolder = rootFolder + "logos/";
            criarPasta(zos, logosFolder);

            String assinaturasFolder =
                    rootFolder + "assinaturas/";
            criarPasta(zos, assinaturasFolder);

            String modeloDocumentoFolder =
                    rootFolder + "modelo-documento/";
            criarPasta(zos, modeloDocumentoFolder);

            String planoComunicacaoFolder =
                    rootFolder + "plano-comunicacao/";
            criarPasta(zos, planoComunicacaoFolder);

            String arquivosAdicionaisFolder =
                    rootFolder + "arquivos-adicionais/";
            criarPasta(zos, arquivosAdicionaisFolder);

            /*
             * =========================================================
             * EXEMPLOS DE USO
             * =========================================================
             *
             * Você vai adaptar conforme os campos do seu DTO
             *
             * Exemplo:
             *
             * adicionarArquivo(
             *      zos,
             *      logosFolder,
             *      checklist.getLogo().getNomeArquivo(),
             *      checklist.getLogo().getConteudo()
             * );
             *
             */

            zos.finish();

            return baos.toByteArray();
        }
    }

    public String nameFolderOrZip(
            ChecklistVersaoDTO checklist,
            boolean isZip
    ) {

        LocalDateTime agora = LocalDateTime.now();

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern(
                        "yyyy-MM-dd_HH-mm-ss"
                );

        String dataHoraFormatada =
                agora.format(formatter);

        String folderOrNot = isZip ? "" : "/";

        return "checklist-"
                + sanitize(checklist.getNomeDocumento())
                + "-"
                + dataHoraFormatada
                + folderOrNot;
    }

    private void criarPasta(
            ZipOutputStream zos,
            String path
    ) throws IOException {

        zos.putNextEntry(new ZipEntry(path));
        zos.closeEntry();
    }

    private void addBytesToZip(
            ZipOutputStream zos,
            byte[] fileBytes,
            String zipPath
    ) throws IOException {

        zos.putNextEntry(new ZipEntry(zipPath));
        zos.write(fileBytes);
        zos.closeEntry();
    }

    private String gerarHtmlChecklist(
            ChecklistVersaoDTO checklist
    ) {

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Checklist</title>

                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            padding: 24px;
                        }

                        h1 {
                            color: #222;
                        }

                        h2 {
                            margin-top: 24px;
                        }

                        ul {
                            padding-left: 20px;
                        }

                        li {
                            margin-bottom: 8px;
                        }

                        .layout {
                            border: 1px solid #ddd;
                            padding: 12px;
                            border-radius: 8px;
                            margin-bottom: 16px;
                        }
                    </style>
                </head>
                <body>
                """);

        html.append("<h1>")
                .append(checklist.getNomeDocumento())
                .append("</h1>");

        html.append("<p>")
                .append("Checklist exportado em ")
                .append(LocalDateTime.now())
                .append("</p>");

        html.append("<h2>Layouts</h2>");

        if (checklist.getLayouts() != null
                && !checklist.getLayouts().isEmpty()) {

            for (LayoutDTO layout : checklist.getLayouts()) {

                html.append("<div class='layout'>");

                html.append("<strong>Layout:</strong> ")
                        .append(layout.getNomeLayout());

                if (layout.getMassasDados() != null
                        && !layout.getMassasDados().isEmpty()) {

                    html.append("<h4>Massas de Dados</h4>");
                    html.append("<ul>");

                    for (MassaDTO massa : layout.getMassasDados()) {

                        html.append("<li>")
                                .append(massa.getNomeMassaDados())
                                .append("</li>");
                    }

                    html.append("</ul>");
                }

                html.append("</div>");
            }

        } else {

            html.append("<p>Nenhum layout encontrado.</p>");
        }

        html.append("""
                </body>
                </html>
                """);

        return html.toString();
    }
}