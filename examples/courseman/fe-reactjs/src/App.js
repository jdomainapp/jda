import React from 'react';
import {Col, Container, Row} from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import constants, {courseModules, enrolment, studentClasses, address, students} from './common/Constants';
import Navigation from './common/Navigation';

import ModuleStudent from './students'
import ModuleStudentClass from './student-classes'
import ModuleAddress from './addresses'
import ModuleEnrolment from './enrolments'
import ModuleCourseModule from './course-modules'


export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.getAppName = this.getAppName.bind(this);
    this.getModules = this.getModules.bind(this);
  }

  getWelcomeMessage() {
    return "Welcome to Course Management App: CourseMan";
  }

  getAppName() {
    return "Courseman";
  }

  getModules() {
    return [
        {
            "endpoint":"/course-modules","name":"Manage Course modules"
        },
        {
            "endpoint":"/enrolments","name":"Manage Enrolments"
        },
        {
            "endpoint":"/students","name":"Manage Students"
        },
        {
            "endpoint":"/addresses","name":"Manage Addresses"
        },
        {
            "endpoint":"/student-classes","name":"Manage Student classes"
        }
    ];
  }

  render() {
    return (<>
        <Router>
          <Navigation appName={this.getAppName()}
                      modules={this.getModules()} />
          <br />
            <Container>
                <Switch>
                    <Route path='/course-modules'><ModuleCourseModule name="course_modules" structure={courseModules} title='Manage Course modules' /></Route>
                    <Route path='/enrolments'><ModuleEnrolment name="enrolments" structure={enrolment} title='Manage Enrolments' /></Route>
                    <Route path='/students'><ModuleStudent name="students" structure={students} title='Manage Students' /></Route>
                    <Route path='/addresses'><ModuleAddress name="addresses" structure={address} title='Manage Addresses' /></Route>
                    <Route path='/student-classes'><ModuleStudentClass name="student_classes" structure={studentClasses} title='Manage Student classes' /></Route>
                    <Route path='/'>
                        <h3 className="text-center">{this.getWelcomeMessage()}</h3>
                        <br />
                        <h4 className="text-center">Select a module to continue.</h4>
                    </Route>
                </Switch>
          </Container>
        </Router>
      </>
    );
  }
}
