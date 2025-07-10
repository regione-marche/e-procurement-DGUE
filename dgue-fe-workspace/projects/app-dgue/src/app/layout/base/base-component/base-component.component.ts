import { Component, Injectable, OnInit, ɵConsole } from '@angular/core';
import { Router } from '@angular/router';
import { SdkComboBoxItem, SdkFormBuilderConfiguration, SdkFormBuilderField, SdkFormBuilderInput, SdkFormFieldGroupConfiguration } from '@maggioli/sdk-controls';
import { cloneDeep, get, has, isEmpty, set, each, isObject } from 'lodash-es';
import { Subject } from 'rxjs';
import { BreadcrumbsClassConfig } from '../../../model/breadcrumbs-model';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { CustomParamsFunctionResponse, FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { environment } from 'projects/app-dgue/src/environments/environment';
@Component({
  selector: 'app-base-component',
  templateUrl: './base-component.component.html',
  styleUrls: ['./base-component.component.scss']
})
@Injectable()
export class BaseComponentComponent implements OnInit {

  public breadcrumbsConfig : BreadcrumbsClassConfig;
  public pageName: string;
  public pagename: string;
  public nextPage : string;
  public previousPage : string;
  public formConfig: SdkFormBuilderConfiguration;
  public formConfigAll: SdkFormBuilderConfiguration;
  public formConfigProcedura: SdkFormBuilderConfiguration;
  public formConfigEsclusione: SdkFormBuilderConfiguration;
  public formConfigSelezione: SdkFormBuilderConfiguration;
  public formConfigFine: SdkFormBuilderConfiguration;
  public formBuilderConfigObsAll: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsProcedura: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsEsclusione: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsSelezione: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsFine: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObs: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderDataSubject: Subject<SdkFormBuilderInput> = new Subject();
  public defaultFormBuilderConfig: SdkFormBuilderConfiguration;
  public formBuilderConfig: SdkFormBuilderConfiguration;
  public fromXML:boolean;
  public fromPortale:boolean;  
  public fromAppalti:boolean; 
  public userInfo: string;  
  public storeObj: any = {};
  public env: string = ''; 
  public visibleHiddenFunction = false;
  public optionalExclusionCriteria = ["breachingObligationsEnvironmental.exists",
                              "breachingObligationsSocial.exists",
                              "breachingObligationsLabour.exists",
                              "bankruptcy.exists",
                              "insolvency.exists",
                              "arrangementWithCreditors.exists",
                              "analogousSituation.exists",
                              "assetsAdministeredByLiquidator.exists",
                              "businessActivitiesSuspended.exists",
                              "guiltyGrave.exists",
                              "agreementsWithOtherEO.exists",
                              "conflictInterest.exists",
                              "involvementPreparationProcurement.exists",
                              "earlyTermination.exists",
                              "guiltyMisinterpretation.exists",
                              "nationalExclusionGrounds.exists"]
    
  constructor(public formBuildService : FormBuilderService, 
    public formBuildUtilsService : FormBuilderUtilsService,
     public store :StoreService,
     public dateHelper: SdkDateHelper,
     public tabellatiProvider: TabellatiComboProvider,
     public router:Router) {
    this.breadcrumbsConfig = {
      classAvvio : "",
      classProcedura : "",
      classEsclusione : "",
      classSelezione : "",
      classFine : ""
    };
  } 

  ngOnInit() {    
    this.visibleHiddenFunction = this.store.viewElement(AppCostants.VISIBLE_HIDDEN_FUNCTION);
    this.fromAppalti = this.store.viewElement(AppCostants.FROM_APPALTI);
    this.fromPortale=this.store.viewElement(AppCostants.FROM_PORTALE);
    this.fromXML=this.store.viewElement(AppCostants.FROM_XML);
    this.userInfo=this.store.viewElement(AppCostants.STORE_USR);
    let eOCompile=this.store.viewElement(AppCostants.EO_COMPILE);
    if((this.userInfo === null || this.userInfo === undefined) && this.pageName !== AppCostants.PAGE_AVVIO){
      this.router.navigate([AppCostants.PAGE_DGUE]);
    } else {
      this.storeObj =this.store.viewElement(AppCostants.STORE_OBJ);      
      let navigationMap = this.getNavigationMap();
      if(this.pageName != undefined && navigationMap[this.pageName] != undefined){
        this.nextPage = navigationMap[this.pageName].next;
        this.previousPage = navigationMap[this.pageName].prev;
      }
      
      if(this.getFormBuilderPages().indexOf(this.pageName) != -1){
        this.formBuildService.getJson(this.pageName).subscribe(data => {          
          if(this.userInfo===AppCostants.SA){
            this.formConfig = {fields: data.fieldsSA};
          }else if(this.userInfo===AppCostants.EO){
            if(eOCompile){
              this.formConfig = {fields: data.fieldsOP};              
            }
            else{
              this.formConfig = {fields: data.fieldsOPExtend};
            }
          }

          let restObject = null;
          if(!isEmpty(get(this.storeObj, this.pageName))){
            restObject = get(this.storeObj, this.pageName)
          }
          if(restObject != null){
            this.formConfig = this.formBuildUtilsService.populateForm(this.formConfig, true, this.customPopulateFunction, restObject);
            //console.log("restObject: ",restObject)
          } else {
            this.formConfig = this.formBuildUtilsService.populateForm(this.formConfig, false, this.customPopulateFunction);
          }      
          if(this.pageName === AppCostants.PAGE_ESCLUSIONE || 
            this.pageName === AppCostants.PAGE_SELEZIONE ||
            this.pageName === AppCostants.PAGE_PROCEDURA){

              this.afterRenderingFunction(); 
            }   
          this.defaultFormBuilderConfig = cloneDeep(this.formConfig);
          this.formBuilderConfig = this.formConfig;
          this.formBuilderConfigObs.next(this.formConfig);
        });
      }
    }
    
  }
  
  afterRenderingFunction(){    
    if (isObject(this.formConfig)) {
      each(this.formConfig.fields, (one: SdkFormBuilderField) => {
          if (one.type === 'FORM-SECTION') {
              one = this.elaborateSection(one);
          } else if (one.type === 'FORM-GROUP') {
              one = this.elaborateGroup(one);
          } else {
              one = this.elaborateOne(one);
          }
      });
    }
  }
 
  getNavigationMap(){
    return {
      "avvio" :{ prev : AppCostants.PAGE_DGUE, next : AppCostants.PAGE_PROCEDURA},
      "procedura" :{ prev : AppCostants.PAGE_AVVIO, next : AppCostants.PAGE_ESCLUSIONE},
      "esclusione" :{ prev : AppCostants.PAGE_PROCEDURA, next : AppCostants.PAGE_SELEZIONE},
      "selezione" :{ prev : AppCostants.PAGE_ESCLUSIONE, next : AppCostants.PAGE_FINE},
      "fine" :{ prev : AppCostants.PAGE_SELEZIONE, next : AppCostants.PAGE_QUADRO_GENERALE}
    }
  }

  getFormBuilderPages(){
    return [AppCostants.PAGE_PROCEDURA,AppCostants.PAGE_ESCLUSIONE,AppCostants.PAGE_SELEZIONE, AppCostants.PAGE_FINE];
  }

 


  public preNext = () => {
    if(this.getFormBuilderPages().indexOf(this.pageName) != -1){
      let jsonContent = this.formBuildService.getFormRequest(this.formConfig);      
      let storeObject=this.store.viewElement(AppCostants.STORE_OBJ,true);
      this.fixJsonContent(jsonContent);
      storeObject[this.pageName] = jsonContent;
      //console.log(jsonContent)      
      this.store.addElement(AppCostants.STORE_OBJ,storeObject,true);   
      this.replaceNumLotto(); 
    }
  }
    

  public prePrevious = () =>{
    if(this.getFormBuilderPages().indexOf(this.pageName) != -1){
      let jsonContent = this.formBuildService.getFormRequest(this.formConfig);  
      let storeObject=this.store.viewElement(AppCostants.STORE_OBJ,true);
      this.fixJsonContent(jsonContent);
      storeObject[this.pageName] = jsonContent;      
      this.store.addElement(AppCostants.STORE_OBJ,storeObject,true); 
      this.replaceNumLotto();
    }
  }

 
  public procedura = () =>{
    if(this.pageName!=AppCostants.PAGE_AVVIO){
      this.router.navigate([AppCostants.PAGE_PROCEDURA]);
      this.preNext();
    }
  }
  public esclusione = () =>{
    if(this.pageName!=AppCostants.PAGE_AVVIO){
      this.router.navigate([AppCostants.PAGE_ESCLUSIONE]);
      this.preNext();
    }
  }
  public selezione = () =>{
    if(this.pageName!=AppCostants.PAGE_AVVIO){
      this.router.navigate([AppCostants.PAGE_SELEZIONE]);
      this.preNext();
    }
  }
  public fine = () =>{
    if(this.pageName!=AppCostants.PAGE_AVVIO){
      this.router.navigate([AppCostants.PAGE_FINE]);
      this.preNext();
    }
  }
  

  protected fixJsonContent(jsonContent: any){    
  }

  public customPopulateFunction = (field: any, restObject?: any, dynamicField?: boolean): CustomParamsFunctionResponse => {    
    let mapping: boolean = true;
    
    let combo = get(restObject, field.mappingInput);
   
    if(field.code === 'numberLot' /*|| field.code === 'caLotsMaxLot' || field.code === 'caLotsMaxLotTender'*/){  
      let numberLot = 0;
      if(this.storeObj.procedura.lots != null && this.storeObj.procedura.lots.length != null){
        numberLot = this.storeObj.procedura.lots.length;
      }
      set(field, 'data', numberLot);
      mapping = false;       
    }
   
    if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='tipoProcedura' ){   
      if(!has(restObject,'procedureCode')){
        let comboItem2: SdkComboBoxItem = {
            key: 'NOT_SPECIFIED',
            value: 'Not specified'
        }      
        set(field, 'data', comboItem2);
        mapping = false;
      }   
    }

    



    /*if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='ruoloOperatoreEconomico' ){   
      if(!has(restObject,'economicOperator.role')){
        let comboItem2: SdkComboBoxItem;
        if(this.store.viewElement(AppCostants.STORE_LANG) == 'it'){          
          comboItem2 = {
            key: 'SCLE',
            value: 'Aggiudicatario Singolo/ Capogruppo'
          } 
        } else {
          comboItem2 = {
            key: 'SCLE',
            value: 'Sole contractor / Lead entity'
          } 
        }
             
        set(field, 'data', comboItem2);
        mapping = false;
      }   
    }*/
    /*
    if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='typeOfRequirements' ){   
      if(!has(restObject,'typeOfRequirement')){
        let comboItem2: SdkComboBoxItem = {
            key: 'true',
            value: 'Economic or financial requirement'
        }      
        set(field, 'data', comboItem2);
        mapping = false;
      }   
    }*/

    
    /*if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='tipoMoneta' && (isEmpty(field.data) || (isObject(field.data) && get(field.data,'key') == undefined))){      
      
        let comboItem3: SdkComboBoxItem = {
            key: 'EUR',
            value: 'EUR (Euro)'
        }      
        set(field, 'data', comboItem3);
        mapping = false;
      
    }*/
    if(environment['serviceProvider'] != null){
      if(this.userInfo === 'sa' && !this.fromAppalti){
        if(field.code == 'authority.serviceProvider.vatNumber'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.website'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.email'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.name'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.country'){
          field.visible = false;
          mapping = false;
        }
      } else{
        if(field.code == 'authority.serviceProvider.vatNumber'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].vatNumber);
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.website'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].website);
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.email'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].email);
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.name'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].name);
          mapping = false;
        }
        if(field.code == 'authority.serviceProvider.country'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].country);
          mapping = false;
        }
      }
      if(this.userInfo === 'eo' && !this.fromPortale){
        if(field.code == 'economicOperator.serviceProvider.vatNumber'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.website'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.email'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.name'){
          field.visible = false;
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.country'){
          field.visible = false;
          mapping = false;
        }
      }else{
        if(field.code == 'economicOperator.serviceProvider.vatNumber'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].vatNumber);
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.website'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].website);
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.email'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].email);
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.name'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].name);
          mapping = false;
        }
        if(field.code == 'economicOperator.serviceProvider.country'){
          set(field,"type","TEXT");
          set(field,"data",environment['serviceProvider'].country);
          mapping = false;
        }
      }
      
    }
    
    

    if(field.type === AppCostants.TYPE_COMBOBOX && field.code == 'caLotsLotSubmit' && !has(restObject,'caLots.lotSubmit')){
      let comboItem: SdkComboBoxItem = {
        key: 'NOT_SPECIFIED',
        value: 'Non specificato'
      }  
      set(field, 'data', comboItem);
      mapping = false;
    }

    /*
    if(field.code == 'paymentTaxesThreshold' && !has(restObject,'paymentTaxes.threshold') && field.type !== 'TEXT'){
      set(field, 'data', 0);
      mapping = false;
    }
    if(field.code == 'paymentTaxesAdditionalInfo' && !has(restObject,'paymentTaxes.additionalInfo') && field.type !== 'TEXT'){
      set(field, 'data', '--');
        mapping = false;
    }

    if(field.code == 'paymentSocialSecurityThreshold' && !has(restObject,'paymentSocialSecurity.threshold') && field.type !== 'TEXT'){
      set(field, 'data', 0);
      mapping = false;
    }
    if(field.code == 'paymentSocialSecurityAdditionalInfo' && !has(restObject,'paymentSocialSecurity.additionalInfo')  && field.type !== 'TEXT'){
      set(field, 'data', '--');
      mapping = false;
    }
    */

    if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='paesi' ){   
      if(field.code == 'authority.country' && has(restObject,'authority.country')){

      } else if(field.code == 'authority.serviceProvider.country' && has(restObject,'authority.serviceProvider.country')){
      
      } else if(field.code == 'economicOperator.serviceProvider.country' && has(restObject,'economicOperator.serviceProvider.country')){
      
      } else if(field.code == 'economicOperatorCountryCountryName' && has(restObject,'economicOperator.country')){
      
      } else if(field.code == 'countryRepr' && has(restObject,'country')){
      
      } else{
        let comboItem4: SdkComboBoxItem = {
            key: 'IT',
            value: 'Italy'
        }      
        set(field, 'data', comboItem4);
        mapping = false;
      }   
    }   
    if(field.type === AppCostants.TYPE_TEXT ){
      if(field.listCode == 'sino'){    
        if(this.store.viewElement(AppCostants.STORE_LANG) == 'it'){ 
          if(get( restObject, field.mappingInput)===true){
            field.data="Si";
          }else if(get( restObject, field.mappingInput)===false){
            field.data="No";
          }
        }else{
          if(get( restObject, field.mappingInput)===true){
            field.data="Yes";
          }else if(get( restObject, field.mappingInput)===false){
            field.data="No";
          }
        }
        
        mapping=false;
      }
      if(AppCostants.LIST_CODE.includes(field.listCode)){     
        field.data=this.tabellatiProvider.getTabellatoDescription(field.listCode,get(restObject, field.mappingInput));
        mapping = false;
      }      
    }
    return {
      mapping,
      field
    };
  }

  

  protected elaborateOne(
    field: SdkFormBuilderField,
   
  ) {
  //console.log(field);
  if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='sino' && (isEmpty(field.data) || (isObject(field.data) && get(field.data,'key') == undefined)) && !this.optionalExclusionCriteria.includes(field.code)){
    let comboItem: SdkComboBoxItem = {
        key: 'false',
        value: 'No'
    }         
    if(field.visible != false){
      set(field, 'data', comboItem);     
    }   
  }

  if(this.optionalExclusionCriteria.includes(field.code)){
    if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='sino' && (isEmpty(field.data) || (isObject(field.data) && get(field.data,'key') == undefined))){
      let comboItem: SdkComboBoxItem = { key: 'true',value: 'Si'};
      if(this.store.viewElement(AppCostants.STORE_LANG) != 'it'){ 
        comboItem = {
          key: 'true',
          value: 'Yes'
        }
      }      
      if(field.visible != false){
        set(field, 'data', comboItem);     
      }   
    }
  }

  if(field.type === AppCostants.TYPE_COMBOBOX && has(field,'listCode') && get(field,'listCode')==='tipoMoneta' && (isEmpty(field.data) || (isObject(field.data) && get(field.data,'key') == undefined))){      
      
        let comboItem3: SdkComboBoxItem = {
            key: 'EUR',
            value: 'EUR (Euro)'
        }      
        set(field, 'data', comboItem3);
       
      
    }
 
 return field;
}

