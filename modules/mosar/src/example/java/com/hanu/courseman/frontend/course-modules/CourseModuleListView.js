import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import CourseModuleListItemView from "./CourseModuleListItemView";

export default class CourseModuleListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
<th>Code</th>
<th>Name</th>
<th>Semester</th>
<th>Credits</th>
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <CourseModuleListItemView {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
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

// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class CourseModuleListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.code)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.semester)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.credits)}</td>
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
