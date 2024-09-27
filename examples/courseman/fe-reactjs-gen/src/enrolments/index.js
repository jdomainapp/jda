import React from "react";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import BaseMainForm from "../base/BaseMainForm";
import EnrolmentListView from "./EnrolmentListView";
import EnrolmentForm from "./EnrolmentForm";
import AccordionFactory from "./patterns/accordion";
import SearchFactory from "./patterns/search";
const enrolmentAPI = new BaseAPI("enrolments", providers.axios);

const studentAPI = new BaseAPI("students", providers.axios);
const courseModuleAPI = new BaseAPI("course-modules", providers.axios);


export default function EnrolmentModule(props) {
  return <EnrolmentMainView
    mainAPI={enrolmentAPI}
    enrolmentAPI={enrolmentAPI}
    studentAPI={studentAPI}
    courseModuleAPI={courseModuleAPI}
    {...props}
  />
}

// export default
class EnrolmentMainView extends BaseMainForm {
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
      return ["id",]
    }

  getPossibleTypes() {
    return []
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">Form: Enrolment</h2>
      </>
    );
  }

  renderListView() {
    return <EnrolmentListView {...this.props} {...this.state}
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
    return <EnrolmentForm {...this.props} {...this.state}
    setReadySubmit={this.setReadySubmit}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })}
    mainForm={this} />;
  }

}
