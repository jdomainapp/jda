{this.props.excludes && this.props.excludes.includes("@slot{{fieldName}}") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>@slot{{Linked__domain}} ID</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.@slot{{fieldName}}Id")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}Id", e.target.value, true)} />
          </Col>
          <Col md={7} className='px-0'>
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} readOnly />
          </Col>
          <@slot{{LinkedDomain}}Submodule
            {...this.onModelRegionMenuItem("accordion")}
            ref={ref => { this.props.mainForm.addSubForm(ref) }}
            mainForm={this.props.mainForm}
            compact={true} mode='submodule'
            viewType={this.props.viewType}
            title="Form: @slot{{LinkedDomain}}"
            current={this.props.current.@slot{{fieldName}}}
            currentId={this.props.current.@slot{{fieldName}}?.@slot{{fieldName}}Id}
            parentName='@slot{{moduleName}}' parent={this.props.current}
            parentId={this.props.currentId}
            parentAPI={this.props.mainAPI}
            partialApplyWithCallbacks={this.partialApplyWithCallbacks}
            handleUnlink={() =>
              this.props.handleStateChange("current.@slot{{fieldName}}", null, false,
                this.props.handleStateChange("current.@slot{{fieldName}}Id", ""))} /></FormGroup></>
      }