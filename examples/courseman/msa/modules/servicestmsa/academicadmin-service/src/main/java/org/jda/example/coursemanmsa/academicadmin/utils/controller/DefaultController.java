package org.jda.example.coursemanmsa.academicadmin.utils.controller;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of {@link #RestfulController}
 */
@SuppressWarnings("unchecked")
public abstract class DefaultController<T, ID> implements IController<T, ID> {

    private final Class<T> genericType =
        (Class<T>) ((ParameterizedType) getClass()
            .getGenericSuperclass()).getActualTypeArguments()[0];

    protected Class<T> getGenericType() {
        return genericType;
    }

    protected <U> PagingAndSortingRepository<T, ID> getServiceOfGenericType(Class<U> cls) {
        return ServiceRegistry.getInstance().get(cls.getSimpleName());
    }
    

    protected PagingAndSortingRepository<T, ID> getServiceOfGenericType(String clsName) {
        return ServiceRegistry.getInstance().get(clsName);
    }
    
    public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String pathPatern) throws IOException {
		String requestMethod = req.getMethod();
		String path = req.getServletPath();
		T entity=null;
		ID id= null;
		if(path.matches(pathPatern)) {
			String pathVariable = path.substring(path.lastIndexOf("/")+1);
			id = (ID) pathVariable;
		}
		String requestData = req.getReader().lines().collect(Collectors.joining());
		if (!requestData.isEmpty()) {
			ObjectMapper mapper = new ObjectMapper();
			entity = mapper.readValue(requestData,genericType);
		}
		if (requestMethod.equals(RequestMethod.GET.toString())) {
			if (id != null) {
				return getEntityById(id);
			} else {
				return getEntityListByPage(PageRequest.of(0, 10));
			}
		} else if (requestMethod.equals(RequestMethod.POST.toString())) {
			return createEntity(entity);
		} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			return updateEntity(id, entity);
		} else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
			deleteEntityById(id);
		}
		return ResponseEntity.ok("No method for request URL");
	}

    @Override
    public ResponseEntity<T> createEntity(@RequestBody T inputEntity) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        T createdEntity = service.save(inputEntity);
        return ResponseEntity.ok(createdEntity);
    }

    @Override
    public ResponseEntity<Page<T>> getEntityListByPage(Pageable pagingModel) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        return ResponseEntity.ok(service.findAll(pagingModel));
    }

    @Override
    public ResponseEntity<T> getEntityById(ID id) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
    	Optional<T> opt = service.findById(id);
        return ResponseEntity.ok((opt.isPresent()) ? opt.get() : null);
    }

    @Override
    public ResponseEntity<T> updateEntity(ID id, T updatedInstance) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        T updatedEntity = service.save(updatedInstance);
        return ResponseEntity.ok(updatedEntity);
    }

    @Override
    public ResponseEntity<String> deleteEntityById(ID id) {
    	PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
        service.deleteById(id);
        return ResponseEntity.ok("Ok");
    }

}
