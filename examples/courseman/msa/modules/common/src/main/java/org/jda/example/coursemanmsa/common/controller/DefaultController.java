package org.jda.example.coursemanmsa.common.controller;

import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.connections.UserContext;
import org.jda.example.coursemanmsa.common.events.model.ChangeModel;
import org.jda.example.coursemanmsa.common.messaging.kafka.KafkaChangeAction;
import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Default implementation of {@link #RestfulController}
 */
@SuppressWarnings("unchecked")
public abstract class DefaultController<T, ID> implements IController<T, ID> {

	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);
	
	@Autowired
	RestTemplate restTemplate;
	
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
    
    public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, ID id){
    	try {
		String requestMethod = req.getMethod();
		if (requestMethod.equals(RequestMethod.GET.toString())) {
			if (id != null) {
				return new MyResponseEntity<T,ID>(getEntityById(id),null);
			} else {
				return new MyResponseEntity(getEntityListByPage(PageRequest.of(0, 10)), null);
			}
		} else if (requestMethod.equals(RequestMethod.POST.toString())) {
			String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
			if (!requestData.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				T entity = mapper.readValue(requestData,genericType);
				ResponseEntity<T> result = createEntity(entity);
				ChangeModel<ID> changeModel = new ChangeModel(genericType.getTypeName(), KafkaChangeAction.CREATED, null, UserContext.getCorrelationId());
				return new MyResponseEntity(result, changeModel);
			}else {
				return new MyResponseEntity (ResponseEntity.ok("No Request body"), null);
			}
			
		} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
			if (!requestData.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				T entity = mapper.readValue(requestData,genericType);
				ResponseEntity<T> result=updateEntity(id, entity);
				ChangeModel<ID> changeModel = new ChangeModel(genericType.getTypeName(), KafkaChangeAction.UPDATED, id, UserContext.getCorrelationId());
				return new MyResponseEntity(result, changeModel);
			}else {
				return new MyResponseEntity (ResponseEntity.ok("No Request body"), null);
			}
			
		} else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
			ResponseEntity<String> result= deleteEntityById(id);
			ChangeModel<ID> changeModel = new ChangeModel(genericType.getTypeName(), KafkaChangeAction.DELETED, id, UserContext.getCorrelationId());
			return new MyResponseEntity(result, changeModel);
		}
    	}catch (Exception e) {
			logger.error(e.getMessage());
			return new MyResponseEntity (ResponseEntity.ok("ERROR"), null);
		}
		return new MyResponseEntity (ResponseEntity.ok("No method for request URL"), null);
	}
    
    public void executeReceivedEvent(String action, ID id, String restPath) {
		if (action.equals(KafkaChangeAction.CREATED)) {
			T entity = getEntityByREST(id, restPath);
			createEntity(entity);
		} else if (action.equals(KafkaChangeAction.UPDATED)) {
			T entity = getEntityByREST(id, restPath);
			updateEntity(id, entity);
		} else if (action.equals(KafkaChangeAction.DELETED)) {
			deleteEntityById(id);
			
		} else {
			logger.error("Received an UNKNOWN event with action: {}, type: {}", action, genericType);
		}
    }
    
    public T getEntityByREST(ID id, String restPath) {
		ResponseEntity<T> restExchange = restTemplate.exchange( restPath, HttpMethod.GET, null, genericType,id);

		return restExchange.getBody();
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
