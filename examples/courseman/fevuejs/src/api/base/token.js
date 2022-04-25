const getAuthToken = () => localStorage.getItem('token');

export const authInterceptor = (config) => {
    config.headers['Authorization'] = getAuthToken();
    return config;
}