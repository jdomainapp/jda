import Error from "./http_error";

export const errorInterceptor = (error) => {
    if (!error.response) {
        return Promise.reject(error);
    }

    switch (error.response.status) {
        case 400:
            error.message = Error.HttpError_400;
            break;
        case 401:
            error.message = Error.HttpError_401;
            break;
        case 404:
            error.message = Error.HttpError_404;
            break;
        case 500:
            error.message = Error.HttpError_500;
            break;
        default:
        // default case
    }

    return Promise.reject(error);
};

export const responseInterceptor = (response) => {
    switch (response.status) {
        case 200:
            break;
        default:
        // default case
    }

    return response;
};
