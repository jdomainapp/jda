import React from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faChevronDown, faChevronUp } from '@fortawesome/free-solid-svg-icons';
import { Button } from "react-bootstrap";
import BaseForm from "./BaseForm";

export default class BaseSubmodule extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      expanded: false // default: collapsed
    };

    this.handleExpand = this.handleExpand.bind(this);
    this.renderForm = this.renderForm.bind(this);
    this.renderExpandButton = this.renderExpandButton.bind(this);
  }

  handleExpand() {
    const expanded = this.state.expanded;
    this.setState({
      expanded: !expanded
    });
  }
  renderExpandButton() {
    return (<>
      <Button variant="success" className="mr-1" onClick={this.handleExpand}>
      {this.props.title}
      <FontAwesomeIcon className="ml-1"
        icon={this.state.expanded === true ? faChevronUp : faChevronDown} />
    </Button>
    </>);
  }

  render() {
    return (<>
      {this.renderExpandButton()}
      {this.state.expanded ?
        (this.props.viewType === "details" ? this.renderListView() : this.renderForm())
        : ""}
    </>);
  }
}