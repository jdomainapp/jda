# Generated CourseMan Software example 

## Set up the example
1. clone/pull latest version of the the training repository.
   - Suppose the cloned folder is: `repo.folder` = `/home/test/jda-training/training`
   - The **Maven project** of this example:
      `proj.folder` = `${repo.folder}/jda/examples/jda-eg-coursemansw`
   - The `root.path`: `/home/test/`
   - the config file: `config.file` = `${proj.folder}/src/main/resources/swgenconfig.json`
2. Configure the root path:
  - change property `rootPath` in the `${config.file}` to the `${root.path}`
  - Note: if you are using Windows then you nedd to change all paths to use the double backward-slash `\\` as the path separator!
3. Install PostgreSQL RDBMS
   1. create database: `coursemands`
   2. database admin account: user = `'user'`, password = `'password'`

## How to run the CourseMan example from the IDE
1. Change configuration to compile generated source files
   - change property `compileSrc` in the `${config.file}` to `true`
2. Import the Maven project of the example into the IDE
3. Run class: `org.jda.example.coursemansw.swgen.CourseManSwGen`
4. Follow the instructions on the screen. Choose one command option at a time, in the displayed order.

## How to run the CourseMan example from the command line
Open 2 console terminals with the current dir set to `proj.folder`.

1. (Terminal 1) Compile the code (from the parent folder, to ensure that local-maven-repo is updated with any JDA's jar changes)
   1. cd into the parent folder: `.../examples`
   2. Compile source code: `mvn -U compile` (`-U` option is to activate the local-maven-repo updates)
2. (Terminal 1) Run the software: cd back into the `proj.folder` and
   `mvn exec:java@run`
3. (Terminal 1) Choose 'C' to generate software configuration
4. (Terminal 2) compile the source code: `mvn compile` (to compile the generated code)
5. (Terminal 1) Choose 'S' to generate the software class
6. Repeat step 4 to compile the generated code
7. (Terminal 1) Choose 'R' to run the software
     
## Study the example
1. Open the modules packages and study the generated MCCs
2. Open the software package and study the generated SCC
3. Experiment with different configuration settings of the MCCs, SCC 
4. Study the [two papers](https://github.com/jdomainapp/training/issues/6) published in 2019 and 2020 to know more about MCCL and SCCL
