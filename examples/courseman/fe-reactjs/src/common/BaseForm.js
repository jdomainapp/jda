import React from "react";

export default class BaseForm extends React.Component {
  constructor(props) {
    super(props);
    this.renderForm = this.renderForm.bind(this);
    this.renderTitle = this.renderTitle.bind(this);
    this.renderListView = this.renderListView.bind(this);

    this.handleStateChange = this.handleStateChange.bind(this);
    this.handleDeepStateChange = this.handleDeepStateChange.bind(this);
  }

  // methods for handling view data flow
  getSubmitBody() { }
  resetState() { }
  handleSubmit() {
    this.props.handleSubmit([this.getSubmitBody()]);
    this.resetState();
  }

  renderObject(obj) {
    if (obj === null || obj === undefined) {
      return "";
    }
    if (typeof (obj) === "object") {
      return Object.keys(obj)
        .map(key => obj[key])
        .reduce((k1, k2) => "" + k1 + "-" + k2);
    } else {
      return obj;
    }
  }
  retrieveObjectById(name, id, onSuccess, onFailure) {
    if (name === "current") {
      const className = this.constructor.name.replace("MainForm", "");
      const propName = className.charAt(0).toLowerCase() + className.substring(1);
      return this.retrieveObjectById(propName, id, onSuccess, onFailure);
    } else {
      this.props[name + "API"].getById([id, onSuccess, onFailure]);
    }
  }
  handleStateChange(stateName, newValue, needsApiCall, onDone = undefined) {
    let newState = {};
    newState[stateName] = newValue;
    if (needsApiCall) {
      const stateObjName = stateName.replace("Id", "");
      this.retrieveObjectById(stateObjName, newValue,
        (result) => { newState[stateObjName] = result; this.setState(newState, onDone); },
        () => { newState[stateObjName] = ""; this.setState(newState, onDone); });
    } else {
      if (stateName.includes(".") && stateName.indexOf(".") === stateName.lastIndexOf(".")) {
        const outer = stateName.split(".")[0];
        const inner = stateName.split(".")[1];
        this.handleDeepStateChange(outer, inner, newValue, needsApiCall, onDone);
      } else {
        this.setState(newState, onDone);
      }
    }
  }
  handleDeepStateChange(outerName, innerName, newValue, needsApiCall, onDone) {
    let outer = this.state[outerName]; outer[innerName] = newValue;
    let newState = {}; newState[outerName] = outer;
    // ignoring `needsApiCall` for simplicity
    this.setState(newState, onDone);
  }

  // methods for handling presentation
  renderTitle() { }
  renderForm() { }
  renderListView() { }
  render() {
    return (<>
      {this.renderForm()}
    </>);
  }
}