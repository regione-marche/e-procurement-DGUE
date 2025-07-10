import { HttpClient, HttpClientModule } from '@angular/common/http';
import { APP_INITIALIZER, Injector, LOCALE_ID, NgModule, Type } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { BrowserModule, HammerModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ServiceWorkerModule } from '@angular/service-worker';
import { createTranslateLoader, SDK_APP_CONFIG, SdkProvider } from '@maggioli/sdk-commons';
import { SdkButtonModule, SdkDateModule, SdkDialogModule, SdkFormBuilderModule, SdkFormModule, SdkLoaderModule, SdkMenuModule, SdkMessagePanelModule, SdkModalModule, SdkNotificationModule, SdkPanelbarModule, SdkSearchModule, SdkSidebarModule, SdkTabsModule } from '@maggioli/sdk-controls';
import { TranslateLoader, TranslateModule } from '@ngx-translate/core';
import { DialogModule } from 'primeng/dialog';

import { environment } from '../environments/environment';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BaseComponentComponent } from './layout/base/base-component/base-component.component';
import { BreadcrumbComponent } from './layout/base/breadcrumb/breadcrumb.component';
import { FooterComponent } from './layout/base/footer/footer.component';
import { HeaderComponent } from './layout/base/header/header.component';
import { NavigationButtonComponent } from './layout/base/navigation-button/navigation-button.component';
import { AvvioComponent } from './layout/pages/avvio/avvio.component';
import { DGUEHomeComponent } from './layout/pages/dgue-home/dgue-home.component';
import { EsclusioneComponent } from './layout/pages/esclusione/esclusione.component';
import { FineComponent } from './layout/pages/fine/fine.component';
import { ProceduraComponent } from './layout/pages/procedura/procedura.component';
import { QuadroGeneraleComponent } from './layout/pages/quadro-generale/quadro-generale.component';
import { SelezioneComponent } from './layout/pages/selezione/selezione.component';
import { TabellatiComboProvider } from './providers/tabellati-combo.provider';
import { AppConfigService } from './services/app-config.service';
import { LocaleService } from './services/locale.service';
import { CookieService } from 'ngx-cookie-service';
import { DGUEHomePortaleComponent } from './layout/pages/dgue-home-portale/dgue-home-portale.component';
import { DGUEHomeAppaltiComponent } from './layout/pages/dgue-home-appalti/dgue-home-appalti.component';
import { QuadroGeneraleAppaltiComponent } from './layout/pages/quadro-generale-appalti/quadro-generale-appalti.component';
import { DatePipe } from '@angular/common';
import { CookiesComponent } from './layout/pages/footer/cookies.component';
import { CreditsComponent } from './layout/pages/footer/credits.component';
import { customElementsMap } from './app.elements';
import { forOwn } from 'lodash-es';
import { createCustomElement } from '@angular/elements';
import { CpvModalWidget } from './layout/pages/cpv-modal/cpv-modal.widget';
import { DisclaimerComponent } from './layout/pages/footer/disclaimer.component';
import { PrivacyComponent } from './layout/pages/footer/privacy.component';
import { AssistenzaComponent } from './layout/pages/footer/assistenza.component';
import { QuadroGeneraleCaricamentoComponent } from './layout/pages/quadro-generale-caricamento/quadro-generale-caricamento.component';


export const appInitializerFn = (appConfig: AppConfigService, localeService: LocaleService) => {
  return () => {
    return appConfig.loadAppConfig().then(() => {
      localeService.loadDefaultLanguage();
      return localeService.loadUserPreferredLanguage();
    });
  };
};

export const providersList: Array<Type<SdkProvider>> = [
  TabellatiComboProvider
];

@NgModule({
  declarations: [
    AppComponent,
    FooterComponent,
    HeaderComponent,
    DGUEHomeComponent,
    BreadcrumbComponent,
    NavigationButtonComponent,
    AvvioComponent,
    ProceduraComponent,
    EsclusioneComponent,
    SelezioneComponent,
    FineComponent,
    BaseComponentComponent,
    QuadroGeneraleComponent,
    DGUEHomePortaleComponent,
    DGUEHomeAppaltiComponent,
    QuadroGeneraleAppaltiComponent,
    CookiesComponent,
    CreditsComponent,
    CpvModalWidget,
    DisclaimerComponent,
    PrivacyComponent,
    AssistenzaComponent,
    QuadroGeneraleCaricamentoComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    HttpClientModule,
    SdkDateModule,
    SdkFormBuilderModule,
    SdkMessagePanelModule,

  
   
    SdkButtonModule,   
   
    SdkDialogModule,
    SdkModalModule,
    SdkSidebarModule,
    SdkLoaderModule,
    SdkFormModule,
    SdkSearchModule,
    SdkMenuModule,

    DialogModule,
    TranslateModule.forRoot({
      loader: {
        provide: TranslateLoader,
        useFactory: (createTranslateLoader),
        deps: [HttpClient]
      }
    }),
    ServiceWorkerModule.register('ngsw-worker.js', { enabled: environment.production }),
    HammerModule    
  ],
  providers: [
    providersList,
    DatePipe,
    {
      provide: APP_INITIALIZER,
      useFactory: appInitializerFn,
      multi: true,
      deps: [
        AppConfigService, LocaleService
      ]
    },
    {
      provide: LOCALE_ID, useValue: environment.language
    },
    {
      provide: SDK_APP_CONFIG,
      useValue: {
        environment
      }
    },
    CookieService
  ],
  bootstrap: [AppComponent],

})
export class AppModule {
  constructor(private inj: Injector) { this.defineElements() }

  protected defineElements(): void {
    

    forOwn(customElementsMap(), this.defineCustomElement)
  }

  protected defineCustomElement = (type: Type<any>, key: string): void => {
    customElements.define(key, createCustomElement(type, { injector: this.inj }))
  }
}
