
import React from 'react';
import usePageUrl from '../hooks/usePageUrl';

const CarPage = () => {
  usePageUrl('cars')
  return (
    
    <div className="min-h-screen flex items-center justify-center bg-blue-100">
      <h1 className="text-4xl font-semibold text-blue-700">
        Car Page
      </h1>
    </div>
  );
};

export default CarPage;
