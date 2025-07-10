import { Component, Injectable, OnInit, ElementRef, ViewChild, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute, ParamMap, Params } from '@angular/router';
import { AppCostants } from '../../../utils/dgue-constants';
import { CookieService } from 'ngx-cookie-service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { StoreModel } from '../../../model/store-model';
import { isEmpty, isObject, each } from 'lodash-es';
import { SdkMessagePanelService, SdkMessagePanelTranslate } from '@maggioli/sdk-controls';
import { LocaleService } from '../../../services/locale.service';
import { Language } from '../../../model/app-settings.models';
import { TranslateService } from '@ngx-translate/core';
import { PrimeNGConfig } from 'primeng/api';


@Component({
  selector: 'app-dgue-home',
  templateUrl: './dgue-home.component.html',
  styleUrls: ['./dgue-home.component.scss']
})
@Injectable()
export class DGUEHomeComponent implements OnInit,OnDestroy {
  
  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('langIt') _langIt: ElementRef;
  @ViewChild('langEn') _langEn: ElementRef;
  @ViewChild('warningspanel') _warningsPanel: ElementRef;
  private cookieData:any;
  private fromPortale: boolean=false;
  private browserName: string;

  storeObject: StoreModel = {
    procedura: {},
    esclusione: {},
    selezione: {},
    fine: {}
  }
 
  private importXml: string;
  translatePrimeNg:any;
  constructor(private router:Router,
              private cookieService:CookieService,
              private restService: RestService,
              public store: StoreService,
              private activatedRoute: ActivatedRoute,
              public sdkMessagePanelService: SdkMessagePanelService,
              private localeService: LocaleService,
              private primeNGConfig: PrimeNGConfig,
              private  translate: TranslateService) { 

          this.store.clearStore();         
          this.fromPortale=false;
  }

  ngOnDestroy(): void {
    
  }

  
  
  ngOnInit() {
    
    this.store.addElement(AppCostants.STORE_LANG,'it');
    let language={"label": "Italiano",
    "code": "it-IT",
    "alternativeCode": "it",
    "currency": "EUR",                
    "visualDateFormat": "dd/MM/yyyy"}
    this.localeService.useLanguage(language);
    this.primeNGConfig.setTranslation(this.translate.store.translations[language.code].primeng);

   
    let cookieTokenString: string = this.cookieService.get(AppCostants.SSO_COOKIE_KEY);
    this.cookieService.deleteAll(AppCostants.SSO_COOKIE_KEY);
    //console.log("cookieTokenString: "+cookieTokenString);
    if (cookieTokenString === null || cookieTokenString === '') {
      //TODO se non trovo il cookie string effettivamente dal cookie lo vado a prendere dai parametri
      this.activatedRoute.queryParams
                .subscribe((el : Params)=> {
                  //console.log(el); 
                  cookieTokenString = el.t;
                  //console.log(cookieTokenString); 
                }
              );
    }    
  }


  ngAfterViewInit(){
    this.browserName = this.detectBrowserName();   
    if(this.browserName != 'firefox' && this.browserName != 'chrome' && this.browserName != 'edge'){
    
      let messages = ["BROWSER-ALERT"];
      
      let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
        each(messages, (mess: string) => {
            let singleMessage : SdkMessagePanelTranslate = {
            message : mess
          };
          messagesForPanel.push( singleMessage);
        });
      this.sdkMessagePanelService.clear(this.warningPanel);
      this.sdkMessagePanelService.showWarning(this.warningPanel,messagesForPanel);
    }   
  }

  parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  };

  private detectBrowserName() { 
    const agent = window.navigator.userAgent.toLowerCase()
    switch (true) {
      case agent.indexOf('edge') > -1:
        return 'edge';
      case agent.indexOf('opr') > -1 && !!(<any>window).opr:
        return 'opera';
      case agent.indexOf('chrome') > -1 && !!(<any>window).chrome:
        return 'chrome';
      case agent.indexOf('trident') > -1:
        return 'ie';
      case agent.indexOf('firefox') > -1:
        return 'firefox';
      case agent.indexOf('safari') > -1:
        return 'safari';
      default:
        return 'other';
    }
  }

  private get warningPanel(): HTMLElement {
    return isObject(this._warningsPanel) ? this._warningsPanel.nativeElement : undefined;
  }

  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

  start(){
    if(this.fromPortale){
      
    }else{
      this.router.navigate([AppCostants.PAGE_AVVIO]);
    }
  }

  open(){        
    let params = {
      isCaricamento: true
    }
    this.router.navigate([AppCostants.PAGE_AVVIO] ,{ queryParams : params });    
  }

  help(){
    this.router.navigate([AppCostants.PAGE_ASSISTENZA]);
  }
  



}
