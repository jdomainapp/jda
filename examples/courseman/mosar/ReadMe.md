RESTful FullStack CourseMan software
--------------------------------------

# Prerequisites
1. clone/pull latest version of the the training repository.
   - The **Maven project** of the example:
     `EXAMPLE.FOLDER` = `~/jda/examples/courseman/mosar`

2. cd into `$EXAMPLE.FOLDER`
3. compile source code: `mvn clean install`

The benchmark frontend software are stored in the subdirectories:
- `fe-reactjs`: Reactjs version of the software
- `fevuejs`: Vuejs version of the software
- `feangular`: (**In progress**) Angular version of the software 
- `fereactnative`: ReactNative version of the software

## Reactjs

**Module Student: Object table**

![Module Student: Object table](docs/images/fereactjs-module-student-browse.png)

**Module Student: Read/Create/Edit object form**

![Module Student: CRUD](docs/images/fereactjs-module-student.png)

## Vuejs 
**Module Student: Object table**

![Module Student: Object table](docs/images/fevuejs-module-student-browse.png)

**Module Student: CRUD form**

![Module Student: CRUD form](docs/images/fevuejs-module-student.png)

# Benchmark software examples
1. run the backend (SpringBoot application):

   `mvn exec:java@runbe`

2. run the frontend (Reactjs, Vuejs or ReactNative)
- cd into the subfolder of the benchmark software (see Prerequisites)
- install dependencies (only needs to do this once; takes a few minutes!):
  `npm install`

**Reactjs**
- run the app: `PORT=<your client port> npm run start`

**VueJs**
- run the app: `npm run serve --port=<your client port>`

**Angular**
- run the app: `ng serve --port <your client port> ` (You might be install angular cli first: `npm install -g @angular/cli`)

**REACTNATIVE**
- Refer to the `Readme.md` file in the folder.

# SwGen example
Use SwGen to generate a frontend software in one of the target platforms (Reactjs, Vuejs, Angular, ReactNative).

1. Generate the CourseMan RFS source code:
   `mvn exec:java@genrfs`
   - frontend source: `src/main/java/org/jda/example/coursemanrestful/frontend`
   - backend source: `src/main/java/org/jda/example/coursemanrestful/backend`

   * (Option) Chose Frontend technology : (VUEJS | ANGULAR | REACTJS | REACTNATIVE) by config value `@RFSGenDesc.fePlatform`
      in `"EXAMPLE.FOLDER"\src\main\java\org\jda\example\coursemanrestful\software\config\SCCCourseManDerby.java`

2. compile again (to compile the generated code): 

    `mvn compile`

3. run the backend (SpringBoot application):

    `mvn exec:java@runbe`
    
4. run the frontend 
  - from the frontend source folder in step 1.:
  - install dependencies (only needs to do this once; takes a few minutes!):
    `npm install`
  * (Reactjs):
  - run the app: `PORT=<your client port> npm run start`

  * (VueJs)
  - run the app: `npm run serve --port=<your client port>`  

  * (Angular):
  - run the app: `ng serve --port <your client port> ` (You might be install angular cli first: `npm install -g @angular/cli`)

  * (REACTNATIVE):
  - Refer Readme.md in `frontend source` folder in step 1.
