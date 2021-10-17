package jda.modules.mosar.backend.base.services;

import java.util.Collection;
import java.util.function.BiConsumer;

import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;

public interface CrudService<T> {
    T createEntity(T entity);
    T getEntityById(Identifier<?> id);
    Page<T> getEntityListByPage(PagingModel pagingModel);
    Collection<T> getAllEntities();
    T updateEntity(Identifier<?> id, T entity);
    void deleteEntityById(Identifier<?> id);

    void setOnCascadeUpdate(BiConsumer<Identifier, Object> handler);
}
