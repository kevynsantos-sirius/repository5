package com.totaldocs.dto;

import java.util.List;

import com.totaldocs.dto.ChecklistVersaoDTO;

public class ChecklistPaginadoResponse {

    private List<ChecklistVersaoDTO> content;
    private int page;
    private int totalPages;
    private long totalElements;

    public ChecklistPaginadoResponse(
            List<ChecklistVersaoDTO> content,
            int page,
            int totalPages,
            long totalElements
    ) {
        this.content = content;
        this.page = page;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<ChecklistVersaoDTO> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
