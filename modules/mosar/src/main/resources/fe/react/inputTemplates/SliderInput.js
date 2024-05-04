<br />
        <FormGroup>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <div style={{display: "flex", alignItems: "center"}}>
            <FormControl {...this.onModelRegionMenuItem("accordion")} type="number" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)}  />
            <span style={{margin: "0 10px"}}>@slot{{min}}</span>
            <Slider
                min={@slot{{min}}}
                max={@slot{{max}}}
                value={this.props.current.cost ? this.props.current.@slot{{fieldName}} : @slot{{min}}}
                onChange={(value)=>{this.props.handleStateChange("current.@slot{{fieldName}}", value, false)}}
            />
            <span style={{margin: "0 10px"}}>@slot{{max}}</span>
          </div>
        </FormGroup>