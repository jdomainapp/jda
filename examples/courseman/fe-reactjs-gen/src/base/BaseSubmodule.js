import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React from "react";
import {Button, Collapse} from "react-bootstrap";
import DeleteConfirmation from "../common/DeleteConfirmation";

export default class BaseSubmodule extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      expanded: false, // default: collapsed
      onEntered: ()=> {},
    };

    this.subForms = Array()

    this.setOnEntered = this.setOnEntered.bind(this);
    this.handleExpand = this.handleExpand.bind(this);
    this.renderModule = this.renderModule.bind(this);
    this.renderExpandButton = this.renderExpandButton.bind(this);
  }

  setOnEntered(newFunc) {
    this.setState({onEntered: newFunc})
  }

  resetOnEntered() {
    this.setState({onEntered: () => {}})
  }

  handleExpand(newState) {
    this.setState({
      expanded: newState
    });
  }

  renderExpandButton() {
    return (<>
      <Button variant={this.props.compact ? "outline-secondary" : "success"}
        className={this.props.compact ? "" : "mr-1"} onClick={()=>this.handleExpand(!this.state.expanded)}>
      {this.props.compact === true ? "" : <>{this.props.title}</>}
      <FontAwesomeIcon className={this.props.compact === true ? "ml-0" : "ml-1"}
        icon={this.state.expanded === true ? faChevronUp : faChevronDown} />
    </Button>
    </>);
  }

  renderModule(props, formRef) {
    
  }

  render() {
    return (<>
      {this.props.compact ?
        <DeleteConfirmation outline label="Unlink"
          action={this.props.handleUnlink}
          readOnly={!this.props.current || this.props.current === ""}
          withoutModal /> : ""}
      {this.renderExpandButton()}
      <div id={this.props.id} onFocus = {() => {
        this.handleExpand(true)
      }} style={{height: this.state.expanded ? "fit-content" : "0", width: "100%", overflow: "hidden"}}>
          {this.renderModule(this.props, this.state.formRef)}
        </div>
    </>);
  }
}