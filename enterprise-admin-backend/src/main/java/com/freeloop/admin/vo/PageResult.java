package com.freeloop.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "分页查询结果")
public class PageResult<T> {
    @Schema(description = "当前页数据列表")
    private List<T> records;
    @Schema(
            description = "符合查询条件的总记录数",
            example = "25"
    )
    private long total;
    @Schema(
            description = "当前页码",
            example = "1"
    )
    private long page;
    @Schema(
            description = "每页数量",
            example = "10"
    )
    private long size;
    @Schema(
            description = "总页数",
            example = "3"
    )
    private long pages;

    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = records;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getPages() {
        return pages;
    }

    public void setPages(long pages) {
        this.pages = pages;
    }
}
