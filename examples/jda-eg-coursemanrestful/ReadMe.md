# RESTful CourseMan example

## How to set up and run the example
1. clone/pull latest version of the the training repository.
   - Suppose the cloned folder is: `~/jda-training/training`.
   - The **Maven project** of the example:
      `EXAMPLE.FOLDER` = `~/jda-training/training/jda/examples/jda-eg-coursemanrestful`

2. cd into `$EXAMPLE.FOLDER`
3. compile source code: `mvn clean install`
4. generate the CourseMan RFS source code:

   `mvn exec:java@genrfs`

   - frontend source: `src/main/java/org/jda/example/coursemanrestful/frontend`
   - backend source: `src/main/java/org/jda/example/coursemanrestful/backend`
5. compile again (to compile the generated code): 

    `mvn compile`

6. run the backend (SpringBoot application):

    `mvn exec:java@runbe`
7. run the frontend (Reactjs):
   - create a project's folder from the provided template `dist/react-proj-template.zip`:

     `unzip $EXAMPLE.FOLDER/dist/react-proj-template.zip`

   - copy the generated frontend source to the project's `src` folder. Assume you have `cd`ed into the project's folder:

     `cp -r $EXAMPLE.FOLDER/src/main/java/org/jda/example/coursemanrestful/frontend/* src/`
    
   - from the project's folder:
     - install dependencies (only needs to do this once; takes a few minutes!):

       `npm install`

     - run the app:

       `PORT=5000 npm run start`

Figure below shows the CourseMan frontend:

![RESTful CourseMan example](https://github.com/jdomainapp/jda/blob/main/modules/mosar/docs/images/FrontEnd-CourseMan.png)

## Study the example
1. Import the Maven project into your IDE
2. Study the frontend
   - package `org.jda.example.coursemanrestful.frontend`
   - use the [Reactjs resources]((https://github.com/jdomainapp/training/issues/3)
3. Study the backend
   - package `org.jda.example.coursemanrestful.backend`
   - use the [Spring Boot's resources](https://github.com/jdomainapp/training/issues/2)
