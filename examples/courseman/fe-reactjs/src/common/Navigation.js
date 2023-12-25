import  React, {createRef} from 'react';
import ReactDOM, {findDOMNode} from "react-dom";
import { Link } from 'react-router-dom'
import {Form, Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import AccordionSearchableMenu from "./AccordionSearchableMenu";



class Navigation extends React.Component {

    constructor(props) {
        super(props);
        this.reff = createRef(null)
        this.state = {
            testAccItem: <AccordionSearchableMenu modules={this.props.modules}/>
        }
    }

    componentDidMount() {
        this.reff.current.style.backgroundColor = "red"
    }

    render() {
    return (
        <Navbar variant="dark" bg="dark" expand="sm" ref={this.reff}>
          <Container>
            <Nav>
              <Navbar.Brand href="/">{this.props.appName}</Navbar.Brand>
              <NavDropdown alignRight title="Go to" align="right" style={{minWidth: "300px"}}>
                  <AccordionSearchableMenu modules={this.props.modules}/>
                  {/*{this.state.testAccItem}*/}
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