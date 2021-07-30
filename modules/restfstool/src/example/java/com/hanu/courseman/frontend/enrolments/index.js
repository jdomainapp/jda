// import EnrolmentMainView from "./EnrolmentMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import EnrolmentListView from "./EnrolmentListView";
import EnrolmentForm from "./EnrolmentForm";
// {{ view.submodule.imports }}

const studentAPI = new BaseAPI("students", providers.axios);

const courseModuleAPI = new BaseAPI("course-modules", providers.axios);

const enrolmentAPI = new BaseAPI("enrolments", providers.axios);


export default function EnrolmentModule(props) {
  return <EnrolmentMainView
    mainAPI={enrolmentAPI}
    
studentAPI={studentAPI}
courseModuleAPI={courseModuleAPI}
enrolmentAPI={enrolmentAPI}
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
  getPossibleTypes() {
    return []
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">Manage Enrolment</h2>
      </>
    );
  }

  renderListView() {
    return <EnrolmentListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <EnrolmentForm {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
