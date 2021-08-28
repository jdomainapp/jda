# How to generate and use test CourseMan software 

## Prerequisites
- Package: `jda.modules.restfstool.test.gensw`: contains the generator programs
  - `RFSSwGenByCountCourseMan`: generate the test software configuration
  - `RFSCourseManPerform`: generate the RFS software from the software configuration
- Package `jda.modules.restfstool.test.performance`: contains the generated test software
  - `model`: seed domain model (**must not be deleted**)

## Generated artefacts of the test software
All the generated artefacts of the test software are located in the package `jda.modules.restfstool.test.performance`:
  - `modelN`: the domain model that contains `N` copies of the seed domain model
  - `modules`: the generated MCCs of the domain model
  - `software`: the generated `ModuleMain` and the `config.SCC`

## How to generate the test software

Class `RFSSwGenByCountCourseMan`:
- Change the `count` variable in method `main` to change the number of domain model copies
- Execute the `main` method in an IDE
- **IMPORTANT**: you MUST follow the instructions on the console to successfully complete the execution!
  
## How to generate the RFS version of the test software
Class `RFSCourseManPerform`:
- Set up a PostgreSQL database for CourseMan:
  - Install PostgreSQL 
  - Add a super-user account: `user = 'admin', password = 'password'`
  - create a database named `coursemands`
- Update the SCC: set `user`, `password` to the above database account
- Update `RFSCourseManPerform.main`: set the variable `scc` value in the method `main` to the generated SCC
- Execute the `main` method in an IDE


 
