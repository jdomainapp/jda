package jda.modules.mosar.backend.base.services;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.expression.Op;
import jda.modules.dcsl.syntax.Associate;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.mosar.backend.base.models.Identifier;
import jda.modules.mosar.backend.base.models.Page;
import jda.modules.mosar.backend.base.models.PagingModel;
import jda.modules.mosar.utils.IdentifierUtils;
import jda.modules.patterndom.assets.domevents.CMEventType;
import jda.modules.patterndom.assets.domevents.Publisher;
import jda.mosa.controller.assets.ControllerTk;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.software.impl.SoftwareImpl;

@SuppressWarnings("unchecked")
public class SimpleDomServiceAdapter<T> implements CrudService<T> {
    protected final SoftwareImpl sw;
    protected Class<T> type;
    protected BiConsumer<Identifier, Object> onCascadeUpdate;

    // autowired constructor
    protected SimpleDomServiceAdapter(SoftwareImpl sw) {
        this(null, sw);
    }

    public SimpleDomServiceAdapter(Class<T> type, SoftwareImpl sw) {
        this.type = type;
        this.sw = sw;
    }

    public Class<T> getType() {
        return type;
    }

    protected void setType(Class<T> type) {
        this.type = type;
    }

    protected static <T> void validateObject(T input, SoftwareImpl sw)
            throws ConstraintViolationException {
        Class<T> cls = (Class) input.getClass();
        DataValidator<T> validator =
                ControllerTk.getDomainSpecificDataValidator(sw.getDODM(), cls);
        List<Field> attrFields = Stream.of(cls.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(DAttr.class))
                .collect(Collectors.toList());
        for (Field field : attrFields) {
            DAttr attr = field.getAnnotation(DAttr.class);
            field.setAccessible(true);
            try {
                Object fieldValue = field.get(input);
                validator.validateDomainValue(attr, fieldValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public T createEntity(T entity) {
        try {
            validateObject(entity, sw);
            sw.addObject((Class<T>) entity.getClass(), entity);

            // cascade update
            Class entityClass = entity.getClass();
            String[] fieldNames = getFieldNames(entityClass, type);
            Object[] values = getFieldValues(entityClass, type, fieldNames, entity)
                    .values().toArray();
            performCascadeUpdate(values);

            return entity;
        } catch (DataSourceException | ConstraintViolationException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T getEntityById(Identifier<?> id) {
        try {
            T retrieved = sw.retrieveObjectById(type, id.getId());
            try {
                sw.loadAssociatedObjects(retrieved);
            } catch (NullPointerException ex) {
                throw new NoSuchElementException("Not found: id = " + id.getId());
            }
            return retrieved;
        } catch (NotFoundException e) {
//ducmle:            throw new NoSuchElementException(e);
          throw new NoSuchElementException(e.getMessage());
        } catch (DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Page<T> getEntityListByPage(PagingModel pagingModel) {
        Collection<T> entities = this.getAllEntities();
        return paginate(entities, pagingModel);
    }

    protected Page<T> paginate(Collection<T> entities, PagingModel pagingModel) {
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
        final Collection<T> pageContent = entities.stream().skip(skip).limit(itemPerPage).collect(Collectors.toList());
        return new Page<>(pageNumber, pageCount, pageContent);
    }

    @Override
    public Collection<T> getAllEntities() {
        try {
            Collection<T> entities = sw.retrieveObjects(type, "id", Op.GT, "0");
            if (entities == null) entities = new ArrayList<>();
            for (T entity : entities) {
                try {
                    sw.loadAssociatedObjects(entity);
                } catch (NullPointerException ex) { }
            }
            return entities;
        } catch (NotFoundException | DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] getFieldNames(Class cls, Class... superClasses) {
        List<Field> fields = new ArrayList<>();
        fields.addAll(List.of(cls.getDeclaredFields()));
        fields.addAll(List.of(superClasses[0].getDeclaredFields()));

        List<String> fieldNames = fields.stream()
                .filter(field -> !Modifier.isStatic(field.getModifiers())
                    && Modifier.isPrivate(field.getModifiers()))
                .filter(field -> field.isAnnotationPresent(DAttr.class))
                .map(field -> field.getAnnotation(DAttr.class))
                .filter(attr -> !attr.id() && !attr.auto() && !attr.virtual())
                .map(DAttr::name)
                .collect(Collectors.toList());
        return fieldNames.toArray(new String[fieldNames.size()]);
    }

    private static boolean isOneOneOrManyOneAssocField(Field field) {
        if (!field.isAnnotationPresent(DAssoc.class)) return false;
        DAssoc dAssoc = field.getAnnotation(DAssoc.class);
        return dAssoc.ascType().equals(DAssoc.AssocType.One2One)
                || (dAssoc.ascType().equals(DAssoc.AssocType.One2Many)
                    && dAssoc.endType().equals(DAssoc.AssocEndType.Many));
    }

    private static Field getFieldByName(String name, Class cls, Class superClass) {
        try {
            Field field = cls.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (NoSuchFieldException e) {
//                        e.printStackTrace();
            try {
                Field field = superClass.getDeclaredField(name);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException ex) {
                return null;
            }
        }
    }

    //  if assoc 1-1 or M-1
    //      allow null
    //  else
    //      reject new value
    // SIDE EFFECT: Null assoc values are mapped instantly to REMOVE_LINK
    private static <T> Map<String, Object> getFieldValues(
            Class<T> cls, Class<T> superClass, String[] fieldNames, T o) {
        final Map<String, Object> toBeUpdated = new HashMap<>();

        List<String> fieldNameList = List.of(fieldNames);
        List<String> nullableFieldNames = fieldNameList.stream()
                .map(name -> getFieldByName(name, cls, superClass))
                .filter(Objects::nonNull)
                .filter(SimpleDomServiceAdapter::isOneOneOrManyOneAssocField)
                .map(Field::getName)
                .collect(Collectors.toList());
        List<String> nonNullableFieldNames = fieldNameList.stream()
                .filter(name -> !nullableFieldNames.contains(name))
                .collect(Collectors.toList());

        nullableFieldNames
                .stream()
                .map(name -> getFieldByName(name, cls, superClass))
                .forEach(field -> {
                    try {
                        toBeUpdated.put(field.getName(), field.get(o));
                    } catch (IllegalAccessException | NullPointerException e) {
                        toBeUpdated.put(field.getName(), null);
                    }
                });

        nonNullableFieldNames
                .stream()
                .map(name -> getFieldByName(name, cls, superClass))
                .filter(Objects::nonNull)
                .forEach(field -> {
                    try {
                        toBeUpdated.put(field.getName(), field.get(o));
                    } catch (IllegalAccessException | NullPointerException e) {
                        toBeUpdated.put(field.getName(), null);
                    }
                });

        return toBeUpdated;
    }

    @Override
    public T updateEntity(Identifier<?> id, T entity) {
        try {
            validateObject(entity, sw);
            if (!id.getId().toString().equals(
                IdentifierUtils.getIdField(getType()).get(entity).toString())) return null;
            Class<T> entityClass = (Class)entity.getClass();
            T oldEntity = sw.retrieveObjectById(entityClass, id.getId());

            if (entity == oldEntity) return entity;
            String[] fieldNames = getFieldNames(entityClass, type);

            Map<String, Object> updateValues = getFieldValues(entityClass, type, fieldNames, entity);
            final int numOfUpdateValues = updateValues.size();
            String[] updateFieldNames = updateValues.keySet().toArray(new String[numOfUpdateValues]);
            Object[] updateFieldValues = updateValues.values().toArray(new Object[numOfUpdateValues]);

            Map<String, Object> originalValues = getFieldValues(entityClass, type, fieldNames, oldEntity);
            originalValues.keySet().forEach(key -> {
                if (!updateValues.containsKey(key)) {
                    originalValues.remove(key);
                }
            });
            Object[] originalFieldValues = originalValues.values().toArray(new Object[numOfUpdateValues]);

            sw.updateObject(entityClass, oldEntity, updateFieldNames, updateFieldValues);

            performCascadeUpdate(originalFieldValues);
            performCascadeUpdate(updateFieldValues);

            return sw.retrieveObjectById(entityClass, id.getId());
        } catch (NotPossibleException | NotFoundException
                | DataSourceException | IllegalArgumentException
                | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void performCascadeUpdate(Object[] values) throws IllegalAccessException {
        // cascade update
        for (Object obj : values) {
            if (obj == null) continue;
            Class<?> cls = obj.getClass();
            if (!cls.isAnnotationPresent(DClass.class)) continue;
            Identifier identifier = null;
            for (Field field : cls.getDeclaredFields()) {
                if (!field.isAnnotationPresent(DAttr.class)) continue;
                DAttr dAttr = field.getAnnotation(DAttr.class);
                if (!dAttr.id()) continue;
                field.setAccessible(true);
                identifier = Identifier.fromString(field.get(obj).toString());
            }
            if (identifier == null) continue;
            if (this.onCascadeUpdate != null) {
                this.onCascadeUpdate.accept(identifier, obj);
            }
        }
    }

    @Override
    public void deleteEntityById(Identifier<?> id) {
        try {
            T toDelete = sw.retrieveObjectById(type, id.getId());
            if (toDelete == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Unable to find resource");
            }

            Collection<Associate> associates = sw.getDom().getAssociates(toDelete, toDelete.getClass());
            if (associates != null) {
                associates.forEach(associate ->
                        processDeleteOnAssociates(toDelete, associate));
            }
            
            sw.deleteObject(toDelete, type);

            // cascade update
//            performCascadeUpdate(values);
        } catch (DataSourceException e) {
            throw new RuntimeException(e);
        }
    }

    private void processDeleteOnAssociates(T toDelete, Associate associate) {
        System.out.println(associate.getAssociateObj());
        if (toDelete instanceof Publisher) {
            // remove link
            Publisher eventSourceObj = (Publisher) toDelete;
            eventSourceObj.notify(CMEventType.OnRemoved, eventSourceObj.getEventSource(), associate.getAssociateObj());
            eventSourceObj.removeAllSubscribers();
        }
        if (associate.isAssociationType(DAssoc.AssocType.One2One)) {
            updateAndSaveAssociate(associate, associate.getAssociateObj());
        } else if (associate.isAssociationType(DAssoc.AssocType.One2Many)
                && associate.isMyEndType(DAssoc.AssocEndType.Many)) {
            List<Object> associatedObjs = (List) associate.getAssociateObj();
            associatedObjs.forEach(associatedObj ->
                    updateAndSaveAssociate(associate, associatedObj));
        }
    }

    private void updateAndSaveAssociate(Associate associate, Object associatedObj) {
        sw.getDom().updateAssociateLink(
                associatedObj,
                associate.getMyEndAttribute(),
                null);
        try {
            sw.updateObject((Class) associatedObj.getClass(), associatedObj);
        } catch (DataSourceException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setOnCascadeUpdate(BiConsumer<Identifier, Object> handler) {
        this.onCascadeUpdate = handler;
    }
}
