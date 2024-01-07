
import React from "react";
import { Typeahead } from 'react-bootstrap-typeahead';
import Pattern from "../Pattern";

export default class AutoCompleteSearch extends Pattern {
    constructor(props) {
        super(props)
        this.searchRef = React.createRef(null)

        this.searchLabel = props.searchLabel
        this.searchFields = props.searchFields
        this.content = props.content

        this.handleOnSearch = this.handleOnSearch.bind(this);
        this.handleOnSelect = this.handleOnSelect.bind(this);
    }

    handleOnSearch(e) {
        if(e.code === "Enter") {
            this.providers.forEach(provider => {
                provider.action("search", {result: this.searchRef.current.items})
            })
        }
    }
    
    handleOnSelect(item) {
        this.providers.forEach(provider => {
            provider.action("select", {item: item})
        })
    }

    formatResult(option) {
        return "Specify display option!"
    }


    render() {
        return (
            <Typeahead
                ref={this.searchRef}
                id={"search"}
                labelKey={this.props.formatResult ? this.props.formatResult: this.formatResult}
                filterBy={this.searchFields ? this.searchFields : []}
                onChange={this.handleOnSelect}
                onKeyDown={this.handleOnSearch}
                options={this.content ? this.content : []}
                placeholder="Search"    
            />
        )
    }
}