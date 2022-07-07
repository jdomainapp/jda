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
        <FormControl as="select" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom @slot{{fieldOptions}} >
          <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
        </FormGroup>]]loop{formEnumInputs}@
        @loop{formLinkedInputs}[[
        @slot{{AssocWithSideOne}}
        @slot{{AssocWithSideMany}}
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
        <FormControl as="select" value={this.renderObject("current.@slot{{fieldName}}")} onChange={(e) => this.props.handleStateChange("current.@slot{{fieldName}}", e.target.value, false)} custom @slot{{fieldOptions}} >
          <option value='' disabled selected>&lt;Please choose one&gt;</option>@slot{{enumOptions}}
        </FormControl>
        </FormGroup></>}]]loop{formTypeEnumInputs}@
        @loop{formTypeLinkedInputs}[[
            @slot{{AssocWithSideOne}}
            @slot{{AssocWithSideMany}}
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
