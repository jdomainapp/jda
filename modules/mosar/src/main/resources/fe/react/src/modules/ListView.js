import React from "react";
import BaseListView from "../base/BaseListView";
import BaseListItemView from "../base/BaseListItemView";

export default class @slot{{ModuleName}}ListView extends BaseListView {
  renderVisibleColumns() {
    return (<>@loop{tableHeader}[[
      <th>@slot{{Field__label}}</th>]]loop{tableHeader}@
      @loop{tableHeaderForLinkedModule}[[
      {!(this.props.excludes?.includes('@slot{{fieldName}}')) && <th>@slot{{Field__label}}</th>}]]loop{tableHeaderForLinkedModule}@
    </>);

  }
  renderRows() {
    const current = this.getPageContent();
    if (current instanceof Array) {
      return (<>
        {
          current.map((item, index) =>
            <@slot{{ModuleName}}ListItemView {...this.props} key={item.id} current={item}
              index={this.state.itemOffSet + index + 1} currentId={item.id}
              changeToDetailsView={this.props.changeToDetailsView}
              handleStateChange={this.props.handleStateChange}
              handleDelete={this.props.partialApplyWithCallbacks(this.props.@slot{{moduleName}}API.deleteById)}
              handleSubmit={this.props.partialApplyWithCallbacks(this.props.@slot{{moduleName}}API.updateById)}
              @slot{{moduleName}}API={this.props.@slot{{moduleName}}API}@loop{LinkedDomainApi}[[
              @slot{{linkedDomain}}API={this.props.@slot{{linkedDomain}}API} ]]loop{LinkedDomainApi}@ />)
        }
      </>);
    }
  }
}


class @slot{{ModuleName}}ListItemView extends BaseListItemView {
  renderVisibleColumns() {
    return (<>@loop{fieldRender}[[
      <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.@slot{{fieldName}})}</td>]]loop{fieldRender}@
      @loop{linkedFieldRender}[[
      {!(this.props.excludes?.includes('@slot{{fieldName}}')) && <td style={this.verticalAlignCell} onClick={this.changeCurrent}>{this.renderObject(this.props.current.@slot{{fieldName}})}</td>}]]loop{linkedFieldRender}@
    </>);
  }
}
