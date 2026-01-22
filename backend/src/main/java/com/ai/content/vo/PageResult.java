package com.ai.content.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * Pagination Response wrapper
 *
 * @param <T> List item type
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {

    private List<T> list;
    private long total;
    private int page;
    private int pageSize;

    public static <T> PageResult<T> of(List<T> list, long total, int page, int pageSize) {
        return PageResult.<T>builder().list(list).total(total).page(page).pageSize(pageSize).build();
    }

    public static <T> PageResult<T> empty() {
        return PageResult.<T>builder().list(Collections.emptyList()).total(0).page(1).pageSize(10).build();
    }

}
