JDA Module: Domain modelling patterns
=======

A repository of domain modelling patterns defined in DCSL.

# Source code structure
1. `src/main/java`: pattern assets and shared APIs. These are used in the pattern definition files.
2. `src/main/resources`: pattern definition files (`*.jdp`; JDP is abbrv. "Java Domain Pattern"). Each pattern file may refer to one or more assets.
3. `src/test/java`: test/example programs for the patterns

# Deployment
GitHub repository: 

1. Create a folder named `test`
2. Download the module deployment jar file (`dist/module-dompattern-5.4-SNAPSHOT-deploy-apsec.jar`) into the `test` folder
3. Download the `dist/src.zip` file and unzip it into a `src` sub-folder of the test folder

The content of test folder needs to look like this:
```
- test/
  - module-dompattern-5.4-SNAPSHOT-deploy-apsec.jar
  - src/
    - jda/
      - ...
```
# Run tests

The instructions in this section use the command-line interface to run the JUnit tests contained in the deployment jar file.

All commands are **executed from within the `test` folder** that was created in the set-up section above.

The test runs will create a sub-folder, named `output`, of the `test` folder, which contains the output source code files. Thus, the test folder structure will look like this:

```
- test/
  - module-dompattern-5.4-SNAPSHOT-deploy-apsec.jar
  - src/
    - jda/
      - ...
  - output/
    - jda/
      - ...
```

## TPC
List of currently implemented TPCs:
1. TPCEntities: pattern Entities
2. TPCAggregates: pattern Aggregates
3. TPCMNormaliser: pattern MNormaliser
4. TPDataSourceAttrib: pattern DataSourceAttribute

### Example
To execute `TPCAggregates`. This TPC uses a pre-defined p-mapping to the CourseMan domain model:
```
test# java -cp ./module-dompattern-5.4-SNAPSHOT-deploy.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.tpc.TPCAggregatesTest
```

### Expected output
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
Aggregates:TPCAggregates...initialised
......executed
DOM...saved

Time: 0.33

OK (1 test)
```
## TGC

### Example
To execute a TGC, named `TGC1Test`, which consists of multiple patterns, using a pre-defined g-mapping to the CourseMan domain model:

```
test# java -cp ./module-dompattern-5.4-SNAPSHOT-deploy.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.tgc.TGC1Test
```

### Expected output:
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
TGC1:TGC...initialised
......executed
DOM...updated

Time: 0.336

OK (1 test)
```

## Example: Cargo shipping
To execute the pre-defined TGC for the CargoShipping case study: 

```
test# java -cp ./module-dompattern-5.4-SNAPSHOT-deploy.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.cargoshipping.TGCCargoShipping
```

### Expected output
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
CargoShipping:TGC...initialised
......executed
DOM...saved

Time: 0.398

OK (1 test)
```