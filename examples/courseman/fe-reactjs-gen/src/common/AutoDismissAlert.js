import React, { useEffect, useState } from "react";
import { Alert } from "react-bootstrap";

export default function AutoDismissAlert(props) {
  const [show, setShow] = useState(true);
  
  useEffect(() => {
    setTimeout(() => {
      setShow(false);
      props.onDisposed();
    }, props.timeout ? props.timeout : 2500);
  })

  return (
    <>
      <Alert className="mt-2" show={show} variant={props.variant}>
        <Alert.Heading>{props.heading}</Alert.Heading>
        <p>
          {props.text}
        </p>
        {props.actionButtons ? props.actionButtons : ""}
      </Alert>
    </>
  );
}