// import AddressMainView from "./AddressMainView";
import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import React from "react";
import BaseMainForm from "../base/BaseMainForm";
import AddressListView from "./AddressListView";
import AddressForm from "./AddressForm";
// {{ view.submodule.imports }}

const studentAPI = new BaseAPI("students", providers.axios);

const addressAPI = new BaseAPI("addresses", providers.axios);


export default function AddressModule(props) {
  return <AddressMainView
    mainAPI={addressAPI}
    
studentAPI={studentAPI}
addressAPI={addressAPI}
    {...props}
  />
}

// export default
class AddressMainView extends BaseMainForm {
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
        <h2 className="text-center">Form: Address</h2>
      </>
    );
  }

  renderListView() {
    return <AddressListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <AddressForm {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

  // renderSubmodules() {
  //   return (<>
  //     {{ view.submodules }}
  //     </>);
  // }
}
