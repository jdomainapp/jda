import React, {createRef} from "react";
import {Form, Container, Nav, Navbar, NavDropdown,Collapse, Button} from "react-bootstrap";
import Accordion from 'react-bootstrap/Accordion';
import Pattern from "../Pattern";
import StructureConstructor from "./accordion";
import arrowdown from "./arrowdown.svg"

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

            this.props.mainMenu.setHighlight(this.props.module.endpoint)
        } else {
            window.location.href = this.props.module.endpoint
        }
    }

    render() {
        return (
            <div ref={this.props.module.ref}
            style={{
                display: this.state.display, borderRadius: 0, border: "none",
            }}
            >
                <div style={{
                    transition: "0.2s ease-in-out",
                    backgroundColor: this.props.mainMenu.getHighlight() === this.props.module.endpoint ? "#E7F1FF" : this.state.bg != "white" ? this.state.bg : (this.state.open ? "rgba(0,0,0,0.1)" : "white"),
                    display: "flex",
                    padding: "0 10px",
                    justifyContent: "space-between",
                    alignItems: "center",
                    height: "40px"
                }}>
                    <a onClick={()=>this.handleLinkClick()} style={{color: "black", cursor: "pointer"}}>
                        {this.props.module.name}
                    </a>
                    {this.props.module.subItem && this.props.module.subItem.length > 0 ?
                        <Button
                            style={{display: "flex", margin: 0,height: "30px", width: "30px", alignItems: "center", justifyContent: "center", border: "none", backgroundColor: "transparent"}}
                            onClick={()=>this.setState({open: !this.state.open})}
                        >

                            <img style={{height: "20px", width: "20px", transition: "0.2s ease-in-out",transformOrigin: "center center", transform: this.state.open ? "rotate(180deg)" : "rotate(0deg)"}}
                            src={arrowdown} alt="drop"/>
                        </Button>
                    : <></>}
                </div>
                {this.props.module.subItem && this.props.module.subItem.length > 0 ?
                    <Collapse style={{border: "none", marginLeft: "10px"}} in={this.state.open}>
                        <div>
                            {this.props.module.subItem ?
                                <Menu mainMenu={this.props.mainMenu} structure={this.props.module.subItem} isSub/>
                                : ""
                            }
                        </div>
                    </Collapse>
                : <></>}

            </div>
        )
    }
}

class Menu extends React.Component {
    constructor(props) {
        super(props);
        this.rawStructure = this.props.modules
        this.modules = this.props.modules !== undefined || this.props.structure !== undefined ? (this.props.modules !== undefined ? this.initializeRef(this.rawStructure.getStructure()) : this.props.structure) : []
        this.state = {
            first: this.props.isSub ? false : true,
            highlighting: "",
        }
        this.ready = true;
        this.lastSearched = ""
        this.lastTyped = ""
        this.mainMenu = this.props.mainMenu ? this.props.mainMenu : this
        setInterval(()=>{
            this.ready = true;
            if(this.lastSearched != this.lastTyped)  this.setState({modules: this.handleSearch(this.lastTyped, this.props.modules)});
        },100);
    }

    componentDidMount() {
        this.setHighlight = this.setHighlight.bind(this)
        this.getHighlight = this.getHighlight.bind(this)
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
                    if(!modules[i].ref.current) console.log(modules[i].endpoint)
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

    setHighlight(id) {
        if(this.mainMenu === this) {
            this.setState({highlighting: id})
        } else {
            this.mainMenu.setHighlight(id)
        }
    }

    getHighlight() {
        if(this.mainMenu === this) {
            return this.state.highlighting
        } else {
            return this.mainMenu.getHighlight()
        }
    }

    render() {
        return (
            <div style={
                this.state.first == true ? {
                    top: "5px",
                    position: "sticky",
                } : {borderLeft: "1px solid rgba(0,0,0,0.1)"}
                }>
                {this.state.first == true?
                    <Form.Control style={{margin: this.props.small ? "0 10px 5px 10px" : "0 0 5px 0", width: this.props.small ? "calc(100% - 20px)" : "100%"}} type="text" placeholder="Search categories"
                    onChange={e=> {
                        this.lastTyped = e.target.value;
                        this.handleSearch(e.target.value, this.modules)
                    }}/>
                :
                    <></>
                }
                <div style={this.state.first == true ? {
                        height: "fit-content",
                        maxHeight: "calc(100vh - 150px)",
                        overflowY: "auto"
                    }: {}}>

                    {this.modules ?
                        this.modules.map(
                            (module) =>
                                <CustomAccordionItem mainMenu={this.mainMenu} module={module} ref={module.ref}/>
                        )
                        : ""
                    }
                </div>
            </div>
        )
    }
}

class AccordionSearchableMenu extends Pattern {
    constructor(props)  {
        super(props)
        this.rawStructure = this.props.modules
    }

    render() {
        return <Menu {...this.props}/>
    }
}

export default AccordionSearchableMenu;