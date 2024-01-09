<br />
        <FormGroup>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <FormControl {...this.onModelRegionMenuItem("accordion")} type="@slot{{fieldType}}" value={this.renderObject("current.@slot{{fieldName}}")}
          onChange={(e) => {
            this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)
            @if{withValidate}((this.validate(this.renderObject('current.@slot{{fieldName}}'),"@slot{{fieldName}}")))if{withValidate}@
          }}
          @if{withValidate1}((
          isValid={this.props.inputState.@slot{{fieldName}} && this.props.inputState.@slot{{fieldName}}.validated !== undefined ? this.props.inputState.@slot{{fieldName}}.validated : false}
          isInvalid={this.props.inputState.@slot{{fieldName}} && this.props.inputState.@slot{{fieldName}}.validated !== undefined ? !this.props.inputState.@slot{{fieldName}}.validated : false}
          ))if{withValidate1}@
         @if{readonly}((readOnly))if{readonly}@  />
        </FormGroup>