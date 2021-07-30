package jda.modules.restfstool.backend.base.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jda.modules.restfstool.backend.base.models.Page;
import jda.modules.restfstool.backend.base.models.PagingModel;
import jda.modules.restfstool.backend.base.services.InheritedCrudService;
import jda.modules.restfstool.backend.base.websockets.WebSocketHandler;

import java.util.Collection;
import java.util.Objects;

import org.jda.example.restfstool.springapp.services.coursemodule.model.CompulsoryModule;
import org.jda.example.restfstool.springapp.services.coursemodule.model.CourseModule;

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
