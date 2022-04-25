export default class AddressForm {
    constructor() {
        this.hidId = true,
        this.hidName = true,
        this.hidSubModule = true
    }

    setHidId(hidId) {
        this.hidId = hidId;
    }

    setHidName(hidName) {
        this.hidName = hidName;
    }

    setHidSubModule(hidSubModule) {
        this.hidSubModule = hidSubModule;
    }
}