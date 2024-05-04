<br />
      {this.props.excludes && this.props.excludes.includes("@slot{{fieldName}}") ? "" : <>
        <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
          <Col md={2.5} className='px-0'>
            <Form.Label>@slot{{Linked__domain}} Id</Form.Label>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="@slot{{idType}}" value={this.renderObject("current.@slot{{fieldName}}Id")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}Id", e.target.value, true)} />
          </Col>
          <Col md={9} className='px-0'>
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} readOnly />
          </Col></FormGroup></>}