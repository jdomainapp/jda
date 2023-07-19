package jda.modules.msacommon.controller;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import jda.modules.msacommon.messaging.kafka.KafkaChangeAction;

/**
 * restTemplate Default implementation of {@link #RestfulController}
 */
@SuppressWarnings("unchecked")
public abstract class DefaultController<T, ID> implements BasicRestController<T, ID> {

	private static final Logger logger = LoggerFactory.getLogger(DefaultController.class);

	@Autowired
	RestTemplate restTemplate;

	private final Class<T> genericType = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
			.getActualTypeArguments()[0];

	public Class<T> getGenericType() {
		return genericType;
	}

	public <U> PagingAndSortingRepository<T, ID> getServiceOfGenericType(Class<U> cls) {
		return ServiceRegistry.getInstance().get(cls.getSimpleName());
	}

	protected PagingAndSortingRepository<T, ID> getServiceOfGenericType(String clsName) {
		return ServiceRegistry.getInstance().get(clsName);
	}

	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, ID id) {
		try {
			String requestMethod = req.getMethod();
			if (requestMethod.equals(RequestMethod.GET.toString())) {
				String path = req.getServletPath();
				String propertyName = ControllerTk.getPropertyNameInPath(path);
				String findMethod = "findBy" + propertyName;
				Method method = ControllerTk.findMethodInClass(getServiceOfGenericType(genericType).getClass(), findMethod);
				if(method==null) {
					return ControllerTk.isPathFindAll(path) ? getEntityListByPage(PageRequest.of(0, 10)) : ResponseEntity.badRequest().build();
				}else if(id !=null){
					return getDataByPropertyName(propertyName, id);
				}else {
					String propertyValue = ControllerTk.getPropertyValueInPath(path);
					if(ControllerTk.isIntegerNumeric(propertyValue)) {
						return getDataByPropertyName(propertyName, Integer.parseInt(propertyValue));
					}else {
						return getDataByPropertyName(propertyName, propertyValue);
					}
				}
			} else if (requestMethod.equals(RequestMethod.POST.toString())) {
				String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
				if (!requestData.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					T entity = mapper.readValue(requestData, genericType);
					return createEntity(entity);
				} else {
					return ResponseEntity.badRequest().body("No Request body");
				}

			} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
				String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
				if (!requestData.isEmpty()) {
					ObjectMapper mapper = new ObjectMapper();
					T entity = mapper.readValue(requestData, genericType);
					return updateEntity(id, entity);
				} else {
					return ResponseEntity.badRequest().body("No Request body");
				}

			} else if (requestMethod.equals(RequestMethod.DELETE.toString())) {
				return deleteEntityById(id);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
		ResponseEntity<T> restExchange = restTemplate.exchange(restPath, HttpMethod.GET, null, genericType, id);

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
	public ResponseEntity<?> getDataByPropertyName(String propertyName, Object propertyValue) {
		PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
		String findMethod = "findBy" + propertyName;
		Method method = ControllerTk.findMethodInClass(service.getClass(), findMethod);
		if (method != null) {
			try {
				return ResponseEntity.ok(method.invoke(service, propertyValue));
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
			}
		}
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	}

	@Override
	public ResponseEntity<T> updateEntity(ID id, T updatedInstance) {
		PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
		T updatedEntity = service.save(updatedInstance);
		return ResponseEntity.ok(updatedEntity);
	}

	@Override
	public ResponseEntity<ID> deleteEntityById(ID id) {
		PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
		service.deleteById(id);
		return ResponseEntity.ok(id);
	}

	public ResponseEntity<?> handleRequest(HttpServletRequest req, HttpServletResponse res) {
		return null;
	}

}
