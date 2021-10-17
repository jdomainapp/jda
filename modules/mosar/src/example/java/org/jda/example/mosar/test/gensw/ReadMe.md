# How to generate and use test CourseMan software 

## Prerequisites
- Package: `org.jda.example.mosar.test.gensw`: contains the generator programs
  - `RFSSwGenByCountCourseMan`: generate the test software configuration
  - `RFSCourseManPerform`: generate the RFS software from the software configuration
- Package `org.jda.example.mosar.test.performance`: contains the generated test software
  - `model`: seed domain model (**must not be deleted**)

## Generated artefacts of the test software
All the generated artefacts of the test software are located in the package `org.jda.example.mosar.test.performance`:
  - `modelN`: the domain model that contains `N` copies of the seed domain model
  - `modulesN`: the generated MCCs of the domain model
  - `softwareN`: the generated `ModuleMain` and the `config.SCC`

## How-to steps

1. Change the `rfsgenconfig.json` file:
   - Change `count` property to the number of domain model copies
   - change `rfsGenDesc`'s properties (especially the output folders) to suite the target setup
2. Execute the `RFSSwGenByCountCourseMan.main` method in an IDE
  
## How to generate the RFS version of the test software
Class `RFSCourseManPerform`:
- Set up a PostgreSQL database for CourseMan:
  - Install PostgreSQL 
  - Add a super-user account: `user = 'admin', password = 'password'`
  - create a database named `coursemands`
- Update the SCC: set `user`, `password` to the above database account
- Update `RFSCourseManPerform.main`: set the variable `scc` value in the method `main` to the generated SCC
- Execute the `main` method in an IDE


 
