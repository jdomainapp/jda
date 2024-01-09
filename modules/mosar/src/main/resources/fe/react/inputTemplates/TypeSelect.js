<FormGroup>
          <Form.Label>Type</Form.Label>
          <Form.Control
              {...this.onModelRegionMenuItem("accordion")}
              as="select" value={this.renderObject('current.type')}
              onChange={(e)=>{
                this.props.handleTypeChange(e)
               }}
              readOnly={this.props.viewType !== "create"} custom
              isValid={this.props.inputState.@slot{{idField}} ? this.props.inputState.@slot{{idField}}.validated : false}
              isInvalid={this.props.inputState.@slot{{idField}} ? !this.props.inputState.@slot{{idField}}.validated : false}
          >
            <option value='' readOnly selected>&lt;Please choose one&gt;</option>@loop{moduleTypeOptions}[[
            <option value="@slot{{type}}">@slot{{type}}</option>]]loop{moduleTypeOptions}@
          </Form.Control>
          {this.props.inputState.@slot{{idField}} ?
              <Form.Control.Feedback type={this.props.inputState.@slot{{idField}}.validated ? "valid" : "invalid"}>{this.props.inputState.@slot{{idField}}.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>