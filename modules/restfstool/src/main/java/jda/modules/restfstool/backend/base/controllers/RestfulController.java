package jda.modules.restfstool.backend.base.controllers;

import jda.modules.restfstool.backend.annotations.*;
import jda.modules.restfstool.backend.base.models.Identifier;
import jda.modules.restfstool.backend.base.models.Page;
import jda.modules.restfstool.backend.base.models.PagingModel;

/**
 * Represent a RESTful Web API endpoint.
 * Operations are grouped as C-R-U-D
 * @param T the generic type
 */
@ResourceController
public interface RestfulController<T> {
    /**
     * Create a new entity based on submitted input.
     * @param inputEntity
     * @return the persisted entity
     */
    @Create
    T createEntity(@Modifying T inputEntity);

    /**
     * Retrieve a paginated list of entities of type T.
     * @param pageNumber
     * @param count
     */
    @Retrieve
    Page<T> getEntityListByPage(@PagingCondition PagingModel pagingModel);

    /**
     * Retrieve an entity instance by its identifier.
     * @param id
     */
    @Retrieve(byId = true)
    T getEntityById(@ID Identifier<?> id);

    /**
     * Update an entity instance
     * @param id
     * @param updatedInstance
     */
    @Update(byId = true)
    T updateEntity(@ID Identifier<?> id, @Modifying T updatedInstance);


    /**
     * Delete an entity instance
     * @param id
     */
    @Delete(byId = true)
    void deleteEntityById(@ID Identifier<?> id);
}
