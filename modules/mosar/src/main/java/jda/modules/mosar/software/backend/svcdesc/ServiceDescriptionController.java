package jda.modules.mosar.software.backend.svcdesc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

/**
 * Controller (entry point) for Service Description module
 * @author binh_dh
 */
@RestController
@RequestMapping("/services")
@Path("/services")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceDescriptionController {

    private final String basePackageName;

    public ServiceDescriptionController() {
        basePackageName = "com.hanu.domainfs";
    }

    public ServiceDescriptionController(
            @Value("${basePackage}") String basePackageName) {
        this.basePackageName = basePackageName;
    }

    /**
     * Get the list of service descriptions.
     * @return the list of service descriptions
     */
    @GetMapping
    @GET
    public List<ServiceDescription> getServiceDescriptions() {
        return ServiceDescriptor.getDescriber()
            .describePackage(basePackageName);
    }
}
