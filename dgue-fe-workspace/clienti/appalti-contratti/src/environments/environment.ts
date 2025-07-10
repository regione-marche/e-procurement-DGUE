// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  appSettingsUrl: '/configurations/app-config.json',
  LOGGER_LEVEL: 'DEBUG',
  restURL: 'http://localhost:8080/rest/dgue-ms/v1/',
  //restURL: 'https://api-dev.maggiolicloud.it/rest/dgue-ms/v1/',  
  language: 'it-IT',
  //language: 'en-GB'
  env:'dev',
  serviceProvider:{
    vatNumber: '02066400405',
    website: 'https://www.maggioli.com',
    email: 'https://dgue.maggiolicloud.it/',
    name: 'Maggioli Spa',
    country: 'IT'
  }
};



/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/plugins/zone-error';  // Included with Angular CLI.
