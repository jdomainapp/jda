package jda.modules.mosar.backend.base.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.stream.Collectors;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.backend.base.services.CrudService;
import jda.modules.mosar.backend.base.websockets.WebSocketHandler;
import jda.modules.mosar.utils.ClassAssocUtils;
import jda.modules.mosar.utils.StringUtils;

/**
 * Default implementation of {@link #RestfulController}
 */
@SuppressWarnings("unchecked")
public abstract class DefaultRestfulController<T> implements RestfulController<T> {

    private final Class<T> genericType =
        (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];

    private final WebSocketHandler webSocketHandler;

    public DefaultRestfulController(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    protected Class<T> getGenericType() {
        return genericType;
    }

    protected <U> CrudService<U> getServiceOfGenericType(Class<U> cls) {
        return ServiceRegistry.getInstance().get(cls.getSimpleName());
    }

    @Override
    public T createEntity(T inputEntity) {
        CrudService<T> service = getServiceOfGenericType(genericType);
        try {
            retrieveCorrectAssociations(inputEntity, genericType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        T createdEntity = service.createEntity(inputEntity);
        // server-push notification
        performServerPush();

        return createdEntity;
    }

    protected <X> CrudService<X> getServiceOfGenericType(String clsName) {
        return ServiceRegistry.getInstance().get(clsName);
    }

    protected void retrieveCorrectAssociations(Object requestBody, Class innerCls)
            throws IllegalAccessException, InvocationTargetException {
        for (Field field : innerCls.getDeclaredFields()) {
            if (isManyManyOrOneMany(field)) continue;
            CrudService service = this.<T>getServiceOfGenericType(field.getType().getSimpleName());
            field.setAccessible(true);

            Class currentClass = field.getType().getSuperclass() != Object.class ?
                    field.getType().getSuperclass()
                    : field.getType();
            String setter = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);

            Object current = field.get(requestBody);

            if (current == null) {
                getMethodByName(innerCls, setter, currentClass)
                        .invoke(requestBody, new Object[1]);
                continue;
            }
            Field currentIdField = getIdField(currentClass);
            currentIdField.setAccessible(true);
            String identifier = currentIdField.get(current).toString();
            getMethodByName(innerCls, setter, currentClass)
                    .invoke(requestBody,
                            service.getEntityById(Identifier.fromString(identifier)));
        }
    }

    private boolean isManyManyOrOneMany(Field field) {
        if (!field.isAnnotationPresent(DAssoc.class)) {
            return true;
        }
        DAssoc dAssoc = field.getAnnotation(DAssoc.class);
        DAssoc.AssocType assocType = dAssoc.ascType();
        DAssoc.AssocEndType endType = dAssoc.endType();
        if (assocType.equals(DAssoc.AssocType.Many2Many)
                || (assocType.equals(DAssoc.AssocType.One2Many) && endType.equals(DAssoc.AssocEndType.One))) {
            return true;
        }
        return false;
    }

    private static Field getIdField(Class<?> cls) {
        if (cls.getSuperclass() != Object.class) {
            cls = cls.getSuperclass();
        }
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(DAttr.class)) {
                continue;
            }
            DAttr dAttr = field.getAnnotation(DAttr.class);
            if (dAttr.id()) return field;
        }
        throw new IllegalStateException("No ID field found!");
    }

    private static Method getMethodByName(Class<?> cls, String name, Class<?>... parameters) {
        try {
            return cls.getMethod(name, parameters);
        } catch (NoSuchMethodException | SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void performServerPush() {
        List<Class<?>> associatedClasses = ClassAssocUtils.getAssociated(genericType);
        associatedClasses.add(genericType);
        webSocketHandler.handleServerPush(
                associatedClasses
                    .stream()
                    .map(Class::getSimpleName)
                    .map(StringUtils::toUrlEntityString)
                    .collect(Collectors.toList()));
    }

    @Override
    public Page<T> getEntityListByPage(PagingModel pagingModel) {
        return getServiceOfGenericType(genericType)
            .getEntityListByPage(pagingModel);
    }

    @Override
    public T getEntityById(Identifier<?> id) {
        return getServiceOfGenericType(genericType)
            .getEntityById(id);
    }

    @Override
    public T updateEntity(Identifier<?> id, T updatedInstance) {
        CrudService<T> service = getServiceOfGenericType(genericType);
        try {
            retrieveCorrectAssociations(updatedInstance, genericType);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        T updatedEntity = service.updateEntity(id, updatedInstance);
        // server-push notification
        performServerPush();
        return updatedEntity;
    }

    @Override
    public void deleteEntityById(Identifier<?> id) {
        getServiceOfGenericType(genericType).deleteEntityById(id);
        // server-push notification
        performServerPush();
    }

}
