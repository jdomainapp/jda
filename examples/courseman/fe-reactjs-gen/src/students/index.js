import React from "react";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import BaseMainForm from "../base/BaseMainForm";
import StudentListView from "./StudentListView";
import StudentForm from "./StudentForm";
import AccordionFactory from "./patterns/accordion";
import SearchFactory from "./patterns/search";
const studentAPI = new BaseAPI("students", providers.axios);

const addressAPI = new BaseAPI("addresses", providers.axios);
const studentClassAPI = new BaseAPI("student-classes", providers.axios);
const enrolmentAPI = new BaseAPI("enrolments", providers.axios);


export default function StudentModule(props) {
  return <StudentMainView
    mainAPI={studentAPI}
    studentAPI={studentAPI}
    addressAPI={addressAPI}
    studentClassAPI={studentClassAPI}
    enrolmentAPI={enrolmentAPI}
    {...props}
  />
}

// export default
class StudentMainView extends BaseMainForm {
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
        <h2 className="text-center">Form: Student</h2>
      </>
    );
  }

  renderListView() {
    return <StudentListView {...this.props} {...this.state}
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
    return <StudentForm {...this.props} {...this.state}
    setReadySubmit={this.setReadySubmit}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })}
    mainForm={this} />;
  }

}