private elaborateSection(field: any): SdkFormBuilderField {

    each(field.fieldSections, (one: SdkFormBuilderField) => {
        if (one.type === 'FORM-SECTION') {
            one = this.elaborateSection(one);                       
        } else if (one.type === 'FORM-GROUP') {
            one = this.elaborateGroup(one);
        } else {
            one = this.elaborateOne(one);
        }
    });
    return field;
}

private elaborateGroup(field: SdkFormBuilderField): SdkFormBuilderField {
   
    each(field.defaultFormGroupFields, (one: SdkFormBuilderField) => {
        if (one.type === 'FORM-SECTION') {
            one = this.elaborateSection(one);
        } else if (one.type === 'FORM-GROUP') {
            one = this.elaborateGroup(one);
        } else {
            one = this.elaborateOne(one);
        }
    });

    each(field.fieldGroups, (group: SdkFormFieldGroupConfiguration, index: number) => {
        each(group.fields, (one: SdkFormBuilderField) => {
            if (one.type === 'FORM-SECTION') {
                one = this.elaborateSection(one);
            } else if (one.type === 'FORM-GROUP') {
                one = this.elaborateGroup(one);
            } else {
                one = this.elaborateOne(one);
            }
        });
        field.fieldGroups[index] = group;
    });
  

    return field;
  }

  arrayBufferToBase64( buffer ) {
    var binary = '';
    var bytes = new Uint8Array( buffer );
    var len = bytes.byteLength;
    for (var i = 0; i < len; i++) {
      binary += String.fromCharCode( bytes[ i ] );
    }
    return window.btoa( binary );
  }

  private replaceNumLotto(){
    let storeObj =this.store.viewElement(AppCostants.STORE_OBJ);
    
    let lotti = storeObj['procedura'].lots;
    if(lotti != null && lotti.length > 0 && !isEmpty(lotti)){
      lotti.forEach(lotto => {
        if(lotto.numLot != null && !lotto.numLot.includes('LOT-')){
          while(lotto.numLot.toString().length < 4){
            lotto.numLot = '0'+lotto.numLot;
          }
          lotto.numLot = 'LOT-' + lotto.numLot;
        }
      });
      storeObj['procedura'].lots = lotti;
  
      this.store.addElement(AppCostants.STORE_OBJ,storeObj);
    }
    
  }


}
