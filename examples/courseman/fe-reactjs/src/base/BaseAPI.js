import constants from "../common/Constants";

export default class BaseAPI {
  /**
   * 
   * @param {string} objectNamePlural name of the main type of objects in charge of by this
   * @param {(url: string, method: string, data) => Promise} commFunction the communication function
   */
  constructor(objectNamePlural, commFunction) {
    this.objectNamePlural = objectNamePlural;
    this.callBackend = commFunction;
    this.create = this.create.bind(this);
    this.createInner = this.createInner.bind(this);
    this.deleteById = this.deleteById.bind(this);
    this.getAllInner = this.getAllInner.bind(this);
    this.getById = this.getById.bind(this);
    this.getByPage = this.getByPage.bind(this);
    this.getByPageAndType = this.getByPageAndType.bind(this);
    this.getFirstPage = this.getFirstPage.bind(this);
    this.updateById = this.updateById.bind(this);
  }

  create([data, onSuccess = undefined, onFailure = undefined]) {
    console.log(this);
    const url = `${constants.host}/${this.objectNamePlural}`;
    const method = "POST";
    this.callBackend(
      url, method, data
    ).then(onSuccess).catch(onFailure);
  }

  getById([id, onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}/${id}`;
    const method = "GET";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  getByPage([pageNumber, onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}?pageNumber=${pageNumber}`;
    const method = "GET";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  getByPageAndType([pageNumber, type, onSuccess = undefined, onFailure = undefined]) {
    if (!type) return;
    const filteringType = type === "0" ? undefined : type;
    const pageNo = pageNumber ? pageNumber : 1;
    const url = `${constants.host}/${this.objectNamePlural}?pageNumber=${pageNo}${filteringType ? `&type=${filteringType}` : ""}`;
    const method = "GET";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  getFirstPage([onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}`;
    const method = "GET";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  updateById([id, data, onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}/${id}`;
    const method = "PATCH";
    this.callBackend(
      url, method, data
    ).then(onSuccess).catch(onFailure);
  }

  deleteById([id, onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}/${id}`;
    const method = "DELETE";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  getAllInner([innerName, outerId, onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}/${outerId}/${innerName}`;
    const method = "GET";
    this.callBackend(
      url, method, undefined
    ).then(onSuccess).catch(onFailure);
  }

  createInner([data, innerName, outerId,
      onSuccess = undefined, onFailure = undefined]) {
    const url = `${constants.host}/${this.objectNamePlural}/${outerId}/${innerName}`;
    const method = "POST";
    this.callBackend(
      url, method, data
    ).then(onSuccess).catch(onFailure);
  }
}