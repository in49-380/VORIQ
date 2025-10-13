import { get } from './api';

export const analysisAPI = {
  getVehicles({ signal }) {
    // return [{ name: 'Mercedes-Benz' }];
    return get('/vehicle', signal, true);
  },

  getMeta({ signal }) {
    return get('/meta', signal);
  },

  getAnalysis({ signal }) {
    // return [{ name: 'Analysis Data' }];
    setTimeout(() => {
      console.log('===ANALYSIS');
      get('/analysis?id=10', signal);
    }, 1000);
    // return get('analysis?id=10', signal);
  },
};
