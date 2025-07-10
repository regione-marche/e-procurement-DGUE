import { Component, Injectable, OnInit, ElementRef, ViewChild, ChangeDetectorRef } from '@angular/core';
import { Router } from '@angular/router';
import { SdkFormBuilderConfiguration, SdkFormFieldGroupConfiguration, SdkFormBuilderField, SdkAutocompleteItem, SdkMessagePanelService, SdkMessagePanelTranslate, SdkMultiselectItem, SdkFormBuilderInput, SdkTextOutput } from '@maggioli/sdk-controls';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';
import { GaralottiQuertyparam } from '../../../model/garalotti-queryparam-model';
import { isEmpty, get, each, isObject, isUndefined, set, findIndex, find, map } from 'lodash-es';
import { RestService } from '../../../services/rest-service';
import { GaraLottiInfo } from '../../../model/garalotti-model';
import { GaraLottiinfoResponse } from '../../../model/garaLottiInfo-response';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { subscribeOn } from 'rxjs/operators';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { of, Subject } from 'rxjs';
import { SelectItem } from 'primeng/api';
import { environment } from 'projects/app-dgue/src/environments/environment';

@Component({
  selector: 'app-procedura',
  templateUrl: './procedura.component.html',
  styleUrls: ['./procedura.component.scss']
})
@Injectable()
export class ProceduraComponent extends BaseComponentComponent implements OnInit {

  showBreadcrumbs: boolean;
  nextPage: string;
  prevPage: string;
  namePage: string;
  pageName: string = AppCostants.PAGE_PROCEDURA;
  selectUser: boolean = true;
  modalform: GaralottiQuertyparam = {
    numeroGaraAnac: "",
    cig: "",
    oggetto: "",
    denomStazioneAppaltante: "",
    token: null
  };
  
  showModelTable: boolean = false;
  garaLottiInfoResp: Array<GaraLottiinfoResponse> = new Array<GaraLottiinfoResponse>();
  garaLotti: Array<GaraLottiInfo> = new Array<GaraLottiInfo>();
  displayDialog: boolean = false;
  user: string;
  password: string;
  lottoInfoString: Array<string> = [];
  lotsList: Array<any> = [];
  public isLoading = false;
  public numberLotValue = 0;
  public lotSubmit;

  @ViewChild('errorspanel') _errorsPanel: ElementRef;

  constructor(public formBuildService : FormBuilderService, 
    public formBuildUtilsService : FormBuilderUtilsService,
     public store :StoreService,
     public dateHelper: SdkDateHelper,
     public tabellatiProvider: TabellatiComboProvider,
     public router:Router,
     private cdr: ChangeDetectorRef) {
      super(formBuildService, formBuildUtilsService,store,dateHelper,tabellatiProvider,router);
    this.showBreadcrumbs = true;
    this.breadcrumbsConfig = {
      classAvvio: "active disabled",
      classProcedura: "active",
      classEsclusione: "",
      classSelezione: "",
      classFine: ""
    };
    

    this.previousPage = AppCostants.PAGE_AVVIO;
    this.nextPage = AppCostants.PAGE_ESCLUSIONE;
  }

  ngOnInit() {

    this.isLoading = true;
    let serviceProvider = {};

    this.lotSubmit = this.storeObj.procedura?.caLots?.lotSubmit
    if(environment['serviceProvider'] != null){
      serviceProvider =environment['serviceProvider']
      let storeObj =this.store.viewElement(AppCostants.STORE_OBJ);
      let userInfo=this.store.viewElement(AppCostants.STORE_USR);
      
      if(userInfo === 'sa'){
        if(storeObj['procedura'] != null){
          if(storeObj['procedura'].authority != null){
            set(storeObj['procedura'].authority, "serviceProvider",environment['serviceProvider'])
          } else{
            storeObj['procedura'] = {...storeObj['procedura'],...{"authority":{"serviceProvider":serviceProvider}}};
          }
        }
      }
      if(userInfo === 'eo'){
        if(storeObj['procedura'] != null){
          if(storeObj['procedura'].economicOperator != null){
            set(storeObj['procedura'].economicOperator, "serviceProvider",environment['serviceProvider'])
          } else{
            storeObj['procedura'] = {...storeObj['procedura'],...{"economicOperator":{"serviceProvider":serviceProvider}}};
          }
        }
      }
     

      
                 
      this.store.addElement(AppCostants.STORE_OBJ,storeObj);  
    }   
    
    super.ngOnInit();
    this.user = this.store.viewElement(AppCostants.STORE_USR);
    
  }

  ngAfterViewInit(){
    setTimeout(()=>{                                
      this.isLoading = false;
    }, 1000);
  }

  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

  public manageFormOutput(formConfig: SdkFormBuilderConfiguration) {
    this.formConfig = formConfig;    
  }

