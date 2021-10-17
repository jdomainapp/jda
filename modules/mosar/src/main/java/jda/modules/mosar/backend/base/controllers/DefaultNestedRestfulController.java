package jda.modules.mosar.backend.base.controllers;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Locale;
import java.util.NoSuchElementException;
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

@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class DefaultNestedRestfulController<T1, T2>
        implements NestedRestfulController<T1, T2> {

    protected Class<T1> outerType = (Class) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];
    protected Class<T2> innerType = (Class) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[1];

    protected WebSocketHandler webSocketHandler;

    public DefaultNestedRestfulController(WebSocketHandler webSocketHandler) {
        this.webSocketHandler = webSocketHandler;
    }

    protected <X> CrudService<X> getServiceOfGenericType(String clsName) {
        return ServiceRegistry.getInstance().get(clsName);
    }

    @Override
    public T2 createInner(Identifier<?> outerId, T2 requestBody) {
        // TODO: FIX THIS!
        try {
            CrudService<T2> svc = getServiceOfGenericType(innerType.getSimpleName());
            CrudService<T1> outerService = getServiceOfGenericType(outerType.getSimpleName());
            T1 outer = outerService.getEntityById(outerId);

            // retrieve correct associations
            Class<T2> innerCls = (Class) requestBody.getClass();
            retrieveCorrectAssociations(requestBody, outer, innerCls);

            // create object
            T2 created = svc.createEntity(requestBody);

            // update outer
            outerService.updateEntity(outerId, outer);

            // perform server-push notification
            performServerPush();

            return created;
        } catch (IllegalArgumentException | IllegalAccessException
                | InvocationTargetException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void retrieveCorrectAssociations(T2 requestBody, T1 outer, Class<T2> innerCls) throws IllegalAccessException, InvocationTargetException {
        for (Field field : innerCls.getDeclaredFields()) {
            if (!field.isAnnotationPresent(DAssoc.class)) {
                continue;
            }
            DAssoc dAssoc = field.getAnnotation(DAssoc.class);
            DAssoc.AssocType assocType = dAssoc.ascType();
            DAssoc.AssocEndType endType = dAssoc.endType();
            if (assocType.equals(DAssoc.AssocType.Many2Many)
                    || (assocType.equals(DAssoc.AssocType.One2Many) && endType.equals(DAssoc.AssocEndType.One))) {
                continue;
            }
            CrudService service = getServiceOfGenericType(field.getType().getSimpleName());
            field.setAccessible(true);

            Class currentClass = field.getType().getSuperclass() != Object.class ?
                    field.getType().getSuperclass()
                    : field.getType();
            String setter = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            if (currentClass.equals(outer.getClass())) {
                getMethodByName(innerCls, setter, currentClass)
                        .invoke(requestBody, outer);
                continue;
            }
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

    private void performServerPush() {
        webSocketHandler.handleServerPush(
                ClassAssocUtils.getAssociated(innerType)
                        .stream()
                        .map(Class::getSimpleName)
                        .map(StringUtils::toUrlEntityString)
                        .collect(Collectors.toList()));
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

    private static <T> Page<T> paginate(Collection<T> entities, PagingModel pagingModel) {
        final int pageNumber = pagingModel.getPage();
        final int itemPerPage = pagingModel.getCount();

        if (entities == null || entities.isEmpty()) {
            return Page.empty();
        }
        final int size = entities.size();
        final int skip = (pageNumber - 1) * itemPerPage;
        if (skip > size) {
            throw new NoSuchElementException("Not found: Page #" + pageNumber);
        }
        final int pageCount = size / itemPerPage + size % itemPerPage > 0 ? 1 : 0;
        final Collection<T> pageContent = entities.stream().distinct().skip(skip).limit(itemPerPage).collect(Collectors.toList());
        return new Page<>(pageNumber, pageCount, pageContent);
    }

    @Override
    public Page<T2> getInnerListByOuterId(Identifier<?> outerId, PagingModel pagingModel) {
        // reflection-based solution -- needs HEAVY optimization
        CrudService<T1> svc = getServiceOfGenericType(outerType.getSimpleName());
        T1 outerById = svc.getEntityById(outerId);
        Method getInnersFromOuter = null;
        for (Method method : outerType.getMethods()) {
            if (method.getName().toLowerCase(Locale.ROOT)
                    .contains(innerType.getSimpleName().toLowerCase(Locale.ROOT))
                    && (method.getReturnType().equals(innerType)
                    || method.getReturnType().equals(Collection.class))) {
                getInnersFromOuter = method;
                break;
            }
        }
        if (getInnersFromOuter == null) {
            throw new IllegalStateException("No suitable getter found!");
        }
        try {
            return paginate((Collection<T2>) getInnersFromOuter.invoke(outerById), pagingModel);
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
