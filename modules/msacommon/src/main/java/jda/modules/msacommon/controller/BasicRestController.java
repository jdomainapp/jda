package jda.modules.msacommon.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

/**
 * Represent a RESTful Web API endpoint.
 * Operations are grouped as C-R-U-D
 * @param T the generic type
 */
public interface BasicRestController<T, ID> extends IController<ID>{
    /**
     * Create a new entity based on submitted input.
     * @param inputEntity
     * @return the persisted entity
     */
	ResponseEntity<T> createEntity( T inputEntity);

    /**
     * Retrieve a paginated list of entities of type T.
     * @param pageNumber
     * @param count
     */
	ResponseEntity<Page<T>> getEntityListByPage(Pageable pagingModel);

    /**
     * Retrieve an entity instance by its identifier.
     * @param id
     */
	ResponseEntity<T> getEntityById(ID id);
	
    /**
     * Retrieve an entity instance by its identifier.
     * @param id
     */
	ResponseEntity<?> getDataById(ID id, String propertyName);

    /**
     * Update an entity instance
     * @param id
     * @param updatedInstance
     */
	ResponseEntity<T> updateEntity(ID id, T updatedInstance);


    /**
     * Delete an entity instance
     * @param id
     */
	ResponseEntity<String> deleteEntityById(ID id);
}
