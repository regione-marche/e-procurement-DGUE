import { Component, Injectable, OnInit } from '@angular/core';
import { Router } from '@angular/router';

import packageIson from '../../../../../../../package.json';
import { FormBuilderService } from '../../../services/form-builder.service';
import { LocaleService } from '../../../services/locale.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';

@Component({
  selector: 'app-credits',
  templateUrl: './credits.html'
})
@Injectable()
export class CreditsComponent implements OnInit {

  public appVersion: string = '';

  constructor(public formBuildService: FormBuilderService,
    public store: StoreService,
    public router: Router,
    private localeService: LocaleService) {


  }

  ngOnInit() {
    this.store.addElement(AppCostants.STORE_LANG, 'it');
    let language = {
      "label": "Italiano",
      "code": "it-IT",
      "alternativeCode": "it",
      "currency": "EUR",
      "visualDateFormat": "dd/MM/yyyy"
    }
    this.localeService.useLanguage(language);
    this.appVersion = packageIson.version;

  }




}
