import React, {createRef} from "react";
import {Form, Container, Nav, Navbar, NavDropdown} from "react-bootstrap";
import Accordion from 'react-bootstrap/Accordion';
import Pattern from "../Pattern";

class CustomAccordionItem extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            ...this.state,
            open: this.props.open ? this.props.open : false,
            bg: "white",
            display: "block"
        }
    }

    changeBg(newColor) {
        this.setState({bg: newColor})
    }

    changeDisplay(newDisplay) {
        this.setState({display: newDisplay})
    }

    expand(newState) {
        this.setState({open: newState})
    }
    
    getSubFormIdFromTarget(id) {
        return id.split("-")
    }

    handleLinkClick() {
        if(this.props.module.endpoint[0] != '/') {
            // analize id endpoint here...
            var subFormId = this.getSubFormIdFromTarget(this.props.module.endpoint)

            var currentId = "#"
            const focusElement = (i, ids) => {
                setTimeout(()=>{
                    currentId += (i == 0 ? '' : '-') + ids[i]
                    window.location.href = currentId
                },100)
            }
            for(var i = 0; i < subFormId.length; i ++) {
                focusElement(i, subFormId)
            }
        } else {
            window.location.href = this.props.module.endpoint
        }
    }

    render() {
        return (
            <Accordion.Item key={this.props.index} eventKey={this.props.index} style={{backgroundColor: this.state.bg, display: this.state.display, borderRadius: 0, border: "none", borderLeft: "1px solid rgba(0,0,0,0.1)"}}>

                {this.props.module.subItem && this.props.module.subItem.length > 0 ?
                    <Accordion.Button style={{margin: 0,padding: 0,paddingRight: "10px", backgroundColor: "transparent", border: "none"}} onClick={()=>this.setState({open: !this.state.open})}>
                        <Nav>
                            <Nav.Link onClick={()=>this.handleLinkClick()} style={{color: "black"}}>
                                <h4>{this.props.module.name}</h4>
                            </Nav.Link>
                        </Nav>
                    </Accordion.Button>
                    :
                    <Nav>
                        <Nav.Link  onClick={()=>this.handleLinkClick()} style={{color: "black"}}>
                            <h5>{this.props.module.name}</h5>
                        </Nav.Link>
                    </Nav>
                }
                <Accordion.Collapse style={{border: "none"}} eventKey={this.props.index} in={this.state.open}>
                    <div style={{padding: "5px 0 5px 10px", backgroundColor: "white"}}>
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

class AccordionSearchableMenu extends Pattern {
    constructor(props) {
        super(props);
        this.rawStructure = this.props.modules
        this.state = {
            modules: this.initializeRef(this.rawStructure.getStructure()),
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
                        modules[i].ref.current.changeDisplay("block")
                        res = true
                    } else if (!subRes) {
                        modules[i].ref.current.changeBg("white")
                        modules[i].ref.current.changeDisplay("none")
                    } else {
                        modules[i].ref.current.changeBg("white")
                        modules[i].ref.current.changeDisplay("block")
                    }
                }
            } else {
                for(var i = 0; i < modules.length; i ++) {
                    modules[i].ref.current.expand(false)
                    this.handleSearch(keyword, modules[i].subItem)
                    modules[i].ref.current.changeBg("white")
                    modules[i].ref.current.changeDisplay("block")
                }
            }
        }
        return res;
    }

    render() {
        return (
            <div className={this.state.first == true?"position-sticky":""} style={{top: "5px"}}>
                {this.state.first == true?
                    <Form.Control style={{margin: this.props.small ? "0 10px 5px 10px" : "0", width: this.props.small ? "calc(100% - 20px)" : "100%"}} type="text" placeholder="Search categories" onChange={e=> {
                        this.lastTyped = e.target.value;
                        this.handleSearch(e.target.value, this.props.modules)
                    }}/>
                :
                    <></>
                }

                <Accordion defaultActiveKey="0" style={{border: "none"}}>
                    {this.state.modules ?
                        this.state.modules.map(
                            (module, index) =>
                                <CustomAccordionItem key={index} eventKey={index} module={module} ref={module.ref}/>
                        )
                        : ""}
                </Accordion>
            </div>
        )
    }
}

export default AccordionSearchableMenu;