# RESTful CourseMan example

## How to set up and run the example
1. clone/pull latest version of the the training repository.
   - The **Maven project** of the example:
      `EXAMPLE.FOLDER` = `~/jda/examples/courseman/mosar`

2. cd into `$EXAMPLE.FOLDER`
3. compile source code: `mvn clean install`
4. generate the CourseMan RFS source code:
   `mvn exec:java@genrfs`
   - frontend source: `src/main/java/org/jda/example/coursemanrestful/frontend`
   - backend source: `src/main/java/org/jda/example/coursemanrestful/backend`

   * (Option) Chose Frontend technology : (VUEJS | ANGULAR | REACTJS | REACTNATIVE) by config value `@RFSGenDesc.fePlatform`
      in `"EXAMPLE.FOLDER"\src\main\java\org\jda\example\coursemanrestful\software\config\SCCCourseManDerby.java`
5. compile again (to compile the generated code): 

    `mvn compile`

6. run the backend (SpringBoot application):

    `mvn exec:java@runbe`
    
7. run the frontend 
  - from the frontend source folder in step 4.:
  - install dependencies (only needs to do this once; takes a few minutes!):
    `npm install`
  * (Reactjs):
  - run the app: `PORT=<your client port> npm run start`

  * (VueJs)
  - run the app: `npm run serve --port=<your client port>`  

  * (Angular):
  - run the app: `ng serve --port <your client port> ` (You might be install angular cli first: `npm install -g @angular/cli`)

  * (REACTNATIVE):
  - Refer Readme.md in `frontend source` folder in step 4.
