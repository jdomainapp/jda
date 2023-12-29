{this.props.excludes && this.props.excludes.includes(\"@slot{{moduleJnames}}") ? "" : <>
    <@slot{{LinkedDomain}}Submodule
      mode='submodule'
      viewType={this.props.viewType}
      title="Form: @slot{{LinkedDomain}}"
      current={this.props.current.@slot{{fieldName}}}
      thisNamePlural='@slot{{fieldName}}' parentName='@slot{{moduleJnames}}' parent='@slot{{moduleJnames}}'
      parentId={this.props.currentId}
      parentAPI={this.props.mainAPI}
      partialApplyWithCallbacks={this.partialApplyWithCallbacks} /></>}