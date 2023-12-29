{this.props.excludes && this.props.excludes.includes(\"@slot{{fieldName}}") ? "" : <>
    <FormGroup className='d-flex flex-wrap justify-content-between align-items-end'>
      @slot{{renderInputByID}}
      @slot{{renderCompactSubmoduleView}}
    </FormGroup></>}