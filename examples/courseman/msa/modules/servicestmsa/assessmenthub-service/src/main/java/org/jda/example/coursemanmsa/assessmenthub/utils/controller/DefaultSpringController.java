package org.jda.example.coursemanmsa.assessmenthub.utils.controller;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

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

    protected <U> PagingAndSortingRepository<U, ID> getServiceOfGenericType(Class<U> cls) {
        return ServiceRegistry.getInstance().get(cls.getSimpleName());
    }

    @Override
    public T createEntity(T inputEntity) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        T createdEntity = service.save(inputEntity);
        return createdEntity;
    }

    protected <X> PagingAndSortingRepository<X, ID> getServiceOfGenericType(String clsName) {
        return ServiceRegistry.getInstance().get(clsName);
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
