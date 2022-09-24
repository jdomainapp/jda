import React from "react";
import { Button, Modal } from "react-bootstrap";
import DeleteConfirmation from "../common/DeleteConfirmation";
import BaseForm from "./BaseForm";

export default class BaseListItemView extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state,
      modalShowing: undefined
    };
    this.handleShow = this.handleShow.bind(this);
    this.handleHide = this.handleHide.bind(this);
  }
  verticalAlignCell = { verticalAlign: "middle" };
  pointerCursor = { cursor: "pointer" };

  renderVisibleColumns() { }

  partialApplyWithCallbacks(func) {
    return this.props.partialApplyWithCallbacks;
  }

  handleShow() {
    this.setState({ modalShowing: true });
  }

  handleHide() {
    this.setState({ modalShowing: false });
  }

  render() {
    return (<>
      <tr style={this.pointerCursor}>
        <td style={this.verticalAlignCell} onClick={this.handleShow}>{this.props.index}</td>
        {this.renderVisibleColumns()}
        <td style={this.verticalAlignCell}>
          <DeleteConfirmation action={() => this.props.handleDelete([this.props.id])} />
        </td>
      </tr>
      { this.state.modalShowing === false ? "" :
        <Modal show={this.state.modalShowing} onHide={this.handleHide} size="lg">
          <Modal.Header closeButton={true}>
            <Modal.Title>
              {this.renderTitle()}
            </Modal.Title>
          </Modal.Header>
          <Modal.Body>
            {this.renderForm()}
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={this.handleHide}>Cancel</Button>
            <Button onClick={this.handleSubmit}>Submit</Button>
          </Modal.Footer>
        </Modal>
      }
    </>)
  }
}