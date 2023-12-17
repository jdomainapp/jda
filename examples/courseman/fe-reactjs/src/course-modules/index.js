// import CourseModuleMainView from "./CourseModuleMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import CourseModuleListView from "./CourseModuleListView";
import CourseModuleForm from "./CourseModuleForm";
import AccordionSearchableMenu from "../common/AccordionSearchableMenu";
// {{ view.submodule.imports }}

const courseModuleAPI = new BaseAPI("course-modules", providers.axios);


export default function CourseModuleModule(props) {
  return <CourseModuleMainView
    mainAPI={courseModuleAPI}
    
courseModuleAPI={courseModuleAPI}
    {...props}
  />
}

// export default
class CourseModuleMainView extends BaseMainForm {
  constructor(props) {
    super(props);
    this.state = {
      ...this.state,
      current: {
        ...this.state.current,
        ...this.props.current,
        type: this.getPossibleTypes.bind(this)() ? this.getPossibleTypes()[0] : undefined
      },
      currentId: this.props.currentId
    }
  }

  getStructure() {
    return {
      fields: [
        {
          "endpoint": "#type", "name": "Type",
        },
        {
          "endpoint": "#id", "name": "ID"
        },
        {
          "endpoint": "#code", "name": "Code"
        },
        {
          "endpoint": "#name", "name": "Name"
        },
        {
          "endpoint": "#semester", "name": "Semester"
        },
        {
          "endpoint": "#cost", "name": "Cost"
        },
        {
          "endpoint": "#rating", "name": "Rating"
        },
        {
          "endpoint": "#description", "name": "Description"
        },
        {
          "endpoint": "#credit", "name": "Credit"
        }
      ],
      "subItem": []
    }
  }

  renderMenu() {
    return (
        <>
          <h2>Fields</h2>
          <AccordionSearchableMenu modules={this.getStructure().fields}/>
          <h2>Sub-modules</h2>
          <AccordionSearchableMenu modules={this.getStructure().subItem}/>
        </>
    )
  }

  getPossibleTypes() {
    return ['compulsory','elective']
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">Manage Course Modules</h2>
      </>
    );
  }

  renderListView() {
    return <CourseModuleListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <CourseModuleForm {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
