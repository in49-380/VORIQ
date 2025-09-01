import axios from 'axios';

const backURL = import.meta.env.VITE_BACKEND_URL;

const authHeader = (json = false) => {
  const contentType = json ? 'application/json' : 'application/x-www-form-urlencoded';
  const headers = { 'content-type': contentType };

  // const token = JSON.parse(localStorage.getItem('jwt_token'));
  const token = '6b5f3d92-4b8c-4f2a-9f88-1a6b2c8a1b1d';

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
