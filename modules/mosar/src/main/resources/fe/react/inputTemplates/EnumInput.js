<br />
      <FormGroup>
        <Form.Label>@slot{{fieldLabel}}</Form.Label>
        <FormControl {...this.onModelRegionMenuItem("accordion")} as="select" value={this.renderObject("current.@slot{{fieldName}}")}
          onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom  >
          <option value='' readOnly selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
      </FormGroup>