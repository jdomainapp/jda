<br />
        <FormGroup>
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <FormControl {...(this.props.structure ? this.props.structure.getCurrentProps() : undefined)}
                         as="select" value={this.renderObject("current.@slot{{fieldName}}")}
                         onChange={(e)=>{
                                        this.props.handleTypeChange(e)
                                       }}
                          disabled={this.props.viewType !== "create"} custom
                          isValid={this.props.inputState.id ? this.props.inputState.id.validated : false}
                          isInvalid={this.props.inputState.id ? !this.props.inputState.id.validated : false}
                        custom >
                <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
            </FormControl>
        </FormGroup>