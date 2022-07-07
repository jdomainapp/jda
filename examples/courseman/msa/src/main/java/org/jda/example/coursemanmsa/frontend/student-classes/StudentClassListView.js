import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import StudentClassListItemView from "./StudentClassListItemView";

export default class StudentClassListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>Id</th>
<th>Name</th>
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <StudentClassListItemView {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.studentClassAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.studentClassAPI.updateById)}
               studentAPI={this.props.studentAPI} studentClassAPI={this.props.studentClassAPI} />)
          }
        </>);
    }
  }
}

// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class StudentClassListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
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
