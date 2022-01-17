import { useState } from "react";
import { Toast } from "react-bootstrap";

export function CustomToast({ style, header, children, timeout, onClick }) {
  const [show, changeShow] = useState(true);

  return show ? (
      <Toast className={`bg-${style}`} show={show} delay={timeout}
            animation={true}
            onClose={() => changeShow(false)} autohide={true}
            style={{
              zIndex: "999"
            }}>
        <Toast.Header style={{
          cursor: "pointer"
        }} className={`d-flex justify-content-between`}>
          <strong>{header}</strong>
        </Toast.Header>
        <Toast.Body className={`btn-${style}`} style={{
          cursor: "pointer"
        }} onClick={onClick}>{children}</Toast.Body>
      </Toast>) : "";
}

export function ToastWrapper({ children }) {
  return (
    <div
      aria-live="polite"
      aria-atomic="true"
      style={{
        position: 'absolute',
        top: 0,
        left: 0,
        width: "100vw",
        margin: 'auto',
        zIndex: 9999
      }}>
      <div
        style={{
          position: 'fixed',
          top: "5%",
          right: "5%",
        }}
      >
        {children}
      </div>
    </div>);
}