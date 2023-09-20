# CourseMan Basic Software

A basic version of the CourseMan application that is used to demonstrate the core functionalities of JDA.

## List of program classes
Execute the following program classes using the `bin/mvn-java` script:

- Artifact: `jda.example.courseman:jda-eg-coursemanbasics`
- Package: `org.jda.example.courseman.software`:

1. `DomCity.java`: object management for class City
2. `DomStudents.java`: object management for class City
3. `DomUtilities.java`: domain object management functionalities
4. `DomReport.java`: report object management
5. `Main.java`: console-based software
6. `MainUI.java`: GUI-based software
7. `MainSoftwareConfigure.java`: software configuration

Run these programs on Eclipse IDE or from the command line.

### Using an IDE (Preferred)
This is a preferred method as the support for command-line execution in Maven is quite limited.

Simply run each of the above programs in your IDE.

### Using the command line
Run the following programs in the listed order:

- `bin/mvn-java.bash jda.examples.courseman:jda-eg-coursemanbasics org.jda.example.courseman.software.DomainCity`
- `bin/mvn-java.bash jda.examples.courseman:jda-eg-coursemanbasics org.jda.example.courseman.software.DomStudents`
- `bin/mvn-java.bash jda.examples.courseman:jda-eg-coursemanbasics org.jda.example.courseman.software.DomUtilities`
- `bin/mvn-java.bash jda.examples.courseman:jda-eg-coursemanbasics org.jda.example.courseman.software.DomReport`
- `bin/mvn-java.bash jda.examples.courseman:jda-eg-coursemanbasics org.jda.example.courseman.software.DomReport`