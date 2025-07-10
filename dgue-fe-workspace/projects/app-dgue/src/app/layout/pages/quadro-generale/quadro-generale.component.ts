import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import {
  SdkFormBuilderConfiguration,
  SdkFormBuilderField,
  SdkMessagePanelService,
  SdkMessagePanelTranslate,
} from '@maggioli/sdk-controls';
import { TranslateService } from '@ngx-translate/core';
import * as jsPDF from 'jspdf'; 
import { cloneDeep, each, get, isEmpty, isObject } from 'lodash-es';
import { environment } from 'projects/app-dgue/src/environments/environment';

import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { FormBuilderService } from '../../../services/form-builder.service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { CustomParamsFunctionResponse, FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { CreateXMLValidationUtilsService } from '../../../validators/create-xml-validation-utils-service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';

declare var require: any;
@Component({
  selector: 'app-quadro-generale',
  templateUrl: './quadro-generale.component.html',
  styleUrls: ['./quadro-generale.component.scss'],
})

export class QuadroGeneraleComponent extends BaseComponentComponent implements OnInit, AfterViewInit {

  public risultato: string;
  public previousPage: string;
  public nextPage: string;
  public popolatoEsclusione = true;
  public popolatoSelezione = true
  public pageName: string = AppCostants.PAGE_QUADRO_GENERALE;
  public subDownload: boolean = false;
  public storeFinalObject: any;
  public storeFinalObjectResponse: any;
  public type: string = "";
  public exclusionExists = false;
  public valid: boolean = true;
  public valid2: boolean = true;
  public valid3: boolean = true;
  public valid4: boolean = true;
  public env: string = '';
  public userInfo: string = '';
  public isLoading = false;
  public storeObject: any;


  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('errorspanel2') _errorsPanel2: ElementRef;
  @ViewChild('errorspanel3') _errorsPanel3: ElementRef;
  @ViewChild('errorspanel4') _errorsPanel4: ElementRef;
  @ViewChild('succespanel2') _succesPanel2: ElementRef;
  @ViewChild('succespanel3') _succesPanel3: ElementRef;
  @ViewChild('succespanel4') _succesPanel4: ElementRef;
  @ViewChild('warningspanel') _warningsPanel: ElementRef;
  @ViewChild('warningspanel2') _warningsPanel2: ElementRef;
  @ViewChild('content') content: ElementRef;



  constructor(public formBuildService: FormBuilderService,
    public formBuildUtilsService: FormBuilderUtilsService,
    public store: StoreService,
    public router: Router,
    private translate: TranslateService,
    public dateHelper: SdkDateHelper,
    public tabellatiProvider: TabellatiComboProvider,
    private sdkMessagePanelService: SdkMessagePanelService,
    private restService: RestService,
    private createXMLValidationUtilsService: CreateXMLValidationUtilsService) {

    super(formBuildService, formBuildUtilsService, store, dateHelper, tabellatiProvider, router);
    this.previousPage = AppCostants.PAGE_FINE;
    this.nextPage = "";
  }

  ngAfterViewInit() {
    this.isLoading = true;
    if (environment['env'] != null && environment['env'] === 'dev') {
      this.env = 'dev';
    } else if (environment['env'] != null && environment['env'] === 'prod') {
      this.env = 'prod';
    }

    this.userInfo = this.store.viewElement(AppCostants.STORE_USR);

    this.storeObject = this.store.viewElement(AppCostants.STORE_OBJ);
    let storeObj = this.store.viewElement(AppCostants.STORE_OBJ);
    if (this.userInfo === 'sa') {
      if (environment['serviceProvider'] != null) {
        storeObj.procedura.authority.serviceProvider = environment['serviceProvider'];
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);
      }

    }