  public manageRemovePanel(field: SdkFormBuilderField){
    let actualLotsList = this.getGroupLottoField(field);
    let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse7');
    if(section != null){
      let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'collapse99');
      if(section2 != null){
        let lotsEoTendersToLotId: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'lotsEoTendersToLotId');          
        lotsEoTendersToLotId.subject.next(actualLotsList);   
      }
      
    }
    
  }

  public manageOutputField(field: SdkFormBuilderField) {
    
    
    if((field.code === 'numLot') && !isEmpty(field.data)){
  
      
      let actualLotsList = this.getGroupLottoField(field);
      let lotto:any = this.checkDuplicate(field);
      let lotsIntersection = [];
      let exists = 0;
      if(!isEmpty(lotto)){
               
        for(let i = 0;i < actualLotsList.length; i++){
          if(actualLotsList[i].key === lotto.key){
            exists++;
          }
        }
        
        if(exists>1){
          set(field, 'data', undefined);
          let lotField: any = {
            code: field.code,
            groupCode: field.groupCode,
            data: undefined
          };
          alert('Numero lotto con = '+lotto.key+' già esistente');
          this.formBuilderDataSubject.next(lotField);
          this.formBuilderConfigObs.next(this.formConfig);
        } 
  
  
      }
      if(this.user == 'eo'){
        let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse7');
        if(section != null && section.fieldSections != null){
          let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'collapse99');
          let lotsEoTendersToLotId: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'lotsEoTendersToLotId'); 
          if(lotsEoTendersToLotId != null && lotsEoTendersToLotId.subject != null){
            lotsEoTendersToLotId.subject.next(actualLotsList);   
          }         
        }
      }
    } else if(field.code === 'caLotsLotSubmit' && this.user == 'sa'){    
      if(this.lotSubmit != field.data?.key){
        this.lotSubmit = undefined;
        let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse11');
        if(section != null && section.fieldSections != null){
          let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'collapse12');
          if(section2 != null && section.fieldSections != null){
  
            let caLotsMaxLotValue;
            let caLotsMaxLotTenderValue;
            if(field.data?.key === 'LOT_ONE'){
              caLotsMaxLotValue = 1;
              caLotsMaxLotTenderValue = 1;
            } else if(field.data?.key === 'LOT_ONE_MORE'){
              caLotsMaxLotValue = undefined;
              caLotsMaxLotTenderValue = undefined;
            } else if(field.data?.key === 'LOT_ALL'){
              caLotsMaxLotValue = +this.numberLotValue;
              caLotsMaxLotTenderValue = +this.numberLotValue;
            }
      
            
            let caLotsMaxLot: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'caLotsMaxLot'); 
            if(caLotsMaxLot != null){
              let caLotsMaxLotField: any = {
                code: caLotsMaxLot.code,
                data: caLotsMaxLotValue
              };
              this.formBuilderDataSubject.next(caLotsMaxLotField); 
            }  
            
            let caLotsMaxLotTender: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'caLotsMaxLotTender'); 
            if(caLotsMaxLotTender != null){
              let caLotsMaxLotTenderField: any = {
                code: caLotsMaxLotTender.code,
                data: caLotsMaxLotTenderValue
              };
              this.formBuilderDataSubject.next(caLotsMaxLotTenderField);   
            } 
          }      
        }
      }
      
    } else if(field.code === 'numberLot' && this.user == 'sa' && !this.fromAppalti && !this.fromPortale){           
      this.numberLotValue = field.data;
    } 
    
  }

  public manageFormClick(config: SdkTextOutput): void {
    console.log(config);
  }

  private getGroupLottoField(field: SdkFormBuilderField){
    let collapse = 'collapse11';
    if(this.user == 'eo'){
      collapse = 'collapse7'
    }
    const section: SdkFormBuilderField = find(this.formConfig.fields, (one: SdkFormBuilderField) => one.code === collapse);  
    if(section != null && section.fieldSections != null){
      const lots: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'lots');  
      let lotti = [];
      for(let i = 0;i < lots.fieldGroups.length; i++){
        let numLot=lots.fieldGroups[i].fields[0].data;        
        if(lots.fieldGroups[i].code === field.groupCode){        
          if(field.code === 'numLot'){
            numLot = field.data;
          }
        }
        if(numLot != null){
          lotti.push({
            key:numLot,
            value:numLot
          })
        }
       
      }
      return lotti;
    }
  }

  private checkDuplicate(field: SdkFormBuilderField){
    let collapse = 'collapse11';
    if(this.user == 'eo'){
      collapse = 'collapse7'
    }
    const section: SdkFormBuilderField = find(this.formConfig.fields, (one: SdkFormBuilderField) => one.code === collapse);  
    if(section != null && section.fieldSections != null){
      const lots: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'lots');  
      let lotto = {};
      for(let i = 0;i < lots.fieldGroups.length; i++){
        let numLot=lots.fieldGroups[i].fields[0].data;        
        if(lots.fieldGroups[i].code === field.groupCode){        
          if(field.code === 'numLot'){
            numLot = field.data;
          }
          if(numLot != null){
            lotto ={
              key:numLot,
              value:numLot
            }
          }
          break;
        }           
      }
      return lotto;
    }
    
  }


 


  public arraysEqual(a1,a2) {
    /* WARNING: arrays must not contain {objects} or behavior may be undefined */
    return JSON.stringify(a1)==JSON.stringify(a2);
}




  showDialog() {
    this.displayDialog = true;
  }

 /* resetModal() {
    this.showModelTable = false;
    this.garaLottiInfoResp = [];
    this.user = "";
    this.password = "";
    this.modalform = {
      numeroGaraAnac: "",
      cig: "",
      oggetto: "",
      denomStazioneAppaltante: "",
      token: null
    };
  }*/

  

  lessThen(i) {
    return i < 5 ? true : false;
  }

  compreso(i: number, array: Array<any>) {
    if (i + 1 < array.length && i < 4) {
      return true;
    }
    return false;
  }
}
