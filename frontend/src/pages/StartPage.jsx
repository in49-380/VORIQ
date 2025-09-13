import GoogleAuthButton from '../components/oauth/GoogleAuthButton';
// import usePageUrl from '../hooks/usePageUrl';
import {useTranslation} from 'react-i18next';

const RegistrationPage = () => {
  // usePageUrl('/login')
  const {t}=useTranslation()
  return (
    <div className="main-container  start-container">
      <div className='split'>
        <div className='welcome big-title'>
                {t('welcome')}
                <div className='subtitle'>
                  Please sign in with Google to continue.
                </div>
        </div>
        <div className='google'>
           <GoogleAuthButton/>
        </div>
      </div>
    

    </div>
  );
};

export default RegistrationPage;
