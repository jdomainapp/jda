
import React from "react";
import { Typeahead } from 'react-bootstrap-typeahead';

export default class AutoCompleteSearch extends React.Component {
    constructor(props) {
        super(props)
        this.searchRef = React.createRef(null)

        this.handleOnSearch = this.handleOnSearch.bind(this);
        this.handleOnSelect = this.handleOnSelect.bind(this);
    }

    formatResult(item) {
        return (
            <>
            <span style={{ display: 'block', textAlign: 'left' }}>id: {item.id}</span>
            <span style={{ display: 'block', textAlign: 'left' }}>code: {item.code}</span>
            <span style={{ display: 'block', textAlign: 'left' }}>name: {item.name}</span>
            </>
        )
    }

    handleOnSearch(e) {
        console.log(this.props)
        if(e.code === "Enter") {
            this.props.handleStateChange("displayingContent", this.searchRef.current.items, false)
        }
    }
    
    handleOnSelect(item) {
        this.props.handleStateChange("viewType", "details")
        this.props.handleStateChange(
            "currentId", item[0].id, true);
    }


    render() {
        return (
            <Typeahead
                ref={this.searchRef}
                id={this.props.id}
                labelKey={this.props.getSearchLabel()}
                filterBy={this.props.getSearchFields()}
                onChange={this.handleOnSelect}
                options={this.props.source}
                placeholder="Search"
                onKeyDown={this.handleOnSearch}
            />
        )
    }
}