import BaseAPI from "../base/BaseAPI";
import providers from "../common/BackendApiProviders";

import BaseMainForm from "../base/BaseMainForm";
import @slot{{ModuleName}}Form from "./@slot{{ModuleName}}Form";
import @slot{{ModuleName}}ListView from "./@slot{{ModuleName}}ListView";

const @slot{{moduleName}}API = new BaseAPI("@slot{{moduleJnames}}", providers.axios);
@loop{linkedModuleApi}[[
const @slot{{linkedDomain}}API = new BaseAPI("@slot{{linkedJdomains}}", providers.axios);]]loop{linkedModuleApi}@


export default function @slot{{ModuleName}}Module(props) {
  return <@slot{{ModuleName}}MainView
    mainAPI={@slot{{moduleName}}API}
    @slot{{moduleName}}API={@slot{{moduleName}}API}@loop{initLinkedModuleApi}[[
    @slot{{linkedDomain}}API={@slot{{linkedDomain}}API}]]loop{initLinkedModuleApi}@
    {...props}
  />
}

// export default
class @slot{{ModuleName}}MainView extends BaseMainForm {
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
    return [@slot{{moduleTypes}}]
  }

  renderTitle() {
    return (
      <>
        <h2 className="text-center">Form: @slot{{ModuleName}}</h2>
      </>
    );
  }

  renderListView() {
    return <@slot{{ModuleName}}ListView {...this.props} {...this.state}
    changeToDetailsView={() => this.handleStateChange("viewType", "details")}
    handleStateChange={this.handleStateChange}
    partialApplyWithCallbacks={this.partialApplyWithCallbacks} />
  }

  renderForm() {
    return <@slot{{ModuleName}}Form {...this.props} {...this.state}
    handleStateChange={this.handleStateChange.bind(this)}
    handleTypeChange={(e) => this.setState({ current: {...this.state.current, type: e.target.value} })} />;
  }

}
