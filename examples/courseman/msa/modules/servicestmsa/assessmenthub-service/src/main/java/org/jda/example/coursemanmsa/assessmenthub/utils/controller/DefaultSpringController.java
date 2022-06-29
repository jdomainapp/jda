package org.jda.example.coursemanmsa.assessmenthub.utils.controller;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default implementation of {@link #RestfulController}
 */
@SuppressWarnings("unchecked")
public abstract class DefaultSpringController<T, ID> implements SpringController<T, ID> {

    private final Class<T> genericType =
        (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];

    protected Class<T> getGenericType() {
        return genericType;
    }

    protected <U> PagingAndSortingRepository<T, ID> getServiceOfGenericType(Class<U> cls) {
        return ServiceRegistry.getInstance().get(cls.getSimpleName());
    }
    

    protected <X> PagingAndSortingRepository<T, ID> getServiceOfGenericType(String clsName) {
        return ServiceRegistry.getInstance().get(clsName);
    }

    @Override
    public T createEntity(@RequestBody T inputEntity) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        T createdEntity = service.save(inputEntity);
        return createdEntity;
    }

    @Override
    public Page<T> getEntityListByPage(Pageable pagingModel) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        return service.findAll(pagingModel);
    }

    @Override
    public T getEntityById(ID id) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
    	Optional<T> opt = service.findById(id);
        return (opt.isPresent()) ? opt.get() : null;
    }

    @Override
    public T updateEntity(ID id, T updatedInstance) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        T updatedEntity = service.save(updatedInstance);
        return updatedEntity;
    }

    @Override
    public void deleteEntityById(ID id) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        service.deleteById(id);
    }

}
