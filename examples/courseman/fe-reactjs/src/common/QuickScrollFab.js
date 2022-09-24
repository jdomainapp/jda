import React from "react";
import FloatActionButton from "./FloatActionButton";
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faArrowDown, faArrowUp } from '@fortawesome/free-solid-svg-icons';

export default class QuickScrollFab extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            scrollPosition: window.pageYOffset
        };
    }
    componentDidMount() {
        window.addEventListener("scroll", () => this.setState({scrollPosition: window.pageYOffset}))
    }
    render() {
        return (<>
        <FloatActionButton variant="outline-success"
            onClick={() => {
                const topPos = this.state.scrollPosition < document.body.scrollHeight - window.innerHeight ?
                    document.body.scrollHeight : 0;
                window.scrollTo({
                    behavior: "smooth",
                    left: 0,
                    top: topPos
                })
            }}>
            <FontAwesomeIcon
                icon={this.state.scrollPosition <
                    document.body.scrollHeight - window.innerHeight ?
                    faArrowDown : faArrowUp} />
        </FloatActionButton>
        </>)
    }
}