    if (this.userInfo === 'eo') {
      if (environment['serviceProvider'] != null) {
        storeObj.procedura.economicOperator.serviceProvider = environment['serviceProvider'];
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);
      }
    }
    //CONTROLLO SE CI SONO CAMPI CON CRITERI DI ESCLUSIONE A SI
    if (this.userInfo === 'eo') {
      this.checkExclusionExists();
    }
    this.checkJSONSection(false);
    if (this.userInfo === 'sa') {
      this.checkSelectionExists();
    }


    if (this.userInfo === 'sa') {
      let cpv = [];
      if (!isEmpty(storeObj.procedura.cpvs)) {
        storeObj.procedura.cpvs.forEach(element => {
          cpv.push(element.cpv);
        });
      }
      let id = undefined;

      if (!isEmpty(storeObj.id)) {
        id = storeObj.id;
      }

      if (storeObj.selezione != null && !isEmpty(storeObj.selezione)) {
        this.modifySelectionLots(storeObj.selezione);
      }


      storeObj.esclusione.paymentTaxes = { ...storeObj.esclusione.paymentTaxes, ...{ "exists": true } }
      storeObj.esclusione.paymentSocialSecurity = { ...storeObj.esclusione.paymentSocialSecurity, ...{ "exists": true } }
      this.storeFinalObject = {
        ...{"owner":storeObj.owner},
        ...storeObj.procedura,
        ...{ "cpvs": cpv },
        ...{
          "criminalConvictions": { "exists": true },
          "corruption": { "exists": true },
          "fraud": { "exists": true },
          "terroristOffences": { "exists": true },
          "moneyLaundering": { "exists": true },
          "childLabour": { "exists": true }
        },
        ...storeObj.esclusione,
        ...storeObj.selezione,
        ...storeObj.fine,
        ...{ 'id': id }
        /*...{'procedureCode':procedureCode}*/
        /*...add*/
};
    } else if (this.userInfo === 'eo') {

      if (!isEmpty(storeObj.procedura.lotsEoTendersTo)) {
        let lotId = [];
        if (!isEmpty(storeObj.procedura.lotsEoTendersTo.lotId) && storeObj.procedura.lotsEoTendersTo.lotId.length > 0) {

          for (let i = 0; i < storeObj.procedura.lotsEoTendersTo.lotId.length; i++) {
            let lot = {};
            lot = { lotId: storeObj.procedura.lotsEoTendersTo.lotId[i].key };
            lotId.push(lot);
          }
          storeObj.procedura.lotsEoTendersTo.lotId = lotId;

        }
      }



      let id = undefined;
      let uuid = undefined;
      let issueDate = undefined;
      let issueTime = undefined;
      let authority = undefined;
      let projectType = undefined;
      if (!isEmpty(storeObj.id)) {
        id = storeObj.id;
      }
      if (!isEmpty(storeObj.id)) {
        uuid = storeObj.uuid;
      }
      if (!isEmpty(storeObj.id)) {
        issueDate = storeObj.issueDate;
      }
      if (!isEmpty(storeObj.id)) {
        issueTime = storeObj.issueTime;
      }
      if (!isEmpty(storeObj.authority)) {
        authority = storeObj.authority;
      }
      if (!isEmpty(storeObj.projectType)) {
        projectType = storeObj.projectType;
      }

      if (storeObj.selezione != null && !isEmpty(storeObj.selezione)) {
        if (this.store.viewElement(AppCostants.EO_COMPILE) === 'true') {
          this.modifySelectionLots(storeObj.selezione);
        } else {
          this.modifySelectionLotsEO(storeObj.selezione);
        }
      }



      this.storeFinalObjectResponse = {
        ...{"owner":storeObj.owner},
        ...storeObj.procedura,
        ...storeObj.esclusione,
        ...storeObj.selezione,
        ...storeObj.fine,
        ...{ 'id': id },
        ...{ 'uuid': uuid },
        ...{ 'issueDate': issueDate },
        ...{ 'issueTime': issueTime },
        ...{ 'authority': authority },
        ...{ 'projectType': projectType }
      };
    }


    //console.log(JSON.stringify(this.storeFinalObject));
    if ((this.userInfo === null || this.userInfo === undefined) && this.pageName !== AppCostants.PAGE_AVVIO) {
      this.router.navigate([AppCostants.PAGE_DGUE]);
    } else {
      //this.populateForm(userInfo,storeObj);    

      this.populateFormProcedeura(this.userInfo, storeObj, AppCostants.PAGE_PROCEDURA);


    }

    setTimeout(() => {
      this.isLoading = false;
    }, 1000);
  }

  populateForm(userInfo: any, storeObj: any): any {
    if (this.pageName === AppCostants.PAGE_QUADRO_GENERALE) {
      this.formBuildService.getJson(this.pageName).subscribe(data => {
        if (userInfo === AppCostants.SA) {
          this.formConfigProcedura = { fields: data.fieldsProceduraSA };
          this.formConfigEsclusione = { fields: data.fieldsEsclusioneSA };
          this.formConfigSelezione = { fields: data.fieldsSelezioneSA };
          this.formConfigFine = { fields: data.fieldsFineSA };
        } else if (userInfo === AppCostants.EO) {
          this.formConfigProcedura = { fields: data.fieldsProceduraOP };
          this.formConfigEsclusione = { fields: data.fieldsEsclusioneOP };
          this.formConfigSelezione = { fields: data.fieldsSelezioneOP };
          this.formConfigFine = { fields: data.fieldsFineOP };
        }
        let formBuilderConfigAll: SdkFormBuilderConfiguration = this.formConfigProcedura;
        formBuilderConfigAll.fields.push(...this.formConfigEsclusione.fields, ...this.formConfigSelezione.fields, ...this.formConfigFine.fields);

        let restObject = null;
        if (!isEmpty(get(storeObj, "procedura"))) {
          restObject = { ...get(storeObj, "procedura"), ...restObject }
        }
        if (!isEmpty(get(storeObj, "esclusione"))) {
          restObject = { ...get(storeObj, "esclusione"), ...restObject }
        }
        if (!isEmpty(get(storeObj, "selezione"))) {
          restObject = { ...get(storeObj, "selezione"), ...restObject }
        }
        if (!isEmpty(get(storeObj, "fine"))) {
          restObject = { ...get(storeObj, "fine"), ...restObject }
        }
        if (restObject != null) {
          formBuilderConfigAll = this.formBuildUtilsService.populateForm(formBuilderConfigAll, true, this.customPopulateFunctionQuadroGenerale, restObject);
        } else {
          formBuilderConfigAll = this.formBuildUtilsService.populateForm(formBuilderConfigAll, false, this.customPopulateFunctionQuadroGenerale);
        }

        this.defaultFormBuilderConfig = cloneDeep(formBuilderConfigAll);
        this.formBuilderConfig = formBuilderConfigAll;
        this.formBuilderConfigObsAll.next(formBuilderConfigAll);
        this.valid = this.createXMLValidationUtilsService.executeValidations(this.formBuilderConfig, this.errorPanel);
      });
    }
  }

  populateFormProcedeura(userInfo: any, storeObj: any, pName: string) {
    if (this.pageName === AppCostants.PAGE_QUADRO_GENERALE) {
      this.formBuildService.getJson(this.pageName).subscribe(data => {
        if (userInfo === AppCostants.SA) {
          this.formConfigProcedura = { fields: data.fieldsProceduraSA };
        } else if (userInfo === AppCostants.EO) {
          this.formConfigProcedura = { fields: data.fieldsProceduraOP };
        }
        let restObject = null;
        if (!isEmpty(get(storeObj, pName))) {
          restObject = get(storeObj, pName)
        }
        if (restObject != null) {
          this.formConfigProcedura = this.formBuildUtilsService.populateForm(this.formConfigProcedura, true, this.customPopulateFunctionQuadroGenerale, restObject);
        } else {
          this.formConfigProcedura = this.formBuildUtilsService.populateForm(this.formConfigProcedura, false);
        }
        this.defaultFormBuilderConfig = cloneDeep(this.formConfigProcedura);
        this.formBuilderConfig = this.formConfigProcedura;
        this.formBuilderConfigObsProcedura.next(this.formConfigProcedura);
        this.valid2 = this.createXMLValidationUtilsService.executeValidations(this.formBuilderConfig, this.errorPanel2);
        if (userInfo === AppCostants.EO && isEmpty(storeObj.procedura.economicOperator.vatNumber) && isEmpty(storeObj.procedura.economicOperator.anotherNationalId)) {
          

          let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
          let messages: Array<string> = [];
          messages.push("E' obbligatorio valorizzare almeno uno dei seguenti campi: partita IVA o il Codice Fiscale (operatore economico)");
          each(messages, (mess: string) => {
            let singleMessage: SdkMessagePanelTranslate = {
              message: mess
            };
            messagesForPanel.push(singleMessage);
          });
          if(this.valid2 === false){
            this.sdkMessagePanelService.append(this.errorPanel2, messagesForPanel);
          } else{
            this.sdkMessagePanelService.showError(this.errorPanel2, messagesForPanel);
          }
          

          this.valid2 = false;
        }
        if (this.valid2) {
          this.sdkMessagePanelService.showSuccess(this.succesPanel2, [{
            message: this.translate.instant("SUCCESS.PANEL_PROCEDURA")
          }]);
        }

        this.populateFormEsclusione(userInfo, storeObj, AppCostants.PAGE_ESCLUSIONE);

      });
    }
  }

  populateFormEsclusione(userInfo: any, storeObj: any, pName: string): any {
    if (this.pageName === AppCostants.PAGE_QUADRO_GENERALE) {
      this.formBuildService.getJson(this.pageName).subscribe(data => {
        if (userInfo === AppCostants.SA) {
          this.formConfigEsclusione = { fields: data.fieldsEsclusioneSA };
        } else if (userInfo === AppCostants.EO) {
          this.formConfigEsclusione = { fields: data.fieldsEsclusioneOP };
        }
        let restObject = null;
        if (!isEmpty(get(storeObj, pName))) {
          restObject = get(storeObj, pName)
        }
        if (restObject != null) {
          this.formConfigEsclusione = this.formBuildUtilsService.populateForm(this.formConfigEsclusione, true, this.customPopulateFunctionQuadroGenerale, restObject);
        } else {
          this.formConfigEsclusione = this.formBuildUtilsService.populateForm(this.formConfigEsclusione, false);
        }
        this.defaultFormBuilderConfig = cloneDeep(this.formConfigEsclusione);
        this.formBuilderConfig = this.formConfigEsclusione;
        this.formBuilderConfigObsEsclusione.next(this.formConfigEsclusione);
        this.valid3 = this.createXMLValidationUtilsService.executeValidations(this.formBuilderConfig, this.errorPanel3);
        this.populateFormSelezione(userInfo, storeObj, AppCostants.PAGE_SELEZIONE);
        if (this.valid3) {
          this.sdkMessagePanelService.showSuccess(this.succesPanel3, [{
            message: this.translate.instant("SUCCESS.PANEL_ESCLUSIONE")
          }]);
        }
      });
    }
  }

  populateFormSelezione(userInfo: any, storeObj: any, pName: string): any {
    if (this.pageName === AppCostants.PAGE_QUADRO_GENERALE) {
      this.formBuildService.getJson(this.pageName).subscribe(data => {
        if (userInfo === AppCostants.SA) {
          this.formConfigSelezione = { fields: data.fieldsSelezioneSA };
        } else if (userInfo === AppCostants.EO) {
          this.formConfigSelezione = { fields: data.fieldsSelezioneOP };
        }
        let restObject = null;
        if (!isEmpty(get(storeObj, pName))) {
          restObject = get(storeObj, pName)
        }
        if (restObject != null) {
          this.formConfigSelezione = this.formBuildUtilsService.populateForm(this.formConfigSelezione, true, this.customPopulateFunctionQuadroGenerale, restObject);
        } else {
          this.formConfigSelezione = this.formBuildUtilsService.populateForm(this.formConfigSelezione, false);
        }

        // TACCONE - IL FORM-GROUP HA PROBLEMI DI VISIBLE CONDITION SE VINCOLATE TRA I FIELD INTERNI
        // ANDREBBE FIXATO DA SDK        
        this.fixCertificateIndependent();
        this.fixCsSuatibilityAuthOrg();

        this.defaultFormBuilderConfig = cloneDeep(this.formConfigSelezione);
        this.formBuilderConfig = this.formConfigSelezione;
        this.formBuilderConfigObsSelezione.next(this.formConfigSelezione);
        this.valid4 = this.createXMLValidationUtilsService.executeValidations(this.formBuilderConfig, this.errorPanel4);
        this.populateFormFine(userInfo, storeObj, AppCostants.PAGE_FINE);
        if (this.valid4) {
          this.sdkMessagePanelService.showSuccess(this.succesPanel4, [{
            message: this.translate.instant("SUCCESS.PANEL_SELEZIONE")
          }]);
        }
      });
    }
  }


  fixCertificateIndependent(){
    let group = this.formBuildUtilsService.getField(this.formConfigSelezione,"certificateIndependentBodiesAboutQaUnboundedGroups");
    if(group){
      let fieldsGroup = group.fieldGroups;
      if(fieldsGroup){
        for(let i = 0; i< fieldsGroup.length; i++){
          let row = fieldsGroup[i];
          if(row){
            let question = this.formBuildUtilsService.getField(row,"certificateIndependentBodiesAboutQaAnswer");
            let description = this.formBuildUtilsService.getField(row,"certificateIndependentBodiesAboutQaDescription");
            if(!isEmpty(question) && !isEmpty(description)){
              if(question.data === 'Si' || question.data === 'Yes'){
                description.visible = false;
              } else {
                description.visible = true;  
              }
            }            
          }
        }
      }
    }
  }

  fixCsSuatibilityAuthOrg(){
    let group = this.formBuildUtilsService.getField(this.formConfigSelezione,"serviceContractsAuthorisationUnboundedGroups");
    if(group){
      let fieldsGroup = group.fieldGroups;
      if(fieldsGroup){
        for(let i = 0; i< fieldsGroup.length; i++){
          let row = fieldsGroup[i];
          if(row){
            let question = this.formBuildUtilsService.getField(row,"serviceContractsAuthorisationAnswer");
            let description = this.formBuildUtilsService.getField(row,"serviceContractsAuthorisationReasonsNotRegistered");
            if(!isEmpty(question) && !isEmpty(description)){
              if(question.data === 'Si' || question.data === 'Yes'){
                description.visible = false;
              } else {
                description.visible = true;  
              }
            }
          }
        }
      }
    }
  }

  populateFormFine(userInfo: any, storeObj: any, pName: string): any {
    if (this.pageName === AppCostants.PAGE_QUADRO_GENERALE) {
      this.formBuildService.getJson(this.pageName).subscribe(data => {
        if (userInfo === AppCostants.SA) {
          this.formConfigFine = { fields: data.fieldsFineSA };
        } else if (userInfo === AppCostants.EO) {
          this.formConfigFine = { fields: data.fieldsFineOP };
        }
        let restObject = null;
        if (!isEmpty(get(storeObj, pName))) {
          restObject = get(storeObj, pName)
        }
        if (restObject != null) {
          this.formConfigFine = this.formBuildUtilsService.populateForm(this.formConfigFine, true, this.customPopulateFunctionQuadroGenerale, restObject,);
        } else {
          this.formConfigFine = this.formBuildUtilsService.populateForm(this.formConfigFine, false);
        }

        this.defaultFormBuilderConfig = cloneDeep(this.formConfigFine);
        this.formBuilderConfig = this.formConfigFine;
        this.formBuilderConfigObsFine.next(this.formConfigFine);
        this.valid = this.valid2 && this.valid3 && this.valid4;
      });
    }
  }





  public downLoadFile = (type) => {

    if (type === 'show') {
      if (this.subDownload === false) {
        this.subDownload = true;
      } else {
        this.subDownload = false;
      }
    }
    if (type === 'pdf') {
      this.isLoading = true;
      if (this.userInfo === 'sa') {
        this.downloadPDFRequest();
      } else {
        this.downloadPDFResponse();
      }

      setTimeout(() => {
        this.isLoading = false;
      }, 1000);
    }
    if (type === 'json') {
      if (this.store.viewElement(AppCostants.STORE_USR) === 'sa') {
        this.type = "request";
      } else if (this.store.viewElement(AppCostants.STORE_USR) === 'eo') {
        this.type = "response";
      }
      this.downloadJson();

    }
    if (type === 'xml') {
      this.isLoading = true;
      if (this.store.viewElement(AppCostants.STORE_USR) === 'sa') {
        this.type = "request";
        this.restService.putJsonRequest(this.storeFinalObject).subscribe(this.downloadXML, this.handleError);
      } else if (this.store.viewElement(AppCostants.STORE_USR) === 'eo') {
        this.type = "response";
        this.restService.putJsonResponse(this.storeFinalObjectResponse).subscribe(this.downloadXML, this.handleError);
      }
      setTimeout(() => {
        this.isLoading = false;
      }, 1000);
    }


  }

  private downloadXML = (response) => {
    let okValidation: boolean = this.checkJSONSection(true);
    if (okValidation === true) {
      const downloadLink = document.createElement("a");
      downloadLink.style.display = "none";
      document.body.appendChild(downloadLink);
      downloadLink.setAttribute("href", window.URL.createObjectURL(response));
      if (this.type === "request") {
        let reqName = AppCostants.XML_REQUEST + ".xml";
        if (this.storeObject.procedura.fileRefByCA != null && this.storeObject.procedura.fileRefByCA.trim() != '') {
          reqName = this.storeObject.procedura.fileRefByCA + "_" + reqName;
        }
        reqName = reqName.replace(/'/g, '');
        downloadLink.setAttribute("download", reqName);

      } else if (this.type === "response") {
        let ragSoc = this.storeObject.procedura.economicOperator.name;
        if(ragSoc.length > 57){
          ragSoc = ragSoc.substring(0,57);
        }
        let resName = ragSoc + "_" + AppCostants.XML_RESPONSE + ".xml";
        if (this.storeObject.procedura.fileRefByCA != null && this.storeObject.procedura.fileRefByCA.trim() != '') {          
          resName = this.storeObject.procedura.fileRefByCA + "_" + resName;
        }
        resName = resName.replace(/'/g, '');
        downloadLink.setAttribute("download", resName);
      }

      downloadLink.click();
      document.body.removeChild(downloadLink);
    }
    this.sdkMessagePanelService.clear(this.errorPanel);
  }

  private handleError = (err: any) => {
    let errors = [];
    errors.push('ERRORS.GENERATION_XML_REQUEST');
    this.showMessages(this.errorPanel, errors, 'E');
  }

  downloadPDFRequest(){
  
    let margins = {
      top: 15,
      bottom: 10,
      left: 15,
      width: 180
    };
    let doc = new jsPDF();
    let okValidation : boolean = this.checkJSONSection(true);
    if(okValidation === true){

      var hiddenElems = this.content.nativeElement.getElementsByTagName('*');
      
      for(var i = 0; i < hiddenElems.length; i++)
      {
          if(hiddenElems[i].checkVisibility() !== true)
          {
              hiddenElems[i].innerHTML = '';
          }
      }
      
      let content="<html><head><style> body{margin:2em;}   .sdk-form-section .title{  margin-top:1em!important;  }  .title{ font-weight : bold; color:#074A8B; font-size:1.5em; margin-top:3.5em; }  .content {    min-height: 450px;    max-width: 1100px;    color: #04498a;    margin: 0 auto;} .datepicker-label, .combobox-label, .textbox-label{    text-align: left;    padding-left: 1em;    margin-right: 4em;    clear:left;    display:block;    float:left;    width: 60%;       }.form-row{    margin-bottom: 1em;    margin-top: 2em;    display: flex;}h3, .h3 {    font-size: 24px;}h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {    font-family: inherit;    font-weight: 500;    line-height: 1.1;    color: inherit;}h2 {    font-size: 1.2em;    font-family: Verdana, Arial, Helvetica, sans-serif;    font-weight: bold;    line-height: 1.5;    margin-left: 2em;} .text-label{    text-align: left;    padding-left: 1em;    margin-right: 4em;    clear:left;    display:block;    float:left;    width: 60%;   margin-top:.5em; margin-bottom: 1em;   font-size:1.2em; } .text-value{  font-weight : bold;   font-size:1.2em; margin-top: .5em!important; margin-bottm:1em!important; } .section-description{ margin-bottom:.5em;  margin-top:.5em; }</style></head><body><div style='font-size:2em;font-weight:bold;'>Documento di gara unico europeo (DGUE)</div>";        
      content= content+this.content.nativeElement.innerHTML+"</body></html>";  
      while(content.includes('<br><br>')){
        content = content.replaceAll('<br><br>','<br>');
      }
      content = this.sanitizeContentForPdf(content);
      const specialElementHandlers = {
        '#editor': function (element, renderer) {
          return true;
        }
      };           
      
      doc.fromHTML(content, margins.left,
        margins.top, {
          width: margins.width 
        },
        function(dispose) {         
          
          doc.save(AppCostants.XML_REQUEST+'.pdf');
          
          
        },
        margins
      ); 
    }    
  }

  sanitizeContentForPdf(content){
    var output = "";
      for (var i=0; i<content.length; i++) {
          if (content.charCodeAt(i) <= 127 
          || content.charCodeAt(i) === 224
          || content.charCodeAt(i) === 232
          || content.charCodeAt(i) === 246
          || content.charCodeAt(i) === 249
          || content.charCodeAt(i) === 242) {
              output += content.charAt(i);
          }
      }
      return output;
  }

  downloadPDFResponse(){
  
    let margins = {
      top: 15,
      bottom: 10,
      left: 15,
      width: 180
    };
    let doc = new jsPDF();
    let okValidation : boolean = this.checkJSONSection(true);
    if(okValidation === true){
      var hiddenElems = this.content.nativeElement.getElementsByTagName('*');;
      
      for(var i = 0; i < hiddenElems.length; i++)
      {
          if(hiddenElems[i].checkVisibility() !== true)
          {
              hiddenElems[i].innerHTML = '';
          }
      }
      let content="<html><head><style> body{margin:2em;}   .sdk-form-section .title{  margin-top:1em!important;  }  .title{ font-weight : bold; color:#074A8B; font-size:1.5em; margin-top:3.5em; }  .content {    min-height: 450px;    max-width: 1100px;    color: #04498a;    margin: 0 auto;} .datepicker-label, .combobox-label, .textbox-label{    text-align: left;    padding-left: 1em;    margin-right: 4em;    clear:left;    display:block;    float:left;    width: 60%;       }.form-row{    margin-bottom: 1em;    margin-top: 2em;    display: flex;}h3, .h3 {    font-size: 24px;}h1, h2, h3, h4, h5, h6, .h1, .h2, .h3, .h4, .h5, .h6 {    font-family: inherit;    font-weight: 500;    line-height: 1.1;    color: inherit;}h2 {    font-size: 1.2em;    font-family: Verdana, Arial, Helvetica, sans-serif;    font-weight: bold;    line-height: 1.5;    margin-left: 2em;} .text-label{    text-align: left;    padding-left: 1em;    margin-right: 4em;    clear:left;    display:block;    float:left;    width: 60%;   margin-top:.5em; margin-bottom: 1em;   font-size:1.2em; } .text-value{  font-weight : bold;   font-size:1.2em; margin-top: .5em!important; margin-bottm:1em!important; } .section-description{ margin-bottom:.5em;  margin-top:.5em; }</style></head><body><div style='font-size:2em;font-weight:bold;'>Documento di gara unico europeo (DGUE)</div>";        
      content= content+this.content.nativeElement.innerHTML+"</body></html>";
      while(content.includes('<br><br>')){
        content = content.replaceAll('<br><br>','<br>');
      }
      content = this.sanitizeContentForPdf(content);
      const specialElementHandlers = {
        '#editor': function (element, renderer) {
          return true;
        }
      };           
      
      doc.fromHTML(content, margins.left,
        margins.top, {
          width: margins.width 
        },
        function(dispose) {         
          
            doc.save(AppCostants.XML_RESPONSE+'.pdf');
          
          
        },
        margins
      ); 
    }    
  }

  downloadJson() {

    var dataStr = "data:text/json;charset=utf-8," + encodeURIComponent(JSON.stringify(this.store.viewElement(AppCostants.STORE_OBJ)));
    const downloadLink = document.createElement("a");
    downloadLink.setAttribute("href", dataStr);
    if (this.type === "request") {
      downloadLink.setAttribute("download", "BOZZA_" + AppCostants.XML_REQUEST + ".json");
    } else if (this.type === "response") {
      downloadLink.setAttribute("download", "BOZZA_" + AppCostants.XML_RESPONSE + ".json");
    }
    downloadLink.click();
    document.body.removeChild(downloadLink);

  }



  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

  private get errorPanel2(): HTMLElement {
    return isObject(this._errorsPanel2) ? this._errorsPanel2.nativeElement : undefined;
  }

  private get errorPanel3(): HTMLElement {
    return isObject(this._errorsPanel3) ? this._errorsPanel3.nativeElement : undefined;
  }

  private get errorPanel4(): HTMLElement {
    return isObject(this._errorsPanel4) ? this._errorsPanel4.nativeElement : undefined;
  }

  private get succesPanel2(): HTMLElement {
    return isObject(this._succesPanel2) ? this._succesPanel2.nativeElement : undefined;
  }

  private get succesPanel3(): HTMLElement {
    return isObject(this._succesPanel3) ? this._succesPanel3.nativeElement : undefined;
  }

  private get succesPanel4(): HTMLElement {
    return isObject(this._succesPanel4) ? this._succesPanel4.nativeElement : undefined;
  }

  private get warningPanel(): HTMLElement {
    return isObject(this._warningsPanel) ? this._warningsPanel.nativeElement : undefined;
  }

  private get warningPanel2(): HTMLElement {
    return isObject(this._warningsPanel2) ? this._warningsPanel2.nativeElement : undefined;
  }

  checkJSONSection(error: boolean): boolean {
    let json = this.store.viewElement(AppCostants.STORE_OBJ);
    let warnings = [];
    let errors = [];
    let esito: boolean = true;
    if (error) {
      if (isEmpty(get(json, AppCostants.PAGE_ESCLUSIONE))) {
        errors.push('ERRORS.POPOLAMENTO.POPOLATO_ESCLUSIONE');
        this.showMessages(this.errorPanel, errors, 'E');
        esito = false;
      }
      if (isEmpty(get(json, AppCostants.PAGE_SELEZIONE))) {
        errors.push('ERRORS.POPOLAMENTO.POPOLATO_SELEZIONE');
        this.showMessages(this.errorPanel, errors, 'E');
        esito = false;
      }
    } else {
      if (isEmpty(get(json, AppCostants.PAGE_ESCLUSIONE))) {
        warnings.push('WARNINGS.POPOLAMENTO.POPOLATO_ESCLUSIONE');
        this.showMessages(this.warningPanel, warnings, 'W');
        esito = false;
      }
      if (isEmpty(get(json, AppCostants.PAGE_SELEZIONE))) {
        warnings.push('WARNINGS.POPOLAMENTO.POPOLATO_SELEZIONE');
        this.showMessages(this.warningPanel, warnings, 'W');
        esito = false;
      }
    }
    return esito;
  }

  private showMessages(panel: HTMLElement, messages: Array<string>, type: string): void {
    if (!isEmpty(messages)) {
      let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
      each(messages, (mess: string) => {
        let singleMessage: SdkMessagePanelTranslate = {
          message: mess
        };
        messagesForPanel.push(singleMessage);
      });
      if (type === 'E') {
        this.sdkMessagePanelService.clear(panel);
        this.sdkMessagePanelService.showError(panel, messagesForPanel);
      } else if (type === 'W') {
        this.sdkMessagePanelService.clear(panel);
        this.sdkMessagePanelService.showWarning(panel, messagesForPanel);
      }
    }
  }



  private customPopulateFunctionQuadroGenerale = (field: SdkFormBuilderField, restObject?: any, dynamicField?: boolean): CustomParamsFunctionResponse => {

    let mapping: boolean = true;

    if (field.listCode == 'sino') {
      if (this.store.viewElement(AppCostants.STORE_LANG) == 'it') {
        if (get(restObject, field.mappingInput) === true) {
          field.data = "Si";
        } else if (get(restObject, field.mappingInput) === false) {
          field.data = "No";
        } else {
          field.data = "No";
        }
        mapping = false;
      } else {
        if (get(restObject, field.mappingInput) === true) {
          field.data = "Yes";
        } else if (get(restObject, field.mappingInput) === false) {
          field.data = "No";
        } else {
          field.data = "No";
        }
        mapping = false;
      }

    }

    if (AppCostants.LIST_CODE.includes(field.listCode)) {
      field.data = this.tabellatiProvider.getTabellatoDescription(field.listCode, get(restObject, field.mappingInput));
      mapping = false;
    }
    if (AppCostants.DATE_CODE.includes(field.code)) {
      let value = get(restObject, field.mappingInput);
      if (value != null) {
        field.data = this.dateHelper.format(new Date(value), 'dd/MM/yyyy');
        mapping = false;
      }
    }
    return {
      mapping,
      field
    };
  }

  public manageFormOutputAll(formConfigAll: SdkFormBuilderConfiguration) {
    this.formConfigAll = formConfigAll;
  }
  public manageFormOutputProcedura(formConfigProcedura: SdkFormBuilderConfiguration) {
    this.formConfigProcedura = formConfigProcedura;
  }
  public manageFormOutputEsclusione(formConfigEsclusione: SdkFormBuilderConfiguration) {
    this.formConfigEsclusione = formConfigEsclusione;
  }
  public manageFormOutputSelezione(formConfigSelezione: SdkFormBuilderConfiguration) {
    this.formConfigSelezione = formConfigSelezione;
  }
  public manageFormOutputFine(formConfigFine: SdkFormBuilderConfiguration) {
    this.formConfigFine = formConfigFine;
  }


  checkExclusionExists() {
    let json = this.store.viewElement(AppCostants.STORE_OBJ);
    let warnings = [];
    let errors = [];

    let esclusione = get(json, AppCostants.PAGE_ESCLUSIONE);
    let exclusionExists = false;
    for (let crit in esclusione) {
      if(esclusione[crit].exists == true){
        //console.log("Key:" + crit);
        if (esclusione[crit].answer == true && crit != 'guiltyMisinterpretation') {
          exclusionExists = true;
        } else if(esclusione[crit].answer == false && crit == 'guiltyMisinterpretation'){
          exclusionExists = true;
        }
      }
    }
    if (exclusionExists) {

      warnings.push('ATTENZIONE: l\'operatore economico ha selezionato uno o più criteri di esclusione!');
      this.showMessages(this.warningPanel2, warnings, 'W');
    }
    return exclusionExists;
  }

  checkSelectionExists() {
    let json = this.store.viewElement(AppCostants.STORE_OBJ);
    let warnings = [];


    let selezione = get(json, AppCostants.PAGE_SELEZIONE);
    let selectionExists = false;
    let count: number = 0;
    let atLeastOne: boolean = false;
    for (let crit in selezione) {
      //console.log("Key:" + crit);
      if (selezione[crit].exists == false) {
        count++;
      } else if (selezione[crit].exists == true) {
        atLeastOne = true
      }
      //console.log("answer:",esclusione[crit].answer);
    }
    if (!atLeastOne) {
      warnings.push('Nessun criterio di selezione impostato');
      this.showMessages(this.warningPanel2, warnings, 'W');
    } else if (count > 0) {
      warnings.push(count + ' criteri di selezione non sono stati impostati');
      this.showMessages(this.warningPanel2, warnings, 'W');
    }
    return selectionExists;
  }


  private scrollToMessagePanel(messagesPanel: HTMLElement): void {
    let ofTop: number = messagesPanel.offsetTop > 100 ? messagesPanel.offsetTop : 100;
    window.scrollTo({
      top: ofTop - 100,
      left: 0,
      behavior: 'auto'
    });
  }

  private modifySelectionLots(selectionObj: any) {
    if (!isEmpty(selectionObj.enrolmentProfessionalRegister.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.enrolmentProfessionalRegister.unboundedGroups.length; i++) {
        if (selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids != null && selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].registerName,
            url: selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.enrolmentProfessionalRegister.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.enrolmentTradeRegister.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.enrolmentTradeRegister.unboundedGroups.length; i++) {
        if (selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids != null && selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.enrolmentTradeRegister.unboundedGroups[i].registerName,
            url: selectionObj.enrolmentTradeRegister.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.enrolmentTradeRegister.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.serviceContractsAuthorisation.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.serviceContractsAuthorisation.unboundedGroups.length; i++) {
        if (selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids != null && selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.serviceContractsAuthorisation.unboundedGroups[i].registerName,
            url: selectionObj.serviceContractsAuthorisation.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.serviceContractsAuthorisation.unboundedGroups = unboundedGroups;
    }

    if (!isEmpty(selectionObj.serviceContractsMembership.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.serviceContractsMembership.unboundedGroups.length; i++) {
        if (selectionObj.serviceContractsMembership.unboundedGroups[i].lotids != null && selectionObj.serviceContractsMembership.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.serviceContractsMembership.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.serviceContractsMembership.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.serviceContractsMembership.unboundedGroups[i].registerName,
            url: selectionObj.serviceContractsMembership.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.serviceContractsMembership.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.professionalRiskInsurance.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.professionalRiskInsurance.unboundedGroups.length; i++) {
        if (selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids != null && selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            unboundedGroups: selectionObj.professionalRiskInsurance.unboundedGroups[i].unboundedGroups
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.professionalRiskInsurance.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.otherEconomicFinancialRequirements.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.otherEconomicFinancialRequirements.unboundedGroups.length; i++) {
        if (selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids != null && selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            typeOfRequirement: selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].typeOfRequirement,
            unboundedGroups: selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].unboundedGroups,
            unboundedGroups2: selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].unboundedGroups2
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.otherEconomicFinancialRequirements.unboundedGroups = unboundedGroups;
    }







    /*if(!isEmpty(selectionObj.workContractsPerformanceOfWorks) ){
      let lotId = [];
      if(!isEmpty(selectionObj.workContractsPerformanceOfWorks.lotId) && selectionObj.workContractsPerformanceOfWorks.lotId.length > 0){
       
        for(let i = 0; i < selectionObj.workContractsPerformanceOfWorks.lotId.length; i++){
          let lot = {};
          lot = selectionObj.workContractsPerformanceOfWorks.lotId[i].key;
          lotId.push(lot);
        }
        selectionObj.workContractsPerformanceOfWorks.lotId = lotId;
        
      }
    }

    if(!isEmpty(selectionObj.supplyContractsPerformanceDeliveries) ){
      let lotId = [];
      if(!isEmpty(selectionObj.supplyContractsPerformanceDeliveries.lotId) && selectionObj.supplyContractsPerformanceDeliveries.lotId.length > 0){
       
        for(let i = 0; i < selectionObj.supplyContractsPerformanceDeliveries.lotId.length; i++){
          let lot = {};
          lot = selectionObj.supplyContractsPerformanceDeliveries.lotId[i].key;
          lotId.push(lot);
        }
        selectionObj.supplyContractsPerformanceDeliveries.lotId = lotId;
        
      }
    }

    if(!isEmpty(selectionObj.serviceContractsPerformanceServices) ){
      let lotId = [];
      if(!isEmpty(selectionObj.serviceContractsPerformanceServices.lotId) && selectionObj.serviceContractsPerformanceServices.lotId.length > 0){
       
        for(let i = 0; i < selectionObj.serviceContractsPerformanceServices.lotId.length; i++){
          let lot = {};
          lot = selectionObj.serviceContractsPerformanceServices.lotId[i].key;
          lotId.push(lot);
        }
        selectionObj.serviceContractsPerformanceServices.lotId = lotId;
        
      }
    }
*/
    if (!isEmpty(selectionObj.workContractsPerformanceOfWorks)) {
      let lotId = [];
      if (!isEmpty(selectionObj.workContractsPerformanceOfWorks.lotId) && selectionObj.workContractsPerformanceOfWorks.lotId.length > 0) {

        for (let i = 0; i < selectionObj.workContractsPerformanceOfWorks.lotId.length; i++) {
          let lot = {};
          lot = { lotId: selectionObj.workContractsPerformanceOfWorks.lotId[i].key };
          lotId.push(lot);
        }
        selectionObj.workContractsPerformanceOfWorks.lotId = lotId;

      }
    }

    if (!isEmpty(selectionObj.supplyContractsPerformanceDeliveries)) {
      let lotId = [];
      if (!isEmpty(selectionObj.supplyContractsPerformanceDeliveries.lotId) && selectionObj.supplyContractsPerformanceDeliveries.lotId.length > 0) {

        for (let i = 0; i < selectionObj.supplyContractsPerformanceDeliveries.lotId.length; i++) {
          let lot = {};
          lot = { lotId: selectionObj.supplyContractsPerformanceDeliveries.lotId[i].key };
          lotId.push(lot);
        }
        selectionObj.supplyContractsPerformanceDeliveries.lotId = lotId;

      }
    }

    if (!isEmpty(selectionObj.serviceContractsPerformanceServices)) {
      let lotId = [];
      if (!isEmpty(selectionObj.serviceContractsPerformanceServices.lotId) && selectionObj.serviceContractsPerformanceServices.lotId.length > 0) {

        for (let i = 0; i < selectionObj.serviceContractsPerformanceServices.lotId.length; i++) {
          let lot = {};
          lot = { lotId: selectionObj.serviceContractsPerformanceServices.lotId[i].key };
          lotId.push(lot);
        }
        selectionObj.serviceContractsPerformanceServices.lotId = lotId;

      }
    }







  }


  private modifySelectionLotsEO(selectionObj: any) {
    if (!isEmpty(selectionObj.enrolmentProfessionalRegister.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.enrolmentProfessionalRegister.unboundedGroups.length; i++) {
        if (selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids != null && selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].registerName,
            url: selectionObj.enrolmentProfessionalRegister.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.enrolmentProfessionalRegister.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.enrolmentTradeRegister.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.enrolmentTradeRegister.unboundedGroups.length; i++) {
        if (selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids != null && selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.enrolmentTradeRegister.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.enrolmentTradeRegister.unboundedGroups[i].registerName,
            url: selectionObj.enrolmentTradeRegister.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.enrolmentTradeRegister.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.serviceContractsAuthorisation.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.serviceContractsAuthorisation.unboundedGroups.length; i++) {
        if (selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids != null && selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.serviceContractsAuthorisation.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.serviceContractsAuthorisation.unboundedGroups[i].registerName,
            url: selectionObj.serviceContractsAuthorisation.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.serviceContractsAuthorisation.unboundedGroups = unboundedGroups;
    }

    if (!isEmpty(selectionObj.serviceContractsMembership.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.serviceContractsMembership.unboundedGroups.length; i++) {
        if (selectionObj.serviceContractsMembership.unboundedGroups[i].lotids != null && selectionObj.serviceContractsMembership.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.serviceContractsMembership.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.serviceContractsMembership.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.serviceContractsMembership.unboundedGroups[i].registerName,
            url: selectionObj.serviceContractsMembership.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.serviceContractsMembership.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.professionalRiskInsurance.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.professionalRiskInsurance.unboundedGroups.length; i++) {
        if (selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids != null && selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.professionalRiskInsurance.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.professionalRiskInsurance.unboundedGroups[i].registerName,
            url: selectionObj.professionalRiskInsurance.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.professionalRiskInsurance.unboundedGroups = unboundedGroups;
    }
    if (!isEmpty(selectionObj.otherEconomicFinancialRequirements.unboundedGroups)) {
      let unboundedGroups = [];
      for (let i = 0; i < selectionObj.otherEconomicFinancialRequirements.unboundedGroups.length; i++) {
        if (selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids != null && selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids.length > 0) {
          let lotId = [];
          for (let j = 0; j < selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids.length; j++) {
            let lot = {};
            lot = selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotids[j].key;
            lotId.push(lot);
          }
          let unboundedObj = {
            lotId: lotId,
            registerName: selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].registerName,
            url: selectionObj.otherEconomicFinancialRequirements.unboundedGroups[i].url
          };
          unboundedGroups.push(unboundedObj);
        }
      }
      if (unboundedGroups.length != 0)
        selectionObj.otherEconomicFinancialRequirements.unboundedGroups = unboundedGroups;
    }


    /** cabiare questi 3, devo sistemare l'uonbounded groups per l'oe */


    if (!isEmpty(selectionObj.workContractsPerformanceOfWorks.unboundedGroups)) {
      for (let i = 0; i < selectionObj.workContractsPerformanceOfWorks.unboundedGroups.length; i++) {
        let lotId = [];
        if (selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId != null && selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId.length > 0) {
          for (let j = 0; j < selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId.length; j++) {
            let lot = {};
            lot = { "lotId": selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId[j].key };
            lotId.push(lot);
          }
          selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId = lotId;
        } else {
          selectionObj.workContractsPerformanceOfWorks.unboundedGroups[i].lotId = { lotId: null };
        }
      }
    }

    if (!isEmpty(selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups)) {
      for (let i = 0; i < selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups.length; i++) {
        let lotId = [];
        if (selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId != null && selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId.length > 0) {
          for (let j = 0; j < selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId.length; j++) {
            let lot = {};
            lot = { "lotId": selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId[j].key };
            lotId.push(lot);
          }
          selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId = lotId;
        } else {
          selectionObj.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId = { lotId: null };
        }
      }
    }

    if (!isEmpty(selectionObj.serviceContractsPerformanceServices.unboundedGroups)) {
      for (let i = 0; i < selectionObj.serviceContractsPerformanceServices.unboundedGroups.length; i++) {
        let lotId = [];
        if (selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId != null && selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId.length > 0) {
          for (let j = 0; j < selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId.length; j++) {
            let lot = {};
            lot = { "lotId": selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId[j].key };
            lotId.push(lot);
          }
          selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId = lotId;
        } else {
          selectionObj.serviceContractsPerformanceServices.unboundedGroups[i].lotId = { lotId: null };
        }
      }
    }
  }

}
