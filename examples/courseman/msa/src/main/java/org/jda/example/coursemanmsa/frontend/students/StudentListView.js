import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import StudentListItemView from "./StudentListItemView";

export default class StudentListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Student ID</th>
<th>Full Name</th>
<th>Gender</th>
<th>Date of birth</th>
<th>Email</th>
{!(this.props.excludes?.includes('address')) && <th>Address</th>}
{!(this.props.excludes?.includes('studentClass')) && <th>Student class</th>}
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <StudentListItemView {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.studentAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.studentAPI.updateById)}
               addressAPI={this.props.addressAPI} studentClassAPI={this.props.studentClassAPI} enrolmentAPI={this.props.enrolmentAPI} studentAPI={this.props.studentAPI} />)
          }
        </>);
    }
  }
}

// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class StudentListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.gender)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.dob)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.email)}</td>
{!(this.props.excludes?.includes('address')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.address)}</td>}
{!(this.props.excludes?.includes('studentClass')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.studentClass)}</td>}
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
