import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import EnrolmentListItemView from "./EnrolmentListItemView";

export default class EnrolmentListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
{!(this.props.excludes?.includes('student')) && <th>Student</th>}
{!(this.props.excludes?.includes('courseModule')) && <th>Course module</th>}
<th>Internal Mark</th>
<th>Exam Mark</th>
<th>Final Grade</th>
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <EnrolmentListItemView {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.enrolmentAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.enrolmentAPI.updateById)}
               studentAPI={this.props.studentAPI} courseModuleAPI={this.props.courseModuleAPI} enrolmentAPI={this.props.enrolmentAPI} />)
          }
        </>);
    }
  }
}

// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class EnrolmentListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
{!(this.props.excludes?.includes('student')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.student)}</td>}
{!(this.props.excludes?.includes('courseModule')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.courseModule)}</td>}
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.internalMark)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.examMark)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.finalGrade)}</td>
      </>);
  }
  //
  // renderTitle() {
  //   return (<>
  //     {{ view.title }}
  //     </>);
  // }
  //
  // renderForm() {
  //   return (<{{ view.name.form }} {...this.props} />);
  // }
}
