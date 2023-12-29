<br/>
        <FormGroup id={"@slot{{fieldName}}"} {...(this.props.structure ? this.props.structure.getCurrentProps() : undefined)}>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <StarRatings
              rating={this.props.current.@slot{{fieldName}} ? this.props.current.@slot{{fieldName}} : 0}
              starRatedColor="blue"
              numberOfStars={5}
              name='@slot{{fieldName}}'
              changeRating={(newRating, name)=>
                {
                  this.props.handleStateChange("current.@slot{{fieldName}}", newRating, false)
                }
              }
          />
        </FormGroup>