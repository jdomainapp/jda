import axios from 'axios';
import development from "../../config/development.json";
import { errorInterceptor, responseInterceptor } from './interceptor'
import { authInterceptor} from './token.js'

const client = axios.create({
    baseURL: development.serverhost,
    timeout: 1000,
    headers: {'X-Custom-Header': 'foobar'}
});

client.interceptors.request.use(authInterceptor);
client.interceptors.response.use(responseInterceptor, errorInterceptor);

export default client;