<br />
        <FormGroup >
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <FormControl {...(this.props.structure ? this.props.structure.getCurrentProps() : undefined)}
                      value={this.renderObject("current.@slot{{fieldName}}")}
                      onChange={(e)=>{
                                    this.props.handleTypeChange(e)
                                   }}
                      disabled={this.props.viewType !== "create"} custom
                      isValid={this.props.inputState.id ? this.props.inputState.id.validated : false}
                      isInvalid={this.props.inputState.id ? !this.props.inputState.id.validated : false}
            type="@slot{{fieldType}}" />
        </FormGroup>