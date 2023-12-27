<br />
        <FormGroup {...(this.props.structure ? this.props.structure.getCurrentProps() : undefined)}>
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <FormControl value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)}  type="@slot{{fieldType}}" @slot{{fieldOptions}} />
        </FormGroup>