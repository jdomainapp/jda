package org.jda.example.coursemanmsa.coursemgnt.utils.controller;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
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
    
    public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, ID id){
    	try {
		String requestMethod = req.getMethod();
		if (requestMethod.equals(RequestMethod.GET.toString())) {
			if (id != null) {
				return getEntityById(id);
			} else {
				return getEntityListByPage(PageRequest.of(0, 10));
			}
		} else if (requestMethod.equals(RequestMethod.POST.toString())) {
			String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
			if (!requestData.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				T entity = mapper.readValue(requestData,genericType);
				return createEntity(entity);
			}else {
				return ResponseEntity.ok("No Request body");
			}
			
		} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
			if (!requestData.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				T entity = mapper.readValue(requestData,genericType);
				return updateEntity(id, entity);
			}else {
				return ResponseEntity.ok("No Request body");
			}
			
		} else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
			deleteEntityById(id);
		}
    	}catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.ok("ERROR");
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
