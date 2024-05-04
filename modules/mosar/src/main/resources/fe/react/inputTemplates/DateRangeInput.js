<br />
        <FormGroup {...(this.props.structure ? this.props.structure.getCurrentProps() : undefined)}>
            <Form.Label>@slot{{fieldLabel}}</Form.Label>
            <DateRangePicker
                  onChange={item => this.handleDateRangeSelect(item, "@slot{{rangeID}}")}
                  showSelectionPreview={true}
                  moveRangeOnFirstSelection={false}
                  months={2}
                  ranges={[{
                    startDate: this.props.current && this.props.current.@slot{{startDate}} ? Date.parse(this.props.current.@slot{{startDate}}) : new Date(),
                    endDate: this.props.current && this.props.current.@slot{{endDate}} ? Date.parse(this.props.current.@slot{{endDate}}) : addDays(new Date(), 7),
                    key: 'selection'
                  }]}
                  direction="horizontal"
              />
        </FormGroup>