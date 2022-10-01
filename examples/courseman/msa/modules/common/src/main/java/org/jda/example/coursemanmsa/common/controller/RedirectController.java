package org.jda.example.coursemanmsa.common.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jda.example.coursemanmsa.common.model.MyResponseEntity;
import org.springframework.http.ResponseEntity;

public abstract class RedirectController<ID> implements IController<ID>{

	private final static Map<String, Class<?>> PathMap = new HashMap<>();

	public static Map<String, Class<?>> getPathmap() {
		return PathMap;
	}

	public MyResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {
		String path = req.getServletPath();
		for (Entry<String, Class<?>> e : PathMap.entrySet()) {
			String p = e.getKey();
			Class<?> cls = e.getValue();
			if (matchDescendantModulePath(path, parentElement, p)) {
				// handle invocation
				DefaultController<?, ID> controller = ControllerRegistry.getInstance().get(cls);
				ID id = null;
				if (matchDescendantModulePathWithId(path, parentElement, p)) { // id available in path
					String pathVariable = path.substring(path.lastIndexOf("/") + 1);
					id = ControllerTk.parseDomainId(cls, "id", pathVariable);
				}
				return controller.handleRequest(req, res, id);
			}
		}

		// invalid path
		return new MyResponseEntity(ResponseEntity.ok("Invalid path: " + path), null);
	}

	/**
	 * @effects if {@link #matchDescendantModulePath(String, String)} on
	 *          <tt>(urlPath, pathElement)</tt> and that the last path segment of
	 *          <tt>urlPath</tt> specifies an object id then return true else return
	 *          false
	 * 
	 */
	protected boolean matchDescendantModulePathWithId(String urlPath, String parentElement, String pathElement) {
		// FIXME: fix path matching to correctly implement the behaviour specification
		// (above)
		return urlPath != null && pathElement != null
				&& urlPath.matches("(.*)" + parentElement + pathElement + "/(.+)");
	}

	/**
	 * @effects if <tt>pathElement</tt> is found in a subpath of <tt>urlPath</tt>
	 *          that corresponds to a descendant module of this module in the
	 *          service tree then return true else return false
	 */
	protected boolean matchDescendantModulePath(String urlPath, String parentElement, String pathElement) {
		// FIXME: fix path matching to correctly implement the behaviour specification
		// (above)
		return urlPath != null && pathElement != null && urlPath.matches("(.*)" + parentElement + pathElement + "(.*)");
	}

}
