import { Component, OnInit, ChangeDetectorRef, ElementRef, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { SdkFormBuilderConfiguration, SdkFormBuilderField, SdkMessagePanelService, SdkMessagePanelTranslate } from '@maggioli/sdk-controls';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { find, isEmpty, isObject, set } from 'lodash-es';


@Component({
  selector: 'app-selezione',
  templateUrl: './selezione.component.html',
  styleUrls: ['./selezione.component.scss']
})
export class SelezioneComponent extends BaseComponentComponent implements OnInit {

  
  @ViewChild('warningspanel') _warningspanel: ElementRef;
  
  showBreadcrumbs : boolean;
  pageName: string = AppCostants.PAGE_SELEZIONE;
  selectUser :boolean=true;
  userInfo: string;
  public isLoading = false;
  public lotData = [];
  public pagename: string;

  constructor(public formBuildService : FormBuilderService, 
    public formBuildUtilsService : FormBuilderUtilsService,
     public store :StoreService,
     public dateHelper: SdkDateHelper,
     public tabellatiProvider: TabellatiComboProvider,
     public router:Router,
     private cdr: ChangeDetectorRef,
     public sdkMessagePanelService: SdkMessagePanelService,) {
      super(formBuildService, formBuildUtilsService,store,dateHelper,tabellatiProvider,router);
    this.showBreadcrumbs=true;
    this.breadcrumbsConfig = {
      classAvvio : "active disabled",
      classProcedura : "active",
      classEsclusione : "active",
      classSelezione : "active",
      classFine : ""
    }

    this.previousPage=AppCostants.PAGE_ESCLUSIONE;
    this.nextPage=AppCostants.PAGE_FINE;
  }

  ngOnInit() {
    this.isLoading = true;
    super.ngOnInit();
    this.userInfo=this.store.viewElement(AppCostants.STORE_USR);
    let jsonStorage = JSON.parse(localStorage.getItem(AppCostants.STORE_OBJ));
    let jsonLots: Array<any> = jsonStorage.procedura.lots;
    if(jsonLots != null){
      if(jsonLots.length == 1){
        this.lotData = [{ key: jsonLots[0].numLot,
                            value: jsonLots[0].numLot }]
      } else {
        this.lotData = [];
      }
    }


    
  }

  ngAfterViewInit(){
    setTimeout(()=>{                                
      this.isLoading = false;   
      let jsonStorage = JSON.parse(localStorage.getItem(AppCostants.STORE_OBJ));      
      if(this.userInfo === 'eo'){
        let selezioneExists = false;     
        for (let crit in jsonStorage.selezione) {          
          if (jsonStorage.selezione[crit].exists == true) {
            selezioneExists = true;
          }
        }
        if(!selezioneExists){
          let messagesForPanel: Array<SdkMessagePanelTranslate> = [];        
          let singleMessage : SdkMessagePanelTranslate = {
            message : "WARNINGS.NO_SELECTION_CRITERIA"
          }          
          messagesForPanel.push(singleMessage);          
          this.sdkMessagePanelService.clear(this.warningsPanel);
          this.sdkMessagePanelService.showWarning(this.warningsPanel,messagesForPanel);
        }                
      }
    }, 1000);
  }

  public manageFormOutput(formConfig: SdkFormBuilderConfiguration) {
    this.formConfig = formConfig;
  }

  public manageFormOutputField(field: SdkFormBuilderField) {
    if(this.userInfo !== 'sa'){
      if(isObject(field) && field.code === 'workContractsPerformanceOfWorksLotId'){         
        if(field.type === 'MULTISELECT'){
          for(let i = 0; i < field.data.length; i++){ 
            field.data[i].value = field.data[i].key;
          }               
          let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse17');
          let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'workContractsPerformanceOfWorks');
          let section3: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'workContractsPerformanceOfWorksUnboundedGroups2');
          
          let lotId: SdkFormBuilderField = find(section3.defaultFormGroupFields, (one: SdkFormBuilderField) => one.code === 'workContractsPerformanceOfWorksLotId2');             
          lotId.subject.next(field.data);
        }     
      }
  
      if(isObject(field) && field.code === 'supplyContractsPerformanceDeliveriesLotId'){         
        if(field.type === 'MULTISELECT'){
          for(let i = 0; i < field.data.length; i++){ 
            field.data[i].value = field.data[i].key;
          }               
          let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse17');
          let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'supplyContractsPerformanceDeliveries');
          let section3: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'supplyContractsPerformanceDeliveriesUnboundedGroups2');
          
          let lotId: SdkFormBuilderField = find(section3.defaultFormGroupFields, (one: SdkFormBuilderField) => one.code === 'supplyContractsPerformanceDeliveriesLotId2');             
          lotId.subject.next(field.data);
        }     
      }
  
      if(isObject(field) && field.code === 'serviceContractsPerformanceServicesLotId'){         
        if(field.type === 'MULTISELECT'){
          for(let i = 0; i < field.data.length; i++){ 
            field.data[i].value = field.data[i].key;
          }               
          let section: SdkFormBuilderField = find(this.formBuilderConfig.fields, (one: SdkFormBuilderField) => one.code === 'collapse17');
          let section2: SdkFormBuilderField = find(section.fieldSections, (one: SdkFormBuilderField) => one.code === 'serviceContractsPerformanceServices');
          let section3: SdkFormBuilderField = find(section2.fieldSections, (one: SdkFormBuilderField) => one.code === 'serviceContractsPerformanceServicesUnboundedGroups2');
          
          let lotId: SdkFormBuilderField = find(section3.defaultFormGroupFields, (one: SdkFormBuilderField) => one.code === 'serviceContractsPerformanceServicesLotId2');             
          lotId.subject.next(field.data);
        }     
      }
    }        
    
    if(isObject(field) && field.code === 'averageYearlyTurnoverMinRequirement' && field.type !== 'TEXT'){
      let currency = {
          code: "averageYearlyTurnoverCurrency",
          data:{ 
            key: "EUR",
            value: "EUR (Euro)"
          }
        }

      this.formBuilderDataSubject.next(currency);
    }

    if(isObject(field) && field.code === 'specificAverageTurnoverMinRequirement' && field.type !== 'TEXT'){
      let currency = {
          code: "specificAverageTurnoverCurrency",
          data:{
            key: "EUR",
            value: "EUR (Euro)"
          }
        }

      this.formBuilderDataSubject.next(currency);
    }

    if(isObject(field) && field.code === 'specificYearlyTurnoverMinRequirement' && field.type !== 'TEXT'){
      let currency = {
          code: "specificYearlyTurnoverCurrency",
          data:{
            key: "EUR",
            value: "EUR (Euro)"
          }
        }

      this.formBuilderDataSubject.next(currency);
    }
    
    
  }
  
  public manageAddPanel(event:any){
    
  }

  
  protected fixJsonContent(jsonContent: any){           
    let storeUsr = this.store.viewElement(AppCostants.STORE_USR);
    if(storeUsr === AppCostants.EO){
      if(!isEmpty(jsonContent['otherEconomicFinancialRequirements']) && 
        !isEmpty(jsonContent['otherEconomicFinancialRequirements'].unboundedGroups) &&
        jsonContent['otherEconomicFinancialRequirements'].unboundedGroups.length > 0 ){
          jsonContent['otherEconomicFinancialRequirements'].unboundedGroups.forEach(element => {   
            if(!isEmpty(element) && !isEmpty(element.typeOfRequirement)){
              if(element.typeOfRequirement === 'Requisiti economico finanziari' || element.typeOfRequirement === 'Economic and financial requirements'){
                element.typeOfRequirement='RADIO_BUTTON_TRUE';
              }else {
                element.typeOfRequirement='RADIO_BUTTON_FALSE';
              }
            }  
          });
      }
  
      if(!isEmpty(jsonContent['techniciansTechnicalBodies']) && !isEmpty(jsonContent['techniciansTechnicalBodies'].weightedCriterion)){
        if(jsonContent['techniciansTechnicalBodies'].weightedCriterion === 'Si' || jsonContent['techniciansTechnicalBodies'].weightedCriterion === 'Yes'){
          jsonContent['techniciansTechnicalBodies'].weightedCriterion = true;
        }else{
          jsonContent['techniciansTechnicalBodies'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['workContractsTechnicians']) && !isEmpty(jsonContent['workContractsTechnicians'].weightedCriterion)){
        if(jsonContent['workContractsTechnicians'].weightedCriterion === 'Si' || jsonContent['workContractsTechnicians'].weightedCriterion === 'Yes'){
          jsonContent['workContractsTechnicians'].weightedCriterion = true;
        }else{
          jsonContent['workContractsTechnicians'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['technicalFacilitiesMeasures']) && !isEmpty(jsonContent['technicalFacilitiesMeasures'].weightedCriterion)){
        if(jsonContent['technicalFacilitiesMeasures'].weightedCriterion === 'Si' || jsonContent['technicalFacilitiesMeasures'].weightedCriterion === 'Yes'){
          jsonContent['technicalFacilitiesMeasures'].weightedCriterion = true;
        }else{
          jsonContent['technicalFacilitiesMeasures'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['studyResearchFacilities']) && !isEmpty(jsonContent['studyResearchFacilities'].weightedCriterion)){
        if(jsonContent['studyResearchFacilities'].weightedCriterion === 'Si' || jsonContent['studyResearchFacilities'].weightedCriterion === 'Yes'){
          jsonContent['studyResearchFacilities'].weightedCriterion = true;
        }else{
          jsonContent['studyResearchFacilities'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['supplyChainManagement']) && !isEmpty(jsonContent['supplyChainManagement'].weightedCriterion)){
        if(jsonContent['supplyChainManagement'].weightedCriterion === 'Si' || jsonContent['supplyChainManagement'].weightedCriterion === 'Yes'){
          jsonContent['supplyChainManagement'].weightedCriterion = true;
        }else{
          jsonContent['supplyChainManagement'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['environmentalManagementFeatures']) && !isEmpty(jsonContent['environmentalManagementFeatures'].weightedCriterion)){
        if(jsonContent['environmentalManagementFeatures'].weightedCriterion === 'Si' || jsonContent['environmentalManagementFeatures'].weightedCriterion === 'Yes'){
          jsonContent['environmentalManagementFeatures'].weightedCriterion = true;
        }else{
          jsonContent['environmentalManagementFeatures'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['toolsPlantTechnicalEquipment']) && !isEmpty(jsonContent['toolsPlantTechnicalEquipment'].weightedCriterion)){
        if(jsonContent['toolsPlantTechnicalEquipment'].weightedCriterion === 'Si' || jsonContent['toolsPlantTechnicalEquipment'].weightedCriterion === 'Yes'){
          jsonContent['toolsPlantTechnicalEquipment'].weightedCriterion = true;
        }else{
          jsonContent['toolsPlantTechnicalEquipment'].weightedCriterion = false;
        }
      }
      if(!isEmpty(jsonContent['educationalProfessionalQualifications']) && !isEmpty(jsonContent['educationalProfessionalQualifications'].weightedCriterion)){
        if(jsonContent['educationalProfessionalQualifications'].weightedCriterion === 'Si' || jsonContent['educationalProfessionalQualifications'].weightedCriterion === 'Yes'){
          jsonContent['educationalProfessionalQualifications'].weightedCriterion = true;
        }else{
          jsonContent['educationalProfessionalQualifications'].weightedCriterion = false;
        }
      }
    } else{
      if(jsonContent?.serviceContractsPerformanceServices?.exists == true && jsonContent?.serviceContractsPerformanceServices?.lotId != null && jsonContent?.serviceContractsPerformanceServices?.lotId.length == 0){
        jsonContent.serviceContractsPerformanceServices.lotId = [{key:'',value:''}];        
      }
      if(jsonContent?.supplyContractsPerformanceDeliveries?.exists == true && jsonContent?.supplyContractsPerformanceDeliveries?.lotId != null && jsonContent?.supplyContractsPerformanceDeliveries?.lotId.length == 0){
        jsonContent.supplyContractsPerformanceDeliveries.lotId = [{key:'',value:''}];        
      }                      
      if(jsonContent?.workContractsPerformanceOfWorks?.exists == true && jsonContent?.workContractsPerformanceOfWorks?.lotId != null && jsonContent?.workContractsPerformanceOfWorks?.lotId.length == 0){
        jsonContent.workContractsPerformanceOfWorks.lotId = [{key:'',value:''}];        
      }
    }
  }

  private get warningsPanel(): HTMLElement {
    return isObject(this._warningspanel) ? this._warningspanel.nativeElement : undefined;
  }

}
