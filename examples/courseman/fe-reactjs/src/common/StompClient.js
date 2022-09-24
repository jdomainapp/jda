import { Stomp } from "@stomp/stompjs";

export class StompOverWSClient {
  constructor(socket) {
    this.socket = socket;
    this.client = Stomp.over(socket);
  }

  register(registrations) {
    const onSuccess = frame => {
      console.log('Connected: ' + frame);
      registrations.forEach(registration => {
        this.client.subscribe(registration.endpoint, registration.callback);
      });
    };
    const onFailure = () => {
      // reconnect
      this.client = Stomp.over(this.socket);
      registrations.forEach(registration => {
        this.client.subscribe(registration.endpoint, registration.callback);
      });
    };
    this.client.connect({}, onSuccess, onFailure);
  }
}