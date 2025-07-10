import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { TranslateService } from '@ngx-translate/core';

import { isObject } from 'lodash-es';
import { PrimeNGConfig } from 'primeng/api';
import { Language } from 'projects/app-dgue/src/app/model/app-settings.models';
import { LocaleService } from 'projects/app-dgue/src/app/services/locale.service';
import { StoreService } from 'projects/app-dgue/src/app/services/storeService';
import { AppCostants } from 'projects/app-dgue/src/app/utils/dgue-constants';
import { environment } from 'projects/app-dgue/src/environments/environment';





@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  
  @ViewChild('langIt') _langIt: ElementRef;
  @ViewChild('langEn') _langEn: ElementRef;

  public env = '';

  constructor( public store: StoreService,  
              private localeService: LocaleService,
              private primeNGConfig: PrimeNGConfig,
              private  translate: TranslateService) { }
 
  ngOnInit() {
    if (environment['env'] != null && environment['env'] === 'dev') {
      this.env = 'dev';
    } else if (environment['env'] != null && environment['env'] === 'prod') {
      this.env = 'prod';
    }
  }

  private get langIt(): HTMLElement {
    return isObject(this._langIt) ? this._langIt.nativeElement : undefined;
  }

  private get langEn(): HTMLElement {
    return isObject(this._langEn) ? this._langEn.nativeElement : undefined;
  }

  public changeLang(lang :string){
    let language: Language;
    if(lang === 'it'){
      this.langIt.className ='selected';
      this.langEn.className ='noSelected';
      this.store.addElement(AppCostants.STORE_LANG,'it');
      language={"label": "Italiano",
      "code": "it-IT",
      "alternativeCode": "it",
      "currency": "EUR",                
      "visualDateFormat": "dd/MM/yyyy"}      
    }else{
      this.langEn.className ='selected';
      this.langIt.className ='noSelected';
     
      this.store.addElement(AppCostants.STORE_LANG,'en');
      language={"label": "English (GB)",
      "code": "en-GB",
      "alternativeCode": "en",
      "currency": "EUR",      
      "visualDateFormat": "MM/dd/yyyy"}      
    }
    
    this.localeService.useLanguage(language);  
    this.translate.setDefaultLang(language.code);                    
    this.translate.use(language.code); 
    this.primeNGConfig.setTranslation(this.translate.store.translations[language.code].primeng);    
  }

}
