package jda.modules.mosar.backend.base.models;

import java.util.Collection;

/**
 * Wrapper class to represent a page with currentPage and pageCount
 */
public final class Page<T> {
    private final Integer currentPage;
    private final Integer pageCount;
    private final Collection<T> content;

    public Page(Integer currentPage, Integer pageCount, Collection<T> content) {
        this.currentPage = currentPage;
        this.pageCount = pageCount;
        this.content = content;
    }

    public Collection<T> getContent() {
        return content;
    }

    public Integer getCurrentPage() {
        return currentPage;
    }

    public Integer getPageCount() {
        return pageCount;
    }

    public static <T> Page<T> empty() {
        return new Page<>(null, 0, null);
    }
}
