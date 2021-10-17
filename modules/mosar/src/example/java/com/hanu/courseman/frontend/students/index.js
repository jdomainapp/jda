// import StudentMainView from "./StudentMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import StudentListView from "./StudentListView";
import StudentForm from "./StudentForm";
// {{ view.submodule.imports }}

const addressAPI = new BaseAPI("addresses", providers.axios);

const studentClassAPI = new BaseAPI("student-classes", providers.axios);

const enrolmentAPI = new BaseAPI("enrolments", providers.axios);

const studentAPI = new BaseAPI("students", providers.axios);


export default function StudentModule(props) {
  return <StudentMainView
    mainAPI={studentAPI}
    
addressAPI={addressAPI}
studentClassAPI={studentClassAPI}
enrolmentAPI={enrolmentAPI}
studentAPI={studentAPI}
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

  renderForm() {
    return <StudentForm {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
