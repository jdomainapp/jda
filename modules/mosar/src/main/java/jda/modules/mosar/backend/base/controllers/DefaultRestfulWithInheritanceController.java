package jda.modules.mosar.backend.base.controllers;

import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.services.InheritedCrudService;
import jda.modules.mosar.backend.base.websockets.WebSocketHandler;

/**
 * Default implementation of {@link #RestfulWithInheritanceController}
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class DefaultRestfulWithInheritanceController<T>
        extends DefaultRestfulController<T>
        implements RestfulWithInheritanceController<T> {

    public DefaultRestfulWithInheritanceController(WebSocketHandler webSocketHandler) {
        super(webSocketHandler);
    }

    @Override
    public Page<T> getEntityListByTypeAndPage(String type, PagingModel pagingModel) {
        return ((InheritedCrudService) getServiceOfGenericType(getGenericType()))
                .getEntityListByTypeAndPage(type, pagingModel);
    }

}
