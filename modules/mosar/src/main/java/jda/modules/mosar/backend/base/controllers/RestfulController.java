package jda.modules.mosar.backend.base.controllers;

import jda.modules.mosar.backend.annotations.Create;
import jda.modules.mosar.backend.annotations.Delete;
import jda.modules.mosar.backend.annotations.ID;
import jda.modules.mosar.backend.annotations.Modifying;
import jda.modules.mosar.backend.annotations.PagingCondition;
import jda.modules.mosar.backend.annotations.ResourceController;
import jda.modules.mosar.backend.annotations.Retrieve;
import jda.modules.mosar.backend.annotations.Update;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;

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
