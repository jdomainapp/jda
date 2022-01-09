import React from 'react';
import { Container } from 'react-bootstrap';
import { BrowserRouter as Router, Route, Switch } from 'react-router-dom';
import constants from './common/Constants';
import Navigation from './common/Navigation';
{{ view.main.imports }}

export default class App extends React.Component {
  constructor(props) {
    super(props);
    this.getAppName = this.getAppName.bind(this);
    this.getModules = this.getModules.bind(this);
  }

  getWelcomeMessage() {
    return "{{ view.main.welcome }}";
  }

  getAppName() {
    return "{{ view.main.appName }}";
  }

  getModules() {
    return {{ view.main.modules }};
  }

  render() {
    return (<>
        <Router>
          <Navigation appName={this.getAppName()}
                      modules={this.getModules()} />
          <br />
          <Container>
            <Switch>
              {{ view.main.routers }}
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