<br />
<FormGroup id="date-range">
    <Form.Label>Date Range</Form.Label>
    <DateRangePicker
        onChange={item => this.handleSelect(item)}
        showSelectionPreview={true}
        moveRangeOnFirstSelection={false}
        months={2}
        ranges={this.state.@slot{{fieldName}}}
        direction="horizontal"
    />
</FormGroup>