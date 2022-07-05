import { Col, Form, FormControl, FormGroup } from "react-bootstrap";
import BaseForm from "../base/BaseForm";
@loop{importLinkedSubmodules}[[
import @slot{{LinkedDomain}}Submodule from "./@slot{{LinkedDomain}}Submodule";]]loop{importLinkedSubmodules}@

export default class @slot{{ModuleName}}Form extends BaseForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state
    };
    @if{haveSubType2}((
    this.renderBaseForm = this.renderBaseForm.bind(this)
    this.renderTypeSelect = this.renderTypeSelect.bind(this)
    this.renderInputsByType = this.renderInputsByType.bind(this)
    ))if{haveSubType2}@
  }

  renderTitle() {
    return (<>
      Form: @slot{{ModuleName}}
    </>);
  }

  render@slot{{formBase}}Form() {
    return (
      <>@loop{formInputs}[[
        <br />
        <FormGroup>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <FormControl value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)}  type="@slot{{fieldType}}" @slot{{fieldOptions}} />
        </FormGroup>]]loop{formInputs}@
        @loop{formEnumInputs}[[
        <br />
        <FormGroup>
        <Form.Label>@slot{{fieldLabel}}</Form.Label>
        <FormControl as="select" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom  >
          <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
        </FormGroup>]]loop{formEnumInputs}@
        @loop{formLinkedInputs}[[
        {this.props.excludes && this.props.excludes.includes("@slot{{fieldName}}") ? "" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
          <Form.Label>@slot{{LinkedDomain}} ID</Form.Label>
          <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}Id")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}Id", e.target.value, true)} />
        </Col>
          <Col md={7} className='px-0'>
            <Form.Label>@slot{{Linked__domain}}</Form.Label>
            <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} disabled />
          </Col><@slot{{LinkedDomain}}Submodule compact={true} mode='submodule'
            viewType={this.props.viewType}
            title="Form: @slot{{LinkedDomain}}"
            current={this.props.current.@slot{{fieldName}}}
            currentId={this.props.current.@slot{{fieldName}}?.@slot{{fieldName}}Id}
            parentName='@slot{{moduleName}}' parent={this.props.current}
            parentId={this.props.currentId}
            parentAPI={this.props.mainAPI}
            partialApplyWithCallbacks={this.partialApplyWithCallbacks}
            handleUnlink={() =>
              this.props.handleStateChange("current.@slot{{fieldName}}", null, false,
                this.props.handleStateChange("current.@slot{{fieldName}}Id", ""))} />
        </FormGroup></>}
        ]]loop{formLinkedInputs}@
      </>);
  }
  @if{haveSubType}((
  renderTypeSelect(){
    return (
    <FormGroup>
        <Form.Label>Type</Form.Label>
        <Form.Control as="select" value={this.renderObject('current.type')} onChange={this.props.handleTypeChange} disabled={this.props.viewType !== "create"} custom>
            <option value='' disabled selected>&lt;Please choose one&gt;</option>@loop{moduleTypeOptions}[[
            <option value="@slot{{type}}">@slot{{type}}</option>]]loop{moduleTypeOptions}@
        </Form.Control>
    </FormGroup>
    )
  }

  renderInputsByType(type){
    return(
        <>@loop{formTypeInputs}[[
        {type==='@slot{{type}}' && <><br />
        <FormGroup>
          <Form.Label>@slot{{fieldLabel}}</Form.Label>
          <FormControl value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)}  type="@slot{{fieldType}}" @slot{{fieldOptions}} />
        </FormGroup></>}]]loop{formTypeInputs}@
        @loop{formTypeEnumInputs}[[
        {type==='@slot{{type}}' && <><br />
        <FormGroup>
        <Form.Label>@slot{{fieldLabel}}</Form.Label>
        <FormControl as="select" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom  >
          <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
        </FormGroup></>}]]loop{formTypeEnumInputs}@
        @loop{formTypeLinkedInputs}[[
        {type==='@slot{{type}}' && this.props.excludes && this.props.excludes.includes("@slot{{fieldName}}") ? "" : <><FormGroup className='d-flex flex-wrap justify-content-between align-items-end'><Col md={2.5} className='px-0'>
          <Form.Label>@slot{{LinkedDomain}} ID</Form.Label>
          <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}Id")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}Id", e.target.value, true)} />
        </Col>
          <Col md={7} className='px-0'>
            <Form.Label>@slot{{Linked__domain}}</Form.Label>
            <FormControl type="text" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} disabled />
          </Col><@slot{{LinkedDomain}}Submodule compact={true} mode='submodule'
            viewType={this.props.viewType}
            title="Form: @slot{{LinkedDomain}}"
            current={this.props.current.@slot{{fieldName}}}
            currentId={this.props.current.@slot{{fieldName}}?.@slot{{fieldName}}Id}
            parentName='@slot{{moduleName}}' parent={this.props.current}
            parentId={this.props.currentId}
            parentAPI={this.props.mainAPI}
            partialApplyWithCallbacks={this.partialApplyWithCallbacks}
            handleUnlink={() =>
              this.props.handleStateChange("current.@slot{{fieldName}}", null, false,
                this.props.handleStateChange("current.@slot{{fieldName}}Id", ""))} />
        </FormGroup></>}
        ]]loop{formTypeLinkedInputs}@
        </>
    )
  }


  renderForm(){
    switch(this.props.current.type){@loop{typedFormRender}[[
        case '@slot{{type}}':
            return (
                <Form>
                    {this.renderTypeSelect()}
                    {this.renderBaseForm()}
                    {this.renderInputsByType('@slot{{type}}')}
                </Form>
            )]]loop{typedFormRender}@
        default:
            return <></>
    }
  }
  ))if{haveSubType}@
}
