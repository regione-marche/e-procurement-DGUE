import { Component, OnInit, Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { LocaleService } from '../../../services/locale.service';

@Component({
  selector: 'app-cookies',
  templateUrl: './cookies.html'
})
@Injectable()
export class CookiesComponent implements OnInit {
  


  constructor(public formBuildService : FormBuilderService, 
              public store :StoreService,
              public router:Router,
              private localeService: LocaleService) {
    
    
   }

  ngOnInit() {   
    this.store.addElement(AppCostants.STORE_LANG,'it');
    let language={"label": "Italiano",
    "code": "it-IT",
    "alternativeCode": "it",
    "currency": "EUR",                
    "visualDateFormat": "dd/MM/yyyy"}
    this.localeService.useLanguage(language);
    
  }
  

  

}
