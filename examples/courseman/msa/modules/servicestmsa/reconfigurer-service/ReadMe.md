# App: ServiceReconfigurer

The Service reconfigurer use case involves two ends: source and target. 

Implements `ServReconfigurer` in the TMSA paper.

## Promote a module into a service 
Instructions:

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
  - `module`: `hello`
  - `targetServ`: `academicadmin-service`
3. Starts the infrastructure services (config-server, discovery-server, gateway server, (if needed) kafka) of the project `jda-eg-coursemanmsa`
4. Starts the source service `$sourceServ`
5. Starts the target service `$targetServ` (if different)
6. Starts the `reconfigurer-service`
7. Run the test case in PostMan
  - `reconfigure-service/Promote`
  - `reconfigure-service/Demote`

## Demote a service back to a module
Instructions:
