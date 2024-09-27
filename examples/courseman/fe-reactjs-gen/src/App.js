import React from 'react';
import {Col, Container, Row} from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import Navigation from './common/Navigation';

import ModuleStudentClass from './student-classes'
import ModuleAddress from './addresses'
import ModuleCourseModule from './course-modules'
import ModuleEnrolment from './enrolments'
import ModuleStudent from './students'
import StructureConstructor from './common/patterns/accordion/accordion';
export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.getAppName = this.getAppName.bind(this);
    this.getModules = this.getModules.bind(this);
  }

  getWelcomeMessage() {
    return "Welcome to Courseman App!";
  }

  getAppName() {
    return "Courseman";
  }

  getModules() {
    return [
        {"endpoint":"/student-classes","name":"Manage Student classes"},
        {"endpoint":"/addresses","name":"Manage Addresses"},
        {"endpoint":"/course-modules","name":"Manage Course modules"},
        {"endpoint":"/enrolments","name":"Manage Enrolments"},
        {"endpoint":"/students","name":"Manage Students"},
    ];
  }

  render() {
    return (<>
        <Router>
          <Navigation appName={this.getAppName()}
                      modules={new StructureConstructor("",this.getModules())} />
          <br />
          <Container>
            <Switch>
              <Route path='/student-classes'><ModuleStudentClass title='Manage Student classes' /></Route>
              <Route path='/addresses'><ModuleAddress title='Manage Addresses' /></Route>
              <Route path='/course-modules'><ModuleCourseModule title='Manage Course modules' /></Route>
              <Route path='/enrolments'><ModuleEnrolment title='Manage Enrolments' /></Route>
              <Route path='/students'><ModuleStudent title='Manage Students' /></Route>
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
