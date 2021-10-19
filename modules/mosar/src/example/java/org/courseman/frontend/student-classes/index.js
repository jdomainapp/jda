// import StudentClassMainView from "./StudentClassMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import StudentClassListView from "./StudentClassListView";
import StudentClassForm from "./StudentClassForm";
// {{ view.submodule.imports }}

const studentAPI = new BaseAPI("students", providers.axios);

const studentClassAPI = new BaseAPI("student-classes", providers.axios);


export default function StudentClassModule(props) {
  return <StudentClassMainView
    mainAPI={studentClassAPI}
    
studentAPI={studentAPI}
studentClassAPI={studentClassAPI}
    {...props}
  />
}

// export default
class StudentClassMainView extends BaseMainForm {
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
        <h2 className="text-center">Manage Student Classes</h2>
      </>
    );
  }

  renderListView() {
    return <StudentClassListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <StudentClassForm {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
