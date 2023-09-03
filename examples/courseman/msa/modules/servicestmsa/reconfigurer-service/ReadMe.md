# App: ServiceReconfigurer

The Service reconfigurer use case involves two ends: source and target. 

Implements `ServReconfigurer` in the TMSA paper.

## General instructions

1. Prerequisites
Update the following paths in `$CONFIG-SERVER/config/reconfigurer-service.properties` to suit your system
``` 
path.service.deploySource = /home/ducmle/tmp/jda/source
path.service.deployTarget = /home/ducmle/tmp/jda/target
# service execution log file
service.shell.logFile= /data/projects/jda/log/service-shell.log
```
2. Test case parameters:
  - `sourceServ`: `academicadmin-service`
  - `module`: `address`
  - `targetServ`: `academicadmin-service`
3. Starts the infrastructure services (config-server, discovery-server, gateway server, (if needed) kafka) of the project `jda-eg-coursemanmsa`
4. Starts the source service `$sourceServ`
5. Starts the target service `$targetServ` (if different)
6. Starts the `reconfigurer-service`
7. Run the test case in PostMan
  - `reconfigure-service/Promote*`
  - `reconfigure-service/Demote*`

## Promote a module into a service
After step 7. described above for PostMan test cases: `reconfigure-service/Promote*`:

1. Check if the promoted service can be accessed through `${targetServ}` as parent, using PostMan: `reconfigure-service/GetPromoted*`. Check that results are the same as invoking the service directly.
2. Test that `module` has been removed from `sourceServ`'s `ControllerRegistry` using PostMan test case: `${sourceServ}/myactuator/show`  
3. (Optional) Check the `${java.io.tmpdir}/jda/log/service-shell.log` that the service was started correctly.

## Demote a service back to a module
After step 7. described above for PostMan test cases: `reconfigure-service/Demote*`:

1. Check if service has been shutdown using `.../actuator/health`
2. Check if the demoted service can NOT be accessed through `${targetServ}` as parent, using PostMan: `reconfigure-service/GetPromoted*`. Result should be 404 (Not Found).
   1. Start demoted service as an independent service
   2. Repeat the outer step (2), result should still be 404.
3. Check if a new module has been registered into `targetServ`'s `ControllerRegistry` using PostMan test case: `${targetServ}/myactuator/show`
4. (Optional) Check the `${java.io.tmpdir}/jda/log/service-shell.log` that the service was shutdown correctly.
