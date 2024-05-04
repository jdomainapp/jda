import React from "react";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import BaseMainForm from "../base/BaseMainForm";
import CourseModuleListView from "./CourseModuleListView";
import CourseModuleForm from "./CourseModuleForm";
import AccordionFactory from "./patterns/accordion";
import SearchFactory from "./patterns/search";
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
      return ["code","name","description",]
    }

  getPossibleTypes() {
    return ["compulsory","elective",]
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">Form: CourseModule</h2>
      </>
    );
  }

  renderListView() {
    return <CourseModuleListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

   // patterns
  initPatterns() {
    super.initPatterns();

    this.consumers.push(AccordionFactory.createProviderConsumer({mainForm: this, name: this.props.structure ? "" : undefined, structure: this.props.structure}))

    this.consumers.push(SearchFactory.createProviderConsumer({mainForm: this}))
  }

  renderForm() {
    return <CourseModuleForm {...this.props} {...this.state}
    setReadySubmit={this.setReadySubmit}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })}
    mainForm={this} />;
  }

}
