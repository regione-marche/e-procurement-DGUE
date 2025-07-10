import { registerLocaleData } from '@angular/common';
import { Injectable } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { Language } from '../model/app-settings.models';
import { AppConfigService } from './app-config.service';
import { SdkLocaleService } from '@maggioli/sdk-commons';

/**
 * @description
 * @author Fabio Gomiero
 * @date 2019-12-10
 * @export
 * @class LocaleService
 */
@Injectable({ providedIn: 'root' })
export class LocaleService {

  /**
   * @description
   */
  private registerLocale: Map<string, boolean> = new Map<string, boolean>();

  /**
   * @description
   */
  private availableLangs: Array<Language> = [];

  /**
   * @description
   */
  private defaultLang: Language = null;


  /**
   *Creates an instance of LocaleService.
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @param {TranslateService} translateService
   * @param {AppConfigService} configService
   * @param {SdkLocaleService} sdkLocaleService
   * @memberof LocaleService
   */
  constructor(private translateService: TranslateService, private configService: AppConfigService, private sdkLocaleService: SdkLocaleService) {
    // Do nothing
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @memberof LocaleService
   */
  public loadDefaultLanguage(): void {
    this.availableLangs = this.configService.config.i18n.availableLangs || [{ label: 'English (EN)', code: 'en-EN', alternativeCode: 'en', currency: 'EUR', default: true, visualDateFormat: 'MM/dd/yyyy' }];
    this.defaultLang = this.availableLangs.find((lang: Language) => lang.default);
    this.translateService.addLangs(this.availableLangs.map((lang: Language) => lang.code));
    this.setDefaultLanguage(this.defaultLang);
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @returns {Promise<any>}
   * @memberof LocaleService
   */
  public loadUserPreferredLanguage(): Promise<any> {
    let lang: Language = JSON.parse(sessionStorage.getItem(this.configService.config.i18n.sessionKey));
    if (!this.isLangSupported(lang)) {
      console.warn(`Lang not exists or not supported: ${lang}`);
      lang = this.defaultLang;
    }

    this.sdkLocaleService.locale = lang.alternativeCode;
    this.sdkLocaleService.currency = lang.currency;

    return this.useLanguage(lang);
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @param {Language} lang
   * @returns {Promise<boolean>}
   * @memberof LocaleService
   */
  public useLanguage(lang: Language): Promise<boolean> {
    return this.localeInitializer(lang.code.substr(0, 2))
      .then(() => {
        return this.translateService.use(lang.code)
          .toPromise()
          .then(() => { // returns all translation in JSON
            sessionStorage.setItem(this.configService.config.i18n.sessionKey, JSON.stringify(lang));
            return true;
          })
          .catch(error => {
            console.warn(error);
            this.translateService.use(this.defaultLang.code);
            return false;
          });
      });
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @param {Language} langToVerify
   * @returns {boolean}
   * @memberof LocaleService
   */
  public isLangSupported(langToVerify: Language): boolean {
    return langToVerify && this.availableLangs.findIndex((lang: Language) => lang.code === langToVerify.code) !== -1;
  }

  /**
   * @description
   * @readonly
   * @type {Language}
   * @memberof LocaleService
   */
  public get currentLang(): Language {
    const usingLang = this.availableLangs.find((lang: Language) => lang.code === this.translateService.currentLang);
    return usingLang;
  }

  /**
   * @description
   * @readonly
   * @type {Array<Language>}
   * @memberof LocaleService
   */
  public get availableLanguages(): Array<Language> {
    return this.availableLangs;
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @protected
   * @param {Language} lang
   * @memberof LocaleService
   */
  protected setDefaultLanguage(lang: Language): void {
    this.translateService.setDefaultLang(lang.code);
  }

  /**
   * @description
   * @author Fabio Gomiero
   * @date 2019-12-10
   * @private
   * @param {string} localeId
   * @returns {Promise<any>}
   * @memberof LocaleService
   */
  private localeInitializer(localeId: string): Promise<any> {
    if (!this.registerLocale.has(localeId)) {
      return import(
        /* webpackExclude: /\.d\.ts$/ */
        /* webpackMode: "lazy-once" */
        /* webpackChunkName: "i18n-extra" */
        /* webpackInclude: /(en|it)\.mjs$/ */
        `@/../node_modules/@angular/common/locales/${localeId}`
      ).then(module => {
        registerLocaleData(module.default);
        return this.registerLocale.set(localeId, true);
      });
    } else {
      return Promise.resolve();
    }
  }
}
