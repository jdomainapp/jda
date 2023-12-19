import React, {createRef} from "react";
import {findDOMNode} from "react-dom";
import {Form, Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import Accordion from 'react-bootstrap/Accordion';
import {useAccordionButton} from "react-bootstrap";

class CustomAccordionItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ...this.state,
            open: this.props.open ? this.props.open : false,
            bg: "white"
        }
    }

    changeBg(newColor) {
        this.setState({bg: newColor})
    }

    expand(newState) {
        this.setState({open: newState})
    }

    render() {
        return (
            <Accordion.Item key={this.props.index} eventKey={this.props.index} style={{backgroundColor: this.state.bg}}>

                {this.props.module.subItem && this.props.module.subItem.length > 0 ?
                    <Accordion.Button style={{margin: 0,padding: 0,paddingRight: "10px", backgroundColor: "transparent"}} onClick={()=>this.setState({open: !this.state.open})}>
                        <Nav>
                            <Nav.Link href={this.props.module.endpoint} style={{color: "black"}}>
                                <h4>{this.props.module.name}</h4>
                            </Nav.Link>
                        </Nav>
                    </Accordion.Button>
                    :
                    <Nav>
                        <Nav.Link href={this.props.module.endpoint} style={{color: "black"}}>
                            <h4>{this.props.module.name}</h4>
                        </Nav.Link>
                    </Nav>
                }
                <Accordion.Collapse eventKey={this.props.index} in={this.state.open}>
                    <div style={{padding: "10px", backgroundColor: "white"}}>
                        {this.props.module.subItem ?
                            <AccordionSearchableMenu modules={this.props.module.subItem} isSub/>
                            : ""
                        }
                    </div>
                </Accordion.Collapse>

            </Accordion.Item>
        )
    }
}

class AccordionSearchableMenu extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            modules: this.initializeRef(this.props.modules),
            first: this.props.isSub ? false : true
        }
        this.ready = true;
        this.lastSearched = ""
        this.lastTyped = ""
        setInterval(()=>{
            this.ready = true;
            if(this.lastSearched != this.lastTyped)  this.setState({modules: this.handleSearch(this.lastTyped, this.props.modules)});
        },100);
    }

    initializeRef(modules) {
        if(modules) {
            for(var i = 0; i < modules.length; i ++) {
                this.initializeRef(modules[i].subItem);
                modules[i].ref = createRef(null);
            }
        }
        return modules
    }

    componentDidMount() {
        console.log(this.state.modules)
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
        var res = false;
        if(modules) {
            if(keyword != "") {
                var newList = Array();
                for(var i = 0; i < modules.length; i ++) {
                    const subRes = this.handleSearch(keyword, modules[i].subItem)
                    if(subRes) res = true
                    modules[i].ref.current.expand(subRes)
                    if(this.isRelativeSubstring(modules[i].name, keyword)) {
                        modules[i].ref.current.changeBg("#E7F1FF")
                        res = true
                    } else {
                        modules[i].ref.current.changeBg("white")
                    }
                }
            } else {
                for(var i = 0; i < modules.length; i ++) {
                    modules[i].ref.current.expand(false)
                    this.handleSearch(keyword, modules[i].subItem)
                    modules[i].ref.current.changeBg("white")
                }
            }
        }
        return res;
    }

    render() {
        return (
            <>
                {this.state.first == true?
                    <Form.Control style={{margin: "0 10px 5px 10px", width: "calc(100% - 20px)"}} type="text" placeholder="Search categories" onChange={e=> {
                        this.lastTyped = e.target.value;
                        this.handleSearch(e.target.value, this.props.modules)
                    }}/>
                :
                    <></>
                }

                <Accordion defaultActiveKey="0">
                    {this.state.modules ?
                        this.state.modules .map(
                            (module, index) =>
                                <CustomAccordionItem key={index} eventKey={index} module={module} ref={module.ref}/>
                        )
                        : ""}
                </Accordion>
            </>
        )
    }
}

export default AccordionSearchableMenu;