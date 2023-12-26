<br/>
        <FormGroup id={"@slot{{fieldName}}"}>
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