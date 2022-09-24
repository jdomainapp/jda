import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import AddressListItemView from "./AddressListItemView";

export default class AddressListView extends BaseListView {
  renderVisibleColumns() {
    return (<>
      <th>ID</th>
<th>City name</th>
{!(this.props.excludes?.includes('student')) && <th>Student</th>}
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <AddressListItemView {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.addressAPI.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.addressAPI.updateById)}
               studentAPI={this.props.studentAPI} addressAPI={this.props.addressAPI} />)
          }
        </>);
    }
  }
}

// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class AddressListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.id)}</td>
<td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.name)}</td>
{!(this.props.excludes?.includes('student')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.student)}</td>}
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
