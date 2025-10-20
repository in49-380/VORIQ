import GoogleAuthButton from '../components/oauth/GoogleAuthButton';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next';

const RegistrationPage = () => {
  // usePageUrl('/login')
  const {t}=useTranslation()
  return (
    <div className="main-container  start-container">
        
        <div className='split'>
          <div className='voriq-icon'>
               <img src="/images/logoBig.png" alt=""/>
               <p>{t('experience.title')}</p>  
          </div>
          <div className='google'>
                <GoogleAuthButton/>
                <div className='subtitle'>
                  {t('welcomeMessage')}
                </div>
          </div>
        
      </div>
    

    </div>
  );
};

export default RegistrationPage;
