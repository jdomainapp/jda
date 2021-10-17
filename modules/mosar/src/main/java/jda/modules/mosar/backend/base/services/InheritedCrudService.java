package jda.modules.mosar.backend.base.services;

import java.util.Collection;

import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;

public interface InheritedCrudService<T>
        extends CrudService<T> {
    Collection<T> getEntityListByType(String type);
    Page<T> getEntityListByTypeAndPage(String type, PagingModel pagingModel);
}
