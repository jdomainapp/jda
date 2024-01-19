import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";

export default class StudentClassListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
      <th>Name</th>
      
    </>);

  }
  renderRows() {
    const current = this.getPageContent();
    if (current instanceof Array) {
      return (<>
        {
          current.map((item, index) =>
            <StudentClassListItemView {...this.props} key={item.id} current={item}
              index={this.state.itemOffSet + index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.studentClassAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.studentClassAPI.updateById)}
              studentClassAPI={this.props.studentClassAPI} />)
        }
      </>);
    }
  }
}


class StudentClassListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
      
    </>);
  }
}
