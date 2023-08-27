package jda.modules.msacommon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import jda.modules.msacommon.messaging.kafka.KafkaChangeAction;
import org.apache.commons.io.FileUtils;
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
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Optional;
import java.util.stream.Collectors;

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

	public ResponseEntity handleRequestWithFile(String requestMethod, T entity, ID id, MultipartFile fileUpload,
			String filePath) {
		if (entity == null) {
			return ResponseEntity.badRequest().body("No Request body");
		}
		if (requestMethod.equals(RequestMethod.POST.toString())) {
			ControllerTk.saveFile(fileUpload, filePath);
			return createEntity(entity);
		} else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			ControllerTk.saveFile(fileUpload, filePath);
			return updateEntity(id, entity);
		}
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}


	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, ID id) {
		try {
			String requestMethod = req.getMethod();
			if (requestMethod.equals(RequestMethod.GET.toString())) {
				String path = req.getServletPath();
				String propertyName = ControllerTk.getPropertyNameInPath(path);
				String findMethod = "findBy" + propertyName;
				Method method = ControllerTk.findMethodInClass(getServiceOfGenericType(genericType).getClass(),
						findMethod);
				if (method == null) {
					return ControllerTk.isPathFindAll(path) ? getEntityListByPage(PageRequest.of(0, 10))
							: ResponseEntity.badRequest().build();
				} else if (id != null) {
					return getDataByPropertyName(propertyName, id);
				} else {
					String propertyValue = ControllerTk.getPropertyValueInPath(path);
					if (ControllerTk.isIntegerNumeric(propertyValue)) {
						return getDataByPropertyName(propertyName, Integer.parseInt(propertyValue));
					} else {
						return getDataByPropertyName(propertyName, propertyValue);
					}
				}
			} else if (requestMethod.equals(RequestMethod.DELETE.toString())){
				return deleteEntityById(id);
			}else {
				T entity = convertRequestDataToEntity(req);
				return handleReques(requestMethod, entity, id);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}
	
	public ResponseEntity<?> handleReques(String requestMethod, T entity, ID id) {
		if(entity == null) {
			return ResponseEntity.badRequest().body("No Request body");
		}
		if (requestMethod.equals(RequestMethod.POST.toString())) {
			return createEntity(entity);
		}else if (requestMethod.equals(RequestMethod.PUT.toString())) {
			return updateEntity(id, entity);
		}
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	}

	public T convertRequestDataToEntity(HttpServletRequest req) {
		try {
			String requestData = req.getReader().lines().collect(Collectors.joining()).trim();
			if (!requestData.isEmpty()) {
				ObjectMapper mapper = new ObjectMapper();
				return mapper.readValue(requestData, genericType);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
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
	public ResponseEntity<T> updateEntity(ID id, T updatedInstance) {
		PagingAndSortingRepository<T, ID> service = getServiceOfGenericType(genericType);
		ID currentId = getId(updatedInstance);
		if (id.equals(currentId)) {
			T updatedEntity = service.save(updatedInstance);
			return ResponseEntity.ok(updatedEntity);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

	private ID getId(T updatedInstance) {
		String getMethod = "getId";
		Method method = ControllerTk.findMethodInClass(updatedInstance.getClass(), getMethod);
		if (method != null) {
			try {
				return (ID) method.invoke(updatedInstance, null);
			} catch (Exception e) {
			}
		}
		return null;
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

	public ResponseEntity<?> handleRequestWithFile(String httpMethod, T entity, ID id, MultipartFile fileUpload) {
		return null;
	}

	public ResponseEntity<?> handleRequest(String method, T entity, int id){
		return null;
	}

	public static void saveFile(MultipartFile fileUpload, String filePath) {
		try {
			FileUtils.copyInputStreamToFile(fileUpload.getInputStream(), new File(filePath));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String convertObjectToJSON(Object object) {
		Gson gson = new Gson();

		return gson.toJson(object);
	}

}