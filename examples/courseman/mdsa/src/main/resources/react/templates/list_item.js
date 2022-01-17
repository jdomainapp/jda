// import React from "react";
// import BaseListItemView from "../base/BaseListItemView";
// import {{ view.name.form }} from "./{{ view.name.form }}";

// export default
class {{ view.name.listItem }} extends BaseListItemView {
  renderVisibleColumns() {
    return (<>
      {{ displayFields }}
      </>);
  }
  //
  // renderTitle() {
  //   return (<>
  //     {{ view.title }}
  //     </>);
  // }
  //
  // renderForm() {
  //   return (<{{ view.name.form }} {...this.props} />);
  // }
}