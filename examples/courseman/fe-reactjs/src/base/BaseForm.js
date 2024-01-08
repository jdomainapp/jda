import React from "react";

export default class BaseForm extends React.Component {
  constructor(props) {
    super(props);
    this.renderObject = this.renderObject.bind(this);
    this._renderObject = this._renderObject.bind(this);
    this.renderForm = this.renderForm.bind(this);
    this.renderTitle = this.renderTitle.bind(this);
    this.renderListView = this.renderListView.bind(this);

  }

  componentDidMount() {
    this.props.handleStateChange("inputState", this.getInputState(), false)
  }

  // methods for handling view data flow
  getSubmitBody() { }
  resetState() { }
  handleSubmit() {
    this.props.handleSubmit([this.getSubmitBody()]);
    this.resetState();
  }

  getInputState() {
    return {}
  }

  async validate(value, name) {
    var newInputState = this.props.inputState
    if(newInputState[name]) {
      if(newInputState[name].regex.test(value)) {
        newInputState[name].validated = true
        newInputState[name].message = newInputState[name].validMsg
      } else {
        newInputState[name].validated = false
        newInputState[name].message = newInputState[name].invalidMsg
      }
  
      await this.props.handleStateChange("inputState", newInputState, false)
    }

    var formValidated = true
    Object.entries(newInputState).forEach((val) => {
      if(formValidated) {
        if (val[1].optional) {
          if (val[1].validated === false) {
            formValidated = false
          }
        } else {
          if (val[1].validated === false || val[1].validated === undefined) {
            formValidated = false
          }
        }
      }
    })

    this.props.handleStateChange("readySubmit", formValidated, false)
  }

  renderObject(propPath) {
    const keys = propPath.split(".");
    let prop = this.props;
    for (let key of keys) {
      try {
        prop = prop[key];
      } catch (err) {
        return this._renderObject(undefined);
      }
    }
    if (propPath.includes("Id")) {
      const idFromObj = this.renderObject(propPath.replace("Id", ".id"));
      if (idFromObj !== "") return idFromObj;
    }
    return this._renderObject(prop);
  }
  _renderObject(obj) {
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
  
  onModelRegionMenuItem(consumerName) {
    var res = {}
    for(var i = 0; i < this.props.mainForm.consumers.length; i ++) {
      if (this.props.mainForm.consumers[i].name == consumerName) {
        let val = this.props.mainForm.consumers[i].onModelRegion("menuItem")
        if(val) res[this.props.mainForm.consumers[i].name] = val
      }
    }
    return res[consumerName]
  }

  // methods for handling presentation
  renderTitle() { }
  renderForm() { }
  renderListView() { }
  render() {
    // if(this.props.structure) this.props.structure.resetItr()
    return (<>
      {this.renderForm()}
    </>);
  }
}