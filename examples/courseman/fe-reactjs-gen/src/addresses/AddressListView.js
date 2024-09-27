import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";

export default class AddressListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
      <th>Name</th>
      
      {!(this.props.excludes?.includes('student')) && <th>Student</th>}
    </>);

  }
  renderRows() {
    const current = this.getPageContent();
    if (current instanceof Array) {
      return (<>
        {
          current.map((item, index) =>
            <AddressListItemView {...this.props} key={item.id} current={item}
              index={this.state.itemOffSet + index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.addressAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.addressAPI.updateById)}
              addressAPI={this.props.addressAPI}
              studentAPI={this.props.studentAPI}  />)
        }
      </>);
    }
  }
}


class AddressListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
      
      {!(this.props.excludes?.includes('student')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.student)}</td>}
    </>);
  }
}
