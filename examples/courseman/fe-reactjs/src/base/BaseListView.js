import React from "react";
import { Table } from "react-bootstrap";
import ReactPaginate from 'react-paginate';
import './PaginateStyle.css';


export default class BaseListView extends React.Component {
  constructor(props) {
    super(props);
    this.parentId = this.props.parentId
    this.renderRows = this.renderRows.bind(this);
    this.renderVisibleColumns = this.renderVisibleColumns.bind(this);
    this.retrieveListData = this.retrieveListData.bind(this);
    this.state = {
      ...this.state,
    }
  }

  componentDidMount() {
      this.retrieveListData();
  }

  renderVisibleColumns() { }
  renderRows() { }
  
  retrieveListData() {
    const onApiCallDone = (result) => {
      this.parentId = this.props.parentId;
      this.props.handleStateChange("current", result === "" ? {} : result);
      // this.props.handleStateChange("currentId", undefined);

    }
    if (this.props.parent && this.props.parentId) {
      this.props.parentAPI.getAllInner([
        this.props.mainAPI.objectNamePlural, this.props.parentId, onApiCallDone])
    } else if (!this.props.current.content) {
      this.props.handleStateChange("currentId", "", true);
    }
  }

  componentDidUpdate() {
    if (this.props.parentId 
      && this.props.parentId !== ""
      && this.props.parentId === this.parentId) return;
    if (this.props.current 
      && this.props.current.pageCount !== undefined) return;
    this.retrieveListData();
  }


  async handlePageClick(event) {
    const newOffset = (event.selected * this.props.numRowsPerPage) % this.props.current.content.length;
    await this.props.handleStateChange("itemOffSet", newOffset)
    await this.props.handleStateChange("displayingContent",
        this.props.current.content.slice(this.props.itemOffSet, this.props.itemOffSet + this.props.numRowsPerPage))
  };

  render() {
    return (<>
      {this.props.current.content && this.props.displayingContent ?
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
          <ReactPaginate
              breakLabel="..."
              nextLabel="next"
              onPageChange={(e)=>this.handlePageClick(e)}
              pageRangeDisplayed={5}
              pageCount={Math.ceil(this.props.current.content.length / this.props.numRowsPerPage)}
              previousLabel="previous"
              renderOnZeroPageCount={null}
              pageClassName="page-item"
              pageLinkClassName="page-link"
              previousClassName="page-item"
              previousLinkClassName="page-link"
              nextClassName="page-item"
              nextLinkClassName="page-link"
              breakClassName="page-item"
              breakLinkClassName="page-link"
              containerClassName="pagination"
              activeClassName="active"
          />
        </Table>
        : ""
      }
    </>)
  }
}