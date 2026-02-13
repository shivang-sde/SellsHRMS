package com.sellspark.SellsHRMS.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationMeta {
    private int page; // current page number
    private int size; // size per page
    private long totalElements; // total records
    private int totalPages; // total pages
    private boolean first; // is first page?
    private boolean last; // is last page?
    private String sort; // e.g. "creditedAt,desc"
}
