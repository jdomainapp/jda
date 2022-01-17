# CourseMan Basic Software

A basic version of the CourseMan application that is used to demonstrate the core functionalities of JDA.

## List of program classes
Execute the following program classes using the `bin/mvn-java` script:

- Artifact: `jda.examples.courseman:basics`
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
- `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.DomStudents`
- `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.DomUtilities`
- `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.DomReport`
- `bin/mvn-java.bash jda.examples.courseman:basics org.courseman.software.DomReport`