import React from 'react';
import { Link } from 'react-router-dom'
import {Form, Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import Accordion from 'react-bootstrap/Accordion';



class Navigation extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            modules: this.props.modules
        }
        this.ready = true;
        this.lastSearched = ""
        this.lastTyped = ""
        setInterval(()=>{
            this.ready = true;
            if(this.lastSearched != this.lastTyped)  this.setState({modules: this.handleSearch(this.lastTyped, this.props.modules)});
        },100);
    }

  renderModules(modules) {
    return (
      <>
        <Accordion defaultActiveKey="0">
          {modules ?
              modules.map(
                  (module, index) =>
                      <Accordion.Item key={index} eventKey={index}>

                          {module.subItem && module.subItem.length > 0 ?
                              <Accordion.Button style={{margin: 0,padding: 0,paddingRight: "10px"}}>
                                  <Nav>
                                      <Nav.Link href={module.endpoint} style={{color: "black"}}>
                                          <h4>{module.name}</h4>
                                      </Nav.Link>
                                  </Nav>
                              </Accordion.Button>
                              :
                              <Nav>
                                  <Nav.Link href={module.endpoint} style={{color: "black"}}>
                                    <h4>{module.name}</h4>
                                  </Nav.Link>
                              </Nav>
                          }
                        <Accordion.Body>
                          {module.subItem ?
                              this.renderModules(module.subItem)
                              : ""
                          }
                        </Accordion.Body>

                      </Accordion.Item>
              )
              : ""}
        </Accordion>
      </>
    )
  }


    isRelativeSubstring(longString, shortString) {
        let longIndex = 0;
        let shortIndex = 0;

        while (longIndex < longString.length && shortIndex < shortString.length) {
            if (longString[longIndex] === shortString[shortIndex]) {
                shortIndex++;
            }
            longIndex++;
        }

        return shortIndex === shortString.length;
    }

  handleSearch(keyword, modules) {
    this.ready = false;
    this.lastSearched = keyword;
    if(modules) {
        if(keyword == "") {
            return this.props.modules;
        } else {
            var newList = Array();
            for(var i = 0; i < modules.length; i ++) {
                var subItems = this.handleSearch(keyword, modules[i].subItem);
                for(var j = 0; j < subItems.length; j++) {
                    newList.push(subItems[j]);
                }
                if(this.isRelativeSubstring(modules[i].name, keyword)) {
                    newList.push(modules[i]);
                }
            }
            console.log(newList)
            return newList;
        }
    } else {
        return Array();
    }
  }

  render() {
    return (
        <Navbar variant="dark" bg="dark" expand="sm">
          <Container>
            <Nav>
              <Navbar.Brand href="/">{this.props.appName}</Navbar.Brand>
              <NavDropdown alignRight title="Go to" align="right" style={{minWidth: "300px"}}>
                  <Form.Control style={{margin: "0 10px 5px 10px", width: "calc(100% - 20px)"}} type="text" placeholder="Search categories" onChange={e=>{
                      if(this.ready) this.setState({modules: this.handleSearch(e.target.value, this.props.modules)});
                      this.lastTyped = e.target.value;
                  }}/>
                  {this.renderModules(this.state.modules)}
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