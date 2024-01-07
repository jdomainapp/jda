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

    this.getSubForms = this.getSubForms.bind(this);
    this.setOnEntered = this.setOnEntered.bind(this);
    this.handleExpand = this.handleExpand.bind(this);
    this.renderModule = this.renderModule.bind(this);
    this.renderExpandButton = this.renderExpandButton.bind(this);
  }

  getSubForm(subFormId) {
    // if subform .id has dash then call getSubFormId() from target and recursive call else v
    var res = Array()
    for(var i = 0; i < this.subForms.length; i++) {
      if(this.subForms[i].props.id === subFormId) {
        res.push(this.subForms[i])
        break
      } else {
        var subRes = this.subForms[i].getSubForm(subFormId)
        if(subRes.length > 0) {
          res.push(this.subForms[i], ...subRes)
          break
        }
      }
    }
    return res
  }

  getSubForms() {
    return this.subForms
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
      <Button id={this.props.id} onFocus = {() => {
      console.log(this.props.id)
      this.handleExpand(true)
    }} variant={this.props.compact ? "outline-secondary" : "success"}
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
      <div style={{display: this.state.expanded ? "block" : "none"}}>
          {this.renderModule(this.props, this.state.formRef)}
        </div>
    </>);
  }
}