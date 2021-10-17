package jda.modules.mosar.backend.base.models;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.QueryParam;

public final class PagingModel {
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_COUNT = 20;
    @QueryParam("page") @DefaultValue("1")
    private int page;
    @QueryParam("count") @DefaultValue("20")
    private int count;

    public PagingModel() {
        this(DEFAULT_PAGE, DEFAULT_COUNT);
    }

    public PagingModel(int page, int count) {
        this.page = page;
        this.count = count;
    }

    public int getPage() {
        return page;
    }

    public int getCount() {
        return count;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "Page(p=" + page + ",count=" + count + ")";
    }
}
