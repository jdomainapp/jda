package jda.modules.mosar.backend.base.controllers;

import jda.modules.mosar.backend.annotations.PagingCondition;
import jda.modules.mosar.backend.annotations.ResourceController;
import jda.modules.mosar.backend.annotations.Retrieve;
import jda.modules.mosar.backend.annotations.Subtype;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;

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
