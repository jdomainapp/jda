import React from "react";
import { Button, Col, Container, Form, FormControl, Row } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import QuickScrollFab from "../common/QuickScrollFab";
import BaseForm from "./BaseForm";
import AutoDismissAlert from "../common/AutoDismissAlert";
import DeleteConfirmation from "../common/DeleteConfirmation";

export default class BaseMainForm extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      current: {}, // list or single object
      viewType: props.viewType ? props.viewType : "create", // create | details | browse (list) | submodule
      currentId: undefined, // filtered ID
      searchInput: undefined // input for search box
    };
    // method binding
    this.renderActionButtons = this.renderActionButtons.bind(this);
    this.renderSubmodules = this.renderSubmodules.bind(this);
    this.renderNavigationButtons = this.renderNavigationButtons.bind(this);
    this.renderSearchInput = this.renderSearchInput.bind(this);
    this.renderTopButtons = this.renderTopButtons.bind(this);

    this.setAlert = this.setAlert.bind(this);
    this.filterByType = this.filterByType.bind(this);
    this.getPossibleTypes = this.getPossibleTypes.bind(this);
    this.onOperationFailed = this.onOperationFailed.bind(this);
    this.onOperationSuccess = this.onOperationSuccess.bind(this);
    this.updateCurrentObjectState = this.updateCurrentObjectState.bind(this);
    this.partialApplyWithCallbacks = this.partialApplyWithCallbacks.bind(this);
  }

  // methods for view logic
  getPossibleTypes() { }
  filterByType() { }

  updateCurrentObjectState(evt) {
    this.setState({ currentId: evt.target.value },
      function () {
        this.handleStateChange("currentId", this.state.currentId, true,
          function () {
            if (this.state.currentId && this.state.current !== {} 
                && !(this.state.current instanceof Array)) {
              this.handleStateChange("viewType", "details");
            } else {
              this.handleStateChange("viewType", "create");
            }
          });
      });
  }
  setAlert(variant, heading, text,
    onDisposed = () => this.setState({ alert: undefined })) {
    this.setState({
      alert: <AutoDismissAlert variant={variant}
        heading={heading} text={text} onDisposed={onDisposed} />
    })
  }
  onOperationSuccess(result) {
    const extra = result && !(result instanceof(Response)) ?
        ` Affected: ${this.renderObject(result)}!` : "";
    // update UI somewhere here
    this.setAlert("success", "Success", "Operation completed!" + extra);
  }

  onOperationFailed(err) {
    const reason = err ? ` Reason: ${err}` : "";
    this.setAlert("danger", "Failure", "Operation failed!" + reason);
  }

  setListFromPage(page) {
    this.setState({
      list: page.content
    });
  }

  partialApplyWithCallbacks(func) {
    return args => {
      const oldArgs = args ? args : [];
      return func([...oldArgs, this.onOperationSuccess, this.onOperationFailed]);
    }
  }

  // base methods for drawing view
  renderSubmodules() { }

  renderNavigationButtons() {
    return (<>
      <Col className="px-0">
        <Button className="mr-2" variant="primary"
          onClick={() => this.handleStateChange("viewType", "create")}>Main</Button>
        <Button className="mr-2" variant="primary"
          onClick={() => this.handleStateChange("viewType", "browse")}>Browse</Button>
        {this.state.viewType === "details" ? <DeleteConfirmation /> : ""}
      </Col>
    </>);
  }
  renderIdInput() {
    return (<>
      <FormControl type="text" placeholder="ID..."
        onChange={this.updateCurrentObjectState}
        className="mr-1 col-md-4" value={this.state.currentId} />
    </>);
  }
  renderSearchInput() {
    return (<>
      <FormControl type="text" placeholder="Search"
        className="mr-1 col-md-6" value={this.state.searchInput} />
      <Button variant="outline-success">
        <FontAwesomeIcon icon={faSearch} />
      </Button>
    </>);
  }
  renderTypeDropdown() {
    const possibleTypes = this.getPossibleTypes();
    return (<>
      {possibleTypes ?
        <Form.Control as="select" value={this.state.type} custom defaultValue="0"
          onChange={(e) => this.setState({ type: e.target.value })}>
          <option value="0">&lt;--- choose a type ---&gt;</option>
          {Object.entries(possibleTypes)
            .map(([key, value]) => <option value={key}>{value}</option>)}
        </Form.Control> : ""}
    </>);
  }
  renderTopButtons() {
    return (<>
      <Row className="mx-0 d-flex justify-content-between">
        {this.renderNavigationButtons()}
        <Col className="px-0 d-flex justify-content-end">
          <Form className="d-flex justify-content-between" inline>
            {this.renderTypeDropdown()}
            {this.renderIdInput()}
            {this.renderSearchInput()}
          </Form>
        </Col>
      </Row>
    </>);
  }
  renderActionButtons() {
    return (<>
    <Row className="d-flex justify-content-end mx-0">
      <Col md={9} />
      <Button variant="secondary" onClick={this.resetState}>Reset</Button>
      <Button className="ml-2" onClick={this.handleSubmit}>Submit</Button>
    </Row>
    </>);
	}

  render() {
    return (<>
      <Container>
        {this.state.alert ? this.state.alert : ""}
        {this.renderTitle()}
        <br />
        {this.renderTopButtons()}
        <br />
        {this.state.viewType === "browse" ? this.renderListView() : this.renderForm()}
        <br />
        {this.state.viewType === "browse" ? "" : this.renderSubmodules()}
        <br />
        {this.state.viewType === "browse" ? "" : this.renderActionButtons()}
        <br />
      </Container>
      <QuickScrollFab />
    </>);
  }
}