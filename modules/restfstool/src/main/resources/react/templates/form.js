import React from "react";
import { Button, Col, Form, FormCheck, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";
{{ view.submodule.imports }}

export default class {{ view.name.form }} extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
  }

  renderTitle() {
    return (<>
      {{ view.title }}
      </>);
  }

  renderForm() {
    {{ view.form }}
  }
}