package org.jda.example.coursemanmsa.assessmenthub.utils.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Represent a RESTful Web API endpoint.
 * Operations are grouped as C-R-U-D
 * @param T the generic type
 */
public interface SpringController<T, ID> {
    /**
     * Create a new entity based on submitted input.
     * @param inputEntity
     * @return the persisted entity
     */
    T createEntity( T inputEntity);

    /**
     * Retrieve a paginated list of entities of type T.
     * @param pageNumber
     * @param count
     */
    Page<T> getEntityListByPage(Pageable pagingModel);

    /**
     * Retrieve an entity instance by its identifier.
     * @param id
     */
    T getEntityById(ID id);

    /**
     * Update an entity instance
     * @param id
     * @param updatedInstance
     */
    T updateEntity(ID id, T updatedInstance);


    /**
     * Delete an entity instance
     * @param id
     */
    void deleteEntityById(ID id);
}
