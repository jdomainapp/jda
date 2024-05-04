import React from "react";
import { Button } from "react-bootstrap";

export default class FloatActionButton extends React.Component {
    render() {
        return <Button style={{
            position: "fixed",
            bottom: 20,
            right: 20
        }} {...this.props}>{this.props.children}</Button>
    }
}