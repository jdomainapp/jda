import React from "react"

export default class Pattern extends React.Component {
    constructor(props) {
        super(props)
        this.providers = Array()
    }

    registerProvider(provider) {
        provider.pattern = this
        this.providers.push(provider)
    }
}