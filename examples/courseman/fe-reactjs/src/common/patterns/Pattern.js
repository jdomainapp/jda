import React from "react"

export default class Pattern extends React.Component {
    constructor(props) {
        super(props)
        this.state = {}
        this.providers = Array()
    }

    componentDidMount() {
        console.log("hello")
    }

    registerProvider(provider) {
        provider.pattern = this
        this.providers.push(provider)
    }
}