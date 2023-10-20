# CourseMan Basic Software

A full-featured version of the CourseMan application that is used to demonstrate extended set of functionalities of JDA. This covers all three aDSLs: DCSL, MCCL and SCCL.

## List of program classes
Execute the following program classes using the `bin/mvn-java` script:

- Artifact: `jda.examples.courseman:software`
- Package: `org.courseman.software`:

1. `DomCity.java`: object management for class City
2. `DomStudents.java`: object management for class City
3. `DomUtilities.java`: domain object management functionalities
4. `DomReport.java`: report object management
5. `Main.java`: GUI-based software
6. `MainSoftwareConfigure.java`: software configuration

Run these programs on Eclipse IDE or from the command line.

### Using Eclipse IDE (Preferred)
This is a preferred method as the support for command-line execution in Maven is quite limited.

Simply run each of the above programs in the Eclipse IDE.

### Using the command line
Run the following programs in the listed order:

- `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.DomainCity`