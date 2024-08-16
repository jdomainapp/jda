import React from "react";
import { Table } from "react-bootstrap";

export default class BaseListView extends React.Component {
  constructor(props) {
    super(props);
    this.renderRows = this.renderRows.bind(this);
    this.renderVisibleColumns = this.renderVisibleColumns.bind(this);
  }

  renderVisibleColumns() { }
  renderRows() { }

  render() {
    return (<>
      {this.props.current.content ?
        <Table bordered hover>
          <thead>
            <tr>
              <th>#</th>
              {this.renderVisibleColumns()}
              <th></th>
            </tr>
          </thead>
          <tbody>
            {this.renderRows()}
          </tbody>
        </Table>
        : ""
      }
    </>)
  }
}