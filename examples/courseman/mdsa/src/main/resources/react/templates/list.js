import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.listItem }} from "./{{ view.name.listItem }}";

export default class {{ view.name.list }} extends BaseListView {
  renderVisibleColumns() {
    return (<>
      {{ view.list.headings }}
      </>);

  }
  renderRows() {
    const current = this.props.current.content;
      if (current instanceof Array) {
        return (<>
          {
            current.map((item, index) =>
            <{{ view.name.listItem }} {...this.props} key={item.id} current={item}
              index={index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.{{ view.api.main }}.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.{{ view.api.main }}.updateById)}
              {{ view.api.bindings }} />)
          }
        </>);
    }
  }
}
