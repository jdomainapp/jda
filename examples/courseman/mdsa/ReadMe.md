# CourseMan MDSA example

## How to set up and run the example
1. clone/pull latest version of the the training repository.
   - The **Maven project** of the example:
      `EXAMPLE.FOLDER` = `~/jda/examples/courseman/mdsa`

2. cd into `$EXAMPLE.FOLDER`
3. compile source code: `mvn clean install`
4. generate the CourseMan source code:

   `mvn exec:java@genmds`

   - frontend source: `src/main/java/org/jda/example/coursemanmdsa/frontend`
   - backend source: `src/main/java/org/jda/example/coursemanmdsa/backend`
5. compile again (to compile the generated code): 

    `mvn compile`

6. run the backend (SpringBoot application):

    `mvn exec:java@runbe`
    
7. run the frontend (Reactjs):
   - create a project's folder from the provided template `react-proj-template.zip`:

     `unzip $EXAMPLE.FOLDER/src/main/resources/disthome/react-proj-template.zip`

   - copy the generated frontend source to the project's `src` folder. Assume you have `cd`ed into the project's folder:

     `cp -r $EXAMPLE.FOLDER/src/main/java/org/jda/example/coursemanmdsa/frontend/* src/`
    
   - from the project's folder:
     - install dependencies (only needs to do this once; takes a few minutes!):

       `npm install`

     - run the app:

       `npm run start`

Figure below shows the CourseMan frontend:

![RESTful CourseMan example](https://github.com/jdomainapp/jda/blob/main/modules/mosar/docs/images/FrontEnd-CourseMan.png)

## Study the example
1. Import the Maven project into your IDE
2. Study the frontend
   - package `org.jda.example.coursemanmdsa.frontend`
   - use the [Reactjs resources]((https://github.com/jdomainapp/training/issues/3)
3. Study the backend
   - package `org.jda.example.coursemanmdsa.backend`
   - use the [Spring Boot's resources](https://github.com/jdomainapp/training/issues/2)
