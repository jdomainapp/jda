import React from "react";
import { Button, Col, Container, Form, FormControl, Row } from "react-bootstrap";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faSearch } from '@fortawesome/free-solid-svg-icons';
import SockJS from "sockjs-client";
import QuickScrollFab from "../common/QuickScrollFab";
import AutoDismissAlert from "../common/AutoDismissAlert";
import DeleteConfirmation from "../common/DeleteConfirmation";
import constants from "../common/Constants";
import { StompOverWSClient } from "../common/StompClient";
import { CustomToast, ToastWrapper } from "../common/Toasts";

export default class BaseMainForm extends React.Component {
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
    this.renderObject = this.renderObject.bind(this);
    this._renderObject = this._renderObject.bind(this);

    this.setAlert = this.setAlert.bind(this);
    this.resetState = this.resetState.bind(this);
    this.filterByType = this.filterByType.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.addToastPopup = this.addToastPopup.bind(this);
    this.getCreateHandler = this.getCreateHandler.bind(this);
    this.getUpdateHandler = this.getUpdateHandler.bind(this);
    this.getPossibleTypes = this.getPossibleTypes.bind(this);
    this.onOperationFailed = this.onOperationFailed.bind(this);
    this.handleStateChange = this.handleStateChange.bind(this);
    this.onOperationSuccess = this.onOperationSuccess.bind(this);
    this.retrieveObjectById = this.retrieveObjectById.bind(this);
    this.handleDeepStateChange = this.handleDeepStateChange.bind(this);
    this.updateCurrentObjectState = this.updateCurrentObjectState.bind(this);
    this.partialApplyWithCallbacks = this.partialApplyWithCallbacks.bind(this);
  }

  // lifecycle
  componentDidMount() {
    if (this.props.parent) return;
    const socket = new SockJS(`${constants.host}/domainapp-ws`);
    const stompClient = new StompOverWSClient(socket);
    const self = this;
    stompClient.register([
      {
        endpoint: `/topic/${this.props.mainAPI.objectNamePlural}`,
        callback: (response) => {
          const message = JSON.parse(response.body).content;
          // self.addToastPopup(message, "light", () => window.location.reload());
        }
      }
    ]);
  }

  // methods for view logic
  getPossibleTypes() { }
  filterByType(type) {
    if (!this.getPossibleTypes()) return;
    this.props.mainAPI.getByPageAndType([
      1, type,
      result => {
        this.setState({ current: result })}
    ]);
  }

  resetState() {
    this.setState({
      current: {
        type: this.getPossibleTypes() ? this.getPossibleTypes()[0] : undefined
      },
      currentId: undefined
    })
  }

  getCreateHandler() {
    if (this.props.parent && this.props.parentName !== '') {
      const fn = this.partialApplyWithCallbacks(this.props.parentAPI.createInner);
      const objectNamePlural = this.props.mainAPI.objectNamePlural;
      const parentId = this.props.parentId;
      const parentName = this.props.parentName;
      const parentValue = this.props.parent;
      return function([data]) {
        data[parentName] = parentValue;
        return fn([data, objectNamePlural, parentId])
      }
    } else {
      return this.partialApplyWithCallbacks(this.props.mainAPI.create);
    }
  }

  getUpdateHandler() {
    const fn = this.partialApplyWithCallbacks(this.props.mainAPI.updateById);
    console.log(this.props);
    if (this.props.parent && this.props.parentName !== '') {
      const parentName = this.props.parentName;
      const parentValue = this.props.parent;
      return function([currentId, data]) {
        data[parentName] = parentValue;
        return fn([currentId, data]);
      }
    }
    return fn;
  }

  handleSubmit() {
    const createUsing = this.getCreateHandler();
    const updateUsing = this.getUpdateHandler();
    if (this.state.viewType === "create"
      || this.state.currentId === "" || !this.state.currentId) {
      createUsing([this.state.current]);
    } else if (this.state.viewType === "details" || this.props.mode === "submodule") {
      updateUsing([this.state.currentId, this.state.current]);
    }
  }

  handleStateChange(stateName, newValue, needsApiCall = false, onDone = undefined) {
    let newState = {};
    if (stateName.includes(".") && stateName.indexOf(".") === stateName.lastIndexOf(".")) {
      const outer = stateName.split(".")[0];
      const inner = stateName.split(".")[1];
      this.handleDeepStateChange(outer, inner, newValue, needsApiCall, onDone);
    } else {
      newState[stateName] = newValue;
    }

    if (needsApiCall) {
      const stateObjName = stateName.replace("Id", "");
      if (stateObjName.includes("current.")) {
        if (newValue === "") return;
        const shortName = stateObjName.replace("current.", "");
        this.retrieveObjectById(shortName, newValue,
          (result) => {
            newState["current"] = {...this.state.current};
            newState["current"][shortName] = result;
            console.log(newState)
            this.setState(newState, onDone);
          },
          () => {
            newState["current"] = {...this.state.current};
            newState["current"][shortName] = "";
            this.setState(newState, onDone);
          });
      } else {
        this.retrieveObjectById(stateObjName, newValue,
          (result) => { newState[stateObjName] = result; this.setState(newState, onDone); },
          () => { newState[stateObjName] = ""; this.setState(newState, onDone); });
      }
    } else {
      this.setState(newState, onDone);
    }
  }
  handleDeepStateChange(outerName, innerName, newValue, needsApiCall, onDone) {
    let outer = this.state[outerName]; outer[innerName] = newValue;
    let newState = {}; newState[outerName] = outer;
    // ignoring `needsApiCall` for simplicity
    this.setState(newState, onDone);
  }
  renderObject(propPath) {
    const realPropPath = propPath.replace("Id", ".id");
    const keys = realPropPath.split(".");
    let prop = this.props;
    for (let key of keys) {
      try {
        prop = prop[key];
      } catch (err) {
        return this._renderObject(undefined);
      }
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
        .reduce((k1, k2) => "" + k1 + "|" + k2);
    } else {
      return obj;
    }
  }
  retrieveObjectById(name, id, onSuccess, onFailure) {
    if (name === "current") {
      const className = this.constructor.name.replace("MainForm", "").replace("MainView", "");
      const propName = className.charAt(0).toLowerCase() + className.substring(1);
      if (this.props.parent && (!id || id === "")) {
        return this.props.parentAPI.getAllInner([
          this.props.thisNamePlural, this.props.parentId, onSuccess, onFailure]);
      }
      return this.retrieveObjectById(propName, id, onSuccess, onFailure);
    } else {
      const actualName = name.replace(".id", "").replace("current.", "");
      const actualAPIName = Object.keys(this.props)
                                  .filter(key => key.includes("API"))
                                  .filter(key =>
                                    key.toLocaleLowerCase()
                                      .startsWith(actualName.toLocaleLowerCase())
                                    && key.length - actualName.length <= 5)[0];
      this.props[actualAPIName].getById([id, onSuccess, onFailure]);
    }
  }

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
  // content: simple message string or JSX component
  addToastPopup(content, style, onClick, header="App Notification", timeout=10000) {
    const notiList = this.state.notifications ? this.state.notifications : [];
    const index = notiList.length;
    this.setState({
      notifications: [
        ...notiList,
        <CustomToast header={header} timeout={timeout}
            onClick={onClick} style={style}
            onClose={() => this.setState({
              notifications: this.state.notifications.splice(index, 1)
            })}
            children={content} />
      ]
    })
  }
  onOperationSuccess(result) {
    if (result instanceof String) {
      this.setAlert("danger", "Message", result);
      return;
    }
    // update UI somewhere here
    if (result) { // if not void
      this.handleStateChange("current", result === "" ? {} : result, false,
        () => this.handleStateChange("currentId", result === "" ? "" : result.id, false,
          () => this.handleStateChange("viewType", result === "" ? "create" : "details")));
    } else {
      this.handleStateChange("currentId", "", true,
          () => this.handleStateChange("viewType", this.state.viewType));
    }
    // this.handleStateChange("currentId", "", true);
    this.addToastPopup((<>
      <p>Successfully performed operation!</p>
      <Button onClick={() => window.location.reload()} size="sm">Reload</Button>
      </>), "success");
    // this.setAlert("success", "Success", "Operation completed!" + extra);
  }

  onOperationFailed(err) {
    const reason = err ? ` Reason: ${err}` : "";
    if (err.response && err.response.data) {
      this.addToastPopup((<>
        <strong className="my-1">Operation failed with error: </strong>
        <p className="my-1" style={{
          wordBreak: "break-word"
        }}>{err.response.data.message}</p>
      </>), "danger")
    }

    // this.setAlert("danger", "Failure", "Operation failed!" + reason);
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
          onClick={() => this.handleStateChange(
            "viewType", "create", false, this.resetState)}>Main</Button>
        <Button className="mr-2" variant="primary"
          onClick={() => this.handleStateChange(
            "viewType", "browse", false,
            () => this.handleStateChange("current.type", "0"))}>Browse</Button>
        {this.state.viewType === "details"
          && this.state.currentId
          && this.state.current !== "" ?
          <DeleteConfirmation action={
            () => this.partialApplyWithCallbacks(this.props.mainAPI.deleteById)([this.state.currentId])} /> : ""}
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
      {possibleTypes && possibleTypes.length > 0 && this.state.viewType === "browse" ?
        <Form.Control as="select" value={this.state.current.type} custom defaultValue="0"
          onChange={(e) => this.filterByType(e.currentTarget.value)}>
          <option value="0">&lt;--- choose a type ---&gt;</option>
          {Object.entries(possibleTypes)
            .map(([_, value]) => <option value={value}>{value}</option>)}
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
      <Button className="ml-2" onClick={this.handleSubmit}>Save</Button>
    </Row>
    </>);
	}

  render() {
    return (<>
      <Container className="border py-4">
        {this.state.alert ? this.state.alert : ""}
        {this.state.notifications && this.state.notifications.length > 0 ?
          <ToastWrapper>{this.state.notifications}</ToastWrapper> : ""}
        {this.props.compact === true ? "" :
          <>
            {this.renderTitle()}
            <br />
            {this.renderTopButtons()}
          </>
        }
        <br />
        {this.state.viewType === "browse" ? this.renderListView() : this.renderForm()}
        <br />
        {this.state.viewType === "browse" ? "" : this.renderSubmodules()}
        <br />
        {this.state.viewType === "browse" ? "" : this.renderActionButtons()}
      </Container>
      <QuickScrollFab />
    </>);
  }
}
