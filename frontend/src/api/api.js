import axios from 'axios';
// const backURL='http://$VITE_SWAGGER_DOMAIN:$VITE_SWAGGER_PORT/api/v1'

// const domain = import.meta.env.VITE_SWAGGER_DOMAIN || 'http://voriq.info';
const domain = import.meta.env.VITE_SWAGGER_DOMAIN
  ? `http://${import.meta.env.VITE_SWAGGER_DOMAIN}`
  : 'http://voriq.info';


  console.log('env:', domain)
const port = import.meta.env.VITE_SWAGGER_PORT || '8084';

const backURL = port ? `${domain}:${port}/api/v1` : `${domain}/api/v1`;

const authHeader = (json = false) => {
  const contentType = json ? 'application/json' : 'application/x-www-form-urlencoded';
  const headers = { 'content-type': contentType };

  // const token = JSON.parse(localStorage.getItem('jwt_token'));
  const token = '3f2f9b4a-26a7-4b0c-a1b2-7e7e05f3c4d1';

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  return headers;
};

const addInterceptors = (instance, json) => {
  instance.interceptors.request.use((config) => {
    if (!config.headers['Authorization']) {
      config.headers = authHeader(json);
    }
    return config;
  }, Promise.reject);
};

const axiosInstance = axios.create({
  async: true,
  crossDomain: true,
  baseURL: backURL,
  headers: authHeader(true),
});

addInterceptors(axiosInstance, true);

const post = async (url, data, callback, errorCallback) => {
  const response = await axiosInstance.post(url, JSON.stringify(data)).then(callback).catch(errorCallback);
  return response.data;
};

const put = async (url, data, callback, errorCallback) => {
  const response = await axiosInstance.put(url, JSON.stringify(data)).then(callback).catch(errorCallback);
  return response.data;
};

const get = async (url, signal, save, callback, errorCallback) => {
  // Config is object with signal
  const config = { signal };
  const response = await axiosInstance.get(url, config).then(callback).catch(errorCallback);
  return { response: response.data, success: 'success', save: !!save };
};

export {
  post,
  put,
  get,
};
