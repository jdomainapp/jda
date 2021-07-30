package jda.modules.restfstool.backend.base.controllers;

import jda.modules.restfstool.backend.annotations.PagingCondition;
import jda.modules.restfstool.backend.annotations.ResourceController;
import jda.modules.restfstool.backend.annotations.Retrieve;
import jda.modules.restfstool.backend.annotations.Subtype;
import jda.modules.restfstool.backend.base.models.Page;
import jda.modules.restfstool.backend.base.models.PagingModel;

@ResourceController
public interface RestfulWithInheritanceController<T>
        extends RestfulController<T> {
    /**
     * Retrieve a paginated list of entities of type T by one of its subtype
     * (if specified).
     * @param page
     * @param count
     */
    @Retrieve
    Page<T> getEntityListByTypeAndPage(
        @Subtype String type, @PagingCondition PagingModel pagingModel);

    /**
     * Retrieve a paginated list of entities of type T.
     * @param pageNumber
     * @param count
     */
    @Retrieve(ignored = true)
    @Override
    Page<T> getEntityListByPage(@PagingCondition PagingModel pagingModel);
}
