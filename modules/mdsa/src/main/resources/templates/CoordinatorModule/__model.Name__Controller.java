package __outputPackage__;

import jda.modules.msacommon.controller.RedirectController;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

__foreach (m : modules)
import __m.model.outputPackage__.__m.model.Name__;
__endforeach

@Controller
public class __model.Name__Controller<ID> extends RedirectController<ID> {

	@Override
	public ResponseEntity handleRequest(HttpServletRequest req, HttpServletResponse res, String parentElement) {

		__foreach (m : modules)
		getPathmap().put("/__m.model.name__", __m.model.Name__.class);
		__endforeach

		return super.handleRequest(req, res, parentElement);
	}
}