import React from "react";
import { Table } from "react-bootstrap";

export default class BaseListView extends React.Component {
  constructor(props) {
    super(props);
    this.parentId = this.props.parentId
    this.renderRows = this.renderRows.bind(this);
    this.renderVisibleColumns = this.renderVisibleColumns.bind(this);
    this.retrieveListData = this.retrieveListData.bind(this);
  }

  renderVisibleColumns() { }
  renderRows() { }
  
  retrieveListData() {
    const onApiCallDone = (result) => {
      this.parentId = this.props.parentId;
      this.props.handleStateChange("current", result);
      // this.props.handleStateChange("currentId", undefined);
    }
    if (this.props.parent && this.props.parentId) {
      this.props.parentAPI.getAllInner([
        this.props.mainAPI.objectNamePlural, this.props.parentId, onApiCallDone])
    } else if (!this.props.current.content) {
      this.props.handleStateChange("currentId", "", true);
    }
  }

  componentDidMount() {
    this.retrieveListData();
  }

  componentDidUpdate() {
    if (this.props.parentId 
      && this.props.parentId !== ""
      && this.props.parentId === this.parentId) return;
    if (this.props.current 
      && this.props.current.pageCount !== undefined) return;
    this.retrieveListData();
  }

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