JDA `module-dompattern`: DDD patterns
=======

A repository for DDD patterns specification and application.

# Source code structure
1. `src/main/java`: pattern assets and shared APIs. These are used in the pattern definition files.
2. `src/main/resources`: pattern definition files (`*.jdp`; JDP is abbrv. "Java Domain Pattern"). Each pattern file may refer to one or more assets.
3. `src/test/java`: test/example programs for the patterns

# Using the deployed module
GitHub repository: `https://github.com/jdomainapp/jda-dompattern/dist`

1. Create a folder named `test` in the local hard drive
2. Download the module deployment jar file: $JARFILE= `dist/jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar`, into the `test` folder
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

The test runs will create a sub-folder, named `output`, of the `test` folder, which contains the output source code files. Thus, the test folder structure with the output will look like this:

```
- test/
  - jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar
  - src/
    - jda/
      - ...
  - output/
    - jda/
      - ...
```

## Transformation procedures
List of currently implemented TPs:
1. TPEntities: pattern Entities
2. TPAggregates: pattern Aggregates
3. TPMNormaliser: pattern MNormaliser
4. TPDataSourceAttrib: pattern DataSourceAttribute

### Example
To execute `TPAggregates`. This TP uses a pre-defined p-mapping to the CourseMan domain model:
```
test# java -cp ./jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.tpc.TPAggregatesTest
```

### Expected output
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
Aggregates:TPAggregates...initialised
......executed
DOM...saved

Time: 0.33

OK (1 test)
```
## Transformation program

### Example
To execute a TG, named `TG1Test`, which consists of multiple patterns, using a pre-defined g-mapping to the CourseMan domain model:

```
test# java -cp ./jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.tgc.TG1Test
```

### Expected output:
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
TG1:TG...initialised
......executed
DOM...updated

Time: 0.336

OK (1 test)
```

## Case study: Cargo shipping

### Assets
1. Domain model (available in the `src` folder): $DOM = `jda.modules.patterndom.test.dom.cargoshipping.domain.model.cargo`
   1. 4 key domain classes: `Cargo`, `Delivery`, `Itinerary`, `RouteSpecification`
   2. Code adaption for class `Cargo`: `Cargo_CodeAdaptationAfterTransform.java`
2. `TGCargoShipping` app: $APP = `jda.modules.patterndom.test.cargoshipping.TGCargoShipping`
3. Unit test: `jda.modules.patterndom.test.cargoshipping.CargoTest`

### Procedure
1. (If not running the first time) Copy 4 key domain classes from subpackage `bak` of $DOM into $DOM, overriding existing classes
2. Execute $APP to apply the p-models, generating 4 new classes in the `output` folder of the project
```
test# java -cp ./jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar org.junit.runner.JUnitCore jda.modules.patterndom.test.cargoshipping.TGCargoShipping
```
3. Copy the 4 new classes back into $DOM, overriding the existing classes
4. Adapt the code of `Cargo.java` by apply the code adaptation in the file `Cargo_CodeAdaptationAfterTransform.java`. The code blocks to be copied are marked with the starting comment `// NEW`
5. Test the transformed class Cargo of the output (transformed) model:
  1. Create a new IDE project:
     - src folder = folder `src`
     - Referenced library: add `jda-dompattern-5.4-SNAPSHOT-test-jar-with-dependencies.jar`
  2. Run JUnit test `jda.modules.patterndom.test.cargoshipping.CargoTest` to observe that all tests are passed

### Expected output
```
Root src path: /data/projects/jda/modules/dompattern/target/src
Root output path: /data/projects/jda/modules/dompattern/target/output
DOM...initialised
CargoShipping:TG...initialised
......executed
DOM...saved

Time: 0.398

OK (1 test)
```