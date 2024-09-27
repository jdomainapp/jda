import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";

export default class CourseModuleListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
      <th>Code</th>
      <th>Name</th>
      <th>Description</th>
      <th>Semester</th>
      <th>Credits</th>
      <th>Rating</th>
      <th>Cost</th>
      
    </>);

  }
  renderRows() {
    const current = this.getPageContent();
    if (current instanceof Array) {
      return (<>
        {
          current.map((item, index) =>
            <CourseModuleListItemView {...this.props} key={item.id} current={item}
              index={this.state.itemOffSet + index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.courseModuleAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.courseModuleAPI.updateById)}
              courseModuleAPI={this.props.courseModuleAPI} />)
        }
      </>);
    }
  }
}


class CourseModuleListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.code)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.description)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.semester)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.credits)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.rating)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.cost)}</td>
      
    </>);
  }
}
