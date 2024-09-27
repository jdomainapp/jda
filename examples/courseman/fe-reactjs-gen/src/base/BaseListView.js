import React from "react";
import { Row, Table, Form, Col } from "react-bootstrap";
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
      itemOffSet: 0,
      numRowsPerPage: this.props.numRowsPerPage && this.props.numRowsPerPage[0] ? this.props.numRowsPerPage[0] : 5
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
      this.props.handleStateChange("displayingContent", result === "" ? {} : result.content);
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

  getPageContent() {
    return this.props.displayingContent.slice(this.state.itemOffSet, this.state.itemOffSet + this.state.numRowsPerPage);
  }

  async handlePageClick(event) {
    const newOffset = (event.selected * this.state.numRowsPerPage) % this.props.displayingContent.length;
    this.setState({itemOffSet: newOffset})
  };

  render() {
    return (<>
      {this.props.displayingContent ?
      <>
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
        <Row>
          <Col>
            <ReactPaginate
                breakLabel="..."
                nextLabel="next"
                onPageChange={(e)=>this.handlePageClick(e)}
                pageRangeDisplayed={5}
                pageCount={Math.ceil(this.props.displayingContent.length / this.state.numRowsPerPage)}
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
          </Col>
          <Col>
            <Form.Select value={this.state.numRowsPerPage} onChange={(e)=>this.setState({numRowsPerPage: e.target.value})} style={{width: "150px", float: "right"}}>
              {(this.props.numRowsPerPage ? this.props.numRowsPerPage : [5, 10, 20]).map((num)=>(
                <option value={num}>{num} rows / page</option>
              ))}
            </Form.Select>
          </Col>
        </Row>
      </>
        : ""
      }
    </>)
  }
}