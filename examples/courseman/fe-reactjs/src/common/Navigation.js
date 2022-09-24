import React from 'react';
import { Container, Nav, Navbar, NavDropdown } from "react-bootstrap";

class Navigation extends React.Component {
  render() {
    return (
      <Navbar variant="dark" bg="dark" expand="sm">
        <Container>
          <Nav>
            <Navbar.Brand href="/">{this.props.appName}</Navbar.Brand>
            <NavDropdown alignRight title="Go to" align="right">
              {this.props.modules ? 
                this.props.modules.map((module, index) => 
                <NavDropdown.Item key={index} eventKey={index}
                  href={module.endpoint}>{module.name}</NavDropdown.Item>) : ""}
            </NavDropdown>
          </Nav>
          <Nav>
            <Nav.Link>Help</Nav.Link>
          </Nav>
        </Container>
      </Navbar>
    )
  }
}

export default Navigation;