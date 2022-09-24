import axios from "axios";

function fetchProvider(url, method, data) {
  const fetchOptions = {
    method: method
  };
  if (data) fetchOptions["data"] = data;
  return fetch(url, fetchOptions).then(response => response.json());
}

function axiosProvider(url, method, data) {
  const axiosOptions = {
    method: method,
    url: url
  };
  if (data) axiosOptions["data"] = data;
  return axios(axiosOptions).then(response => response.data);
}

const providers = {
  "fetch": fetchProvider,
  "axios": axiosProvider
};

export default providers;