// import CourseModuleMainView from "./CourseModuleMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import CourseModuleListView from "./CourseModuleListView";
import CourseModuleForm from "./CourseModuleForm";

import AccordionFactory from "./patterns/accordion";
import SearchFactory from "./patterns/search";
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

  
  getSearchLabel() {
    return "name"
  }

  getSearchFields() {
    return ["code", "name", "description"]
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
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} 
    mainForm={this}/>
  }

  // patterns
  initPatterns() {
    super.initPatterns();

    this.consumers.push(AccordionFactory.createProviderConsumer(this))

    this.consumers.push(SearchFactory.createProviderConsumer(this))
  }

  renderForm() {
    return <CourseModuleForm {...this.props} {...this.state}
      setReadySubmit={this.setReadySubmit}
      handleStateChange={this.handleStateChange.bind(this)}
      handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })}
      mainForm={this} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
