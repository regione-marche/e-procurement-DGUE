import { Component, HostListener, OnInit, OnDestroy } from '@angular/core';
import { SdkProviderService, SdkValidatorService } from '@maggioli/sdk-commons';
import { providers } from './app.providers';
import { validators } from './app.validators';
import { StoreModel } from './model/store-model';
import { StoreService } from './services/storeService';
import { AppCostants } from './utils/dgue-constants';
import { map } from 'lodash-es';
import { PrimeNGConfig } from 'primeng/api';
import { TranslateService } from '@ngx-translate/core';



@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit,OnDestroy{
  title = 'app-dgue';
  
  translatePrimeNg:any;
  
  storeObject :StoreModel = {
    procedura:{},
    esclusione:{},
    selezione:{},
    fine:{}
  }
  
  constructor(
    private provider: SdkProviderService,
    private validator: SdkValidatorService,
    private store: StoreService,
    private primeNGConfig: PrimeNGConfig,
    private  translate: TranslateService) {
  
  }
  ngOnDestroy(): void {
    this.translatePrimeNg.unSubscribe();
  }

  public ngOnInit(): void {
    this.initProviders();
    this.initValidators();   
    this.store.clearStore();
    this.store.addElement(AppCostants.STORE_OBJ,this.storeObject);
    this.translatePrimeNg = this.translate.get('primeng').subscribe((res) => {
      this.primeNGConfig.setTranslation(res);      
    });
  }

  onActivate(event) {
    window.scroll(0,0);
  }
  
  @HostListener('window:unload', ['$event'])
  public unloadHandler(event) {
    this.store.clearStore();
  }

  @HostListener('window:beforeunload', ['$event'])
  public beforeUnloadHander(event) {
      event.returnValue = false;
  }

  /**
   * @description
   * @author Cristiano Perin
   * @date 2020-01-20
   * @private
   * @memberof AppComponent
   */
  private initProviders(): void {
    this.provider.init(providers());
  }


  /**
   * @description
   * @author Cristiano Perin
   * @date 2020-01-20
   * @private
   * @memberof AppComponent
   */
  private initValidators(): void {
    this.validator.init(validators());
  }

  
}
