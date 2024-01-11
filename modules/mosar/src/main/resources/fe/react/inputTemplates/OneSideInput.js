 {this.props.excludes && this.props.excludes.includes("@slot{{fieldName}}") ? "" : <>
        <@slot{{LinkedDomain}}Submodule
          {...this.onModelRegionMenuItem("accordion")}
          ref={ref => { this.props.mainForm.addSubForm(ref) }}
          mainForm={this.props.mainForm}
          mode='submodule'
          viewType={this.props.viewType}
          title="Form: @slot{{Linked__domain}}"
          current={this.props.current.@slot{{fieldNames}}}
          thisNamePlural='@slot{{fieldNames}}' parentName='@slot{{moduleJname}}' parent='@slot{{moduleJname}}'
          parentId={this.props.currentId}
          parentAPI={this.props.mainAPI}
          partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
      </>}