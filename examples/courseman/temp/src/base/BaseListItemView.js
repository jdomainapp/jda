import React from "react";
import { Button, Modal } from "react-bootstrap";
import DeleteConfirmation from "../common/DeleteConfirmation";

export default class BaseListItemView extends React.Component {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state,
      modalShowing: undefined
    };
    this.changeCurrent = this.changeCurrent.bind(this);
  }
  verticalAlignCell = { verticalAlign: "middle" };
  pointerCursor = { cursor: "pointer" };

  renderVisibleColumns() { }

  changeCurrent() {
    console.log(this.props.currentId);
    this.props.changeToDetailsView();
    this.props.handleStateChange(
      "currentId", this.props.currentId, true);
  }

  partialApplyWithCallbacks(func) {
    return this.props.partialApplyWithCallbacks(func);
  }

  renderObject(obj) {
    if (obj === null || obj === undefined) {
      return "";
    }
    if (typeof (obj) === "object") {
      // return JSON.stringify(obj);
      return Object.keys(obj)
        .map(key => obj[key])
        .reduce((k1, k2) => "" + k1 + " | " + k2);
    } else {
      return obj;
    }
  }

  render() {
    return (<>
      <tr style={this.pointerCursor}>
        <td style={this.verticalAlignCell}
          onClick={this.changeCurrent}>{this.props.index}</td>
        {this.renderVisibleColumns()}
        <td style={this.verticalAlignCell}>
          <DeleteConfirmation action={() => this.props.handleDelete([this.props.current.id])} />
        </td>
      </tr>
    </>)
  }
}