package com.sellspark.SellsHRMS.utils;

import com.sellspark.SellsHRMS.dto.common.PagedResponse;
import com.sellspark.SellsHRMS.dto.common.PaginationMeta;

import java.util.Collections;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;

public class PaginationUtils {

        public static <T> PagedResponse<T> toPagedResponse(Page<T> page) {

                if (page == null) {
                        return new PagedResponse<>(Collections.emptyList(),
                                        new PaginationMeta(0, 0, 0, 0, true, true, "unsorted"));
                }

                String sortDesc = page.getSort().stream()
                                .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                                .collect(Collectors.joining(";"));
                if (sortDesc.isEmpty())
                        sortDesc = "unsorted";

                PaginationMeta meta = PaginationMeta.builder()
                                .page(page.getNumber())
                                .size(page.getSize())
                                .totalElements(page.getTotalElements())
                                .totalPages(page.getTotalPages())
                                .first(page.isFirst())
                                .last(page.isLast())
                                .sort(sortDesc)
                                .build();

                return PagedResponse.<T>builder()
                                .content(page.getContent())
                                .meta(meta)
                                .build();
        }
}
