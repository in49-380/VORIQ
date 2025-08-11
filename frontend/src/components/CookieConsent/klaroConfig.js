import configTranslations from './configTranslations.json'


export const klaroConfig={
    // noAutoLoad: true,

    // url:.../#klaro-testing
    testing: false,
    // delete if KlaroUi is don't used
    elementID:'klaro',
    storageMethod:'localStorage',
    storageName:'acceptedCookies',
    htmlTexts:true,
    noAutoLoad: true,

    // next 2 are only relevant if storageMethos is set to 'cookie'
    // cookieDomain:''
    // cookieExpiresAfterDays: 30,
    default:false,
    //   If 'mustConsent' is set to 'true', Klaro will directly display the consent
    // manager modal and not allow the user to close it before having actively
    // consented or declined the use of third-party services.
    // *******
    // !!!!
    // // Disabled in order to integrate a custom UI component
    mustConsent: true,
    acceptAll: true,
    hideDeclineAll: false,
    hideLearnMore: false,

    translations:configTranslations,
    

    services: [
        {
            name: 'matomo',
            default: true,
            translations: {
                zz: {
                    title: 'Matomo/Piwik',
                },
                en: {
                    description: 'Matomo is a simple, self-hosted analytics service.',
                },
                de: {
                    description: 'Matomo ist ein einfacher, selbstgehosteter Analytics-Service.',
                },
            },
            purposes: ['analytics'],
   /*
                you an either only provide a cookie name or regular expression (regex) or a list
                consisting of a name or regex, a path and a cookie domain. Providing a path and
                domain is necessary if you have services that set cookies for a path that is not
                "/", or a domain that is not the current domain. If you do not set these values
                properly, the cookie can't be deleted by Klaro, as there is no way to access the
                path or domain of a cookie in JS. Notice that it is not possible to delete
                cookies that were set on a third-party domain, or cookies that have the HTTPOnly
                attribute: https://developer.mozilla.org/en-US/docs/Web/API/Document/cookie#new-
                cookie_domain
                */

                /*
                This rule will match cookies that contain the string '_pk_' and that are set on
                the path '/' and the domain 'klaro.kiprotect.com'
                */
            cookies: [
                [/^_pk_.*$/, '/', 'klaro.kiprotect.com'],
                [/^_pk_.*$/, '/', 'localhost'],
                'piwik_ignore',
            ],

 /*
            You can define an optional callback function that will be called each time the
            consent state for the given service changes. The consent value will be passed as
            the first parameter to the function (true=consented). The `service` config will
            be passed as the second parameter.
            */
            callback: function(consent, service) {
                console.log(
                    'User consent for service ' + service.name + ': consent=' + consent
                );
            },

  /*
            If 'required' is set to 'true', Klaro will not allow this service to be disabled
            by the user. Use this for services that are always required for your website to
            function (e.g. shopping cart cookies).
            */
            required: false,

            /*
            If 'optOut' is set to 'true', Klaro will load this service even before the user
            has given explicit consent. We strongly advise against this.
            */
            optOut: false,

            /*
            If 'onlyOnce' is set to 'true', the service will only be executed once
            regardless how often the user toggles it on and off. This is relevant e.g. for
            tracking scripts that would generate new page view events every time Klaro
            disables and re-enables them due to a consent change by the user.
            */
            onlyOnce: true,        },
        {
            name: 'youtube',
            // “The service loads only upon contextual user consent (for example, clicking on the video).”
            contextualConsentOnly: true,
            purposes: ['video'],
        },
        // ******************************************
        // ******🔽 * TEST *****************************
        // ******************************************
    {
      name: 'funnyTracker',
      title: 'Funny Tracker',
      purposes: ['jokes'],
      cookies: ['funny_cookie'],
      default: false,
      required: false,
    },
    {
      name: 'betaTest',
      title: 'Beta Performance Tester',
      purposes: ['performance'],
      cookies: ['beta_cookie'],
      default: false,
      required: false,
    },
    {
            name: 'inlineTracker',
            title: 'Inline Tracker',
            purposes: ['analytics'],
            cookies: ['inline-tracker'],
            optOut: false,
        },
        {
            name: 'externalTracker',
            title: 'External Tracker',
            purposes: ['analytics', 'security'],
            cookies: ['external-tracker'],
        },
        {
            name: 'intercom',
            title: 'Intercom',
            default: true,
            purposes: ['livechat'],
        },
        {
            name: 'mouseflow',
            title: 'Mouseflow',
            purposes: ['analytics'],
        },
        {
            name: 'adsense',
            // if you omit the title here Klaro will try to look it up in the
            // translations
            //title: 'Google AdSense',
            purposes: ['advertising'],
        },
        {
            name: 'camera',
            title: 'Surveillance Camera',
            purposes: ['security'],
        },
        {
            name: 'googleFonts',
            title: 'Google Fonts',
            purposes: ['styling'],
        },
        {
            name: 'cloudflare',
            title: 'Cloudflare',
            purposes: ['security'],
            required: true,
        },
    ],
    
        
    // optional callback function that will be called each time the
    // consent state for any given service changes.  The consent value will be passed as
    // the first parameter to the function (true=consented). The `service` config will
    // be passed as the second parameter.

    callback: function(consent, service) {
        console.log(
            'User consent for service ' + service.name + ': consent=' + consent
        );
    },
    

}

