<br/>
        <FormGroup>
          <Button 
          {...this.onModelRegionMenuItem("accordion")}
          style={{
            cursor: "pointer",
            padding: "18px",
            width: "100%",
            border: "none",
            textAlign: "left",
            outline: "none",
          }} onClick={(e)=>this.expand(e)}>@slot{{fieldLabel}}</Button>
          <div id="input" className={"content"}  style={{
            width: "100%",
            maxHeight: 0,
            overflow: "hidden",
            transition: "max-height 0.2s ease-out",
            backgroundColor: "#f1f1f1",
          }}>
            <textarea style={{
              width: "100%",
              border: "none",
              resize: "none",
              padding: "0",
              backgroundColor: "transparent",
              outline: "none",
              margin: "0"
            }} placeholder={"This is a test collapsible input"} rows={"5"}
            onChange={(e) => {
              this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)
              @if{withValidate}((
              this.validate(this.renderObject('current.@slot{{fieldName}}'),"@slot{{fieldName}}")
              ))if{withValidate}@
            }}
            isValid={this.props.inputState.@slot{{fieldName}} && this.props.inputState.@slot{{fieldName}}.validated !== undefined ? this.props.inputState.@slot{{fieldName}}.validated : false}
            isInvalid={this.props.inputState.@slot{{fieldName}} && this.props.inputState.@slot{{fieldName}}.validated !== undefined ? !this.props.inputState.@slot{{fieldName}}.validated : false}
            ></textarea>
          </div>

          {this.props.inputState.@slot{{fieldName}} && this.props.inputState.@slot{{fieldName}}.validated !== undefined ?
              <Form.Control.Feedback type={this.props.inputState.@slot{{fieldName}}.validated ? "valid" : "invalid"}>{this.props.inputState.@slot{{fieldName}}.message}</Form.Control.Feedback>
              : ""
          }
        </FormGroup>