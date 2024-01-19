import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";

export default class EnrolmentListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
      <th>Internal mark</th>
      <th>Exam mark</th>
      <th>Final grade</th>
      <th>Finalmark</th>
      <th>Date range</th>
      <th>Date range</th>
      
      {!(this.props.excludes?.includes('student')) && <th>Student</th>}
      {!(this.props.excludes?.includes('courseModule')) && <th>Course module</th>}
    </>);

  }
  renderRows() {
    const current = this.getPageContent();
    if (current instanceof Array) {
      return (<>
        {
          current.map((item, index) =>
            <EnrolmentListItemView {...this.props} key={item.id} current={item}
              index={this.state.itemOffSet + index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.enrolmentAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.enrolmentAPI.updateById)}
              enrolmentAPI={this.props.enrolmentAPI}
              studentAPI={this.props.studentAPI} 
              courseModuleAPI={this.props.courseModuleAPI}  />)
        }
      </>);
    }
  }
}


class EnrolmentListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.internalMark)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.examMark)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.finalGrade)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.finalMark)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.startDate)}</td>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.endDate)}</td>
      
      {!(this.props.excludes?.includes('student')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.student)}</td>}
      {!(this.props.excludes?.includes('courseModule')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.courseModule)}</td>}
    </>);
  }
}
