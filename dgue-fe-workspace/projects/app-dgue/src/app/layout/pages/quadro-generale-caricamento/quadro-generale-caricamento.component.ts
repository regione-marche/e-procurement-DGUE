import { Component, ElementRef, Injectable, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Params, Router } from '@angular/router';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import {
  SdkFormBuilderConfiguration,
  SdkFormBuilderField,
  SdkFormBuilderInput,
  SdkMessagePanelService,
  SdkMessagePanelTranslate,
} from '@maggioli/sdk-controls';
import * as jsPDF from 'jspdf'; 
import { cloneDeep, each, get, isEmpty, isObject, set } from 'lodash-es';
import { CookieService } from 'ngx-cookie-service';
import { environment } from 'projects/app-dgue/src/environments/environment';
import { Subject } from 'rxjs';

import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { FormBuilderService } from '../../../services/form-builder.service';
import { LocaleService } from '../../../services/locale.service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { CustomParamsFunctionResponse, FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';

@Component({
  selector: 'app-quadro-generale-caricamento',
  templateUrl: './quadro-generale-caricamento.component.html'
})
@Injectable()
export class QuadroGeneraleCaricamentoComponent implements OnInit {

  
  public fromPortale = true;
  public xml: any;
  public userInfo = '';
  public storeObj: any;
  public risultato: string;
  public previousPage: string;
  public nextPage: string;
  public popolatoEsclusione = true;
  public popolatoSelezione = true
  public pagename: string = AppCostants.PAGE_QUADRO_GENERALE_APPALTI;
  public pageName: string = AppCostants.PAGE_QUADRO_GENERALE;
  public subDownload: boolean = false;
  public storeFinalObject: any;
  public storeFinalObjectResponse: any;
  public type: string = "";
  public exclusionExists = false;
  public valid: boolean = true;
  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('warningspanel') _warningsPanel: ElementRef;
  @ViewChild('content') content: ElementRef;

  public formConfig: SdkFormBuilderConfiguration;
  public formConfigProcedura: SdkFormBuilderConfiguration;
  public formConfigEsclusione: SdkFormBuilderConfiguration;
  public formConfigSelezione: SdkFormBuilderConfiguration;
  public formConfigFine: SdkFormBuilderConfiguration;
  public formBuilderConfigObsProcedura: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsEsclusione: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsSelezione: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObsFine: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderConfigObs: Subject<SdkFormBuilderConfiguration> = new Subject();
  public formBuilderDataSubject: Subject<SdkFormBuilderInput> = new Subject();
  public defaultFormBuilderConfig: SdkFormBuilderConfiguration;
  public formBuilderConfig: SdkFormBuilderConfiguration;
  public formBuilderConfigObsAll: Subject<SdkFormBuilderConfiguration> = new Subject();
  public env: string = '';
  private cookieData: any;
  public dgueRequest: string = null;
  public dgueResponse: string = null;
  public isLoading = false;

  constructor(public formBuildService: FormBuilderService,
    public formBuildUtilsService: FormBuilderUtilsService,
    public store: StoreService,
    public router: Router,
    private dateHelper: SdkDateHelper,
    private tabellatiProvider: TabellatiComboProvider,
    private sdkMessagePanelService: SdkMessagePanelService,
    private restService: RestService,
    private cookieService: CookieService,
    private activatedRoute: ActivatedRoute,
    private localeService: LocaleService) {

    this.previousPage = "";
    this.nextPage = "";
  }

  ngOnInit() {    
    if (environment['env'] != null && environment['env'] === 'dev') {
      this.env = 'dev';
    } else if (environment['env'] != null && environment['env'] === 'prod') {
      this.env = 'prod';
    }
    this.store.addElement(AppCostants.STORE_LANG, 'it');
    let language = {
      "label": "Italiano",
      "code": "it-IT",
      "alternativeCode": "it",
      "currency": "EUR",
      "visualDateFormat": "dd/MM/yyyy"
    }
    this.localeService.useLanguage(language);
    let userInfo = this.store.viewElement(AppCostants.STORE_USR);

    let storeObject = this.store.viewElement(AppCostants.STORE_OBJ);

    this.populateForm(userInfo,storeObject);  
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
      });
    }
  }

  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

  private get warningPanel(): HTMLElement {
    return isObject(this._warningsPanel) ? this._warningsPanel.nativeElement : undefined;
  }



  private showMessages(messages: Array<string>, type: string): void {
    if (!isEmpty(messages)) {
      let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
      each(messages, (mess: string) => {
        let singleMessage: SdkMessagePanelTranslate = {
          message: mess
        };
        messagesForPanel.push(singleMessage);
      });
      if (type === 'E') {
        this.sdkMessagePanelService.clear(this.errorPanel);
        this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
      }else if (type === 'W') {
        this.sdkMessagePanelService.clear(this.warningPanel);
        this.sdkMessagePanelService.showWarning(this.warningPanel, messagesForPanel);
      }
    }
  }



  private customPopulateFunctionQuadroGenerale = (field: SdkFormBuilderField, restObject?: any, dynamicField?: boolean): CustomParamsFunctionResponse => {

    let mapping: boolean = true;
    let arrayCode = AppCostants.ARRAY_CODE_COMBO_SINO;
    let storeObj = this.store.viewElement(AppCostants.IMPORTED_XML);
    if (field.code === 'numberLot') {
      let numberLot = 0;
      if (storeObj.lots != null && storeObj.lots.length != null) {
        numberLot = storeObj.lots.length;
      }
      set(field, 'data', numberLot);
      mapping = false;

    }

    if (field.listCode == 'sino' || arrayCode.includes(field.code)) {
      if (get(restObject, field.mappingInput) === true) {
        field.data = "Si";
      } else if (get(restObject, field.mappingInput) === false) {
        field.data = "No";
      }
      mapping = false;
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

  public manageFormOutputAll(formBuilderConfig: SdkFormBuilderConfiguration) {
    this.formBuilderConfig = formBuilderConfig;
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
      this.showMessages(warnings, 'W');
    }
    return exclusionExists;
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
      this.downloadPDF();
    }
    if (type === 'xml') {
      if (this.store.viewElement(AppCostants.STORE_USR) === 'sa') {
        this.type = "request";
        this.restService.putJsonRequest(this.xml).subscribe(this.downloadXML, this.handleError);
      } else if (this.store.viewElement(AppCostants.STORE_USR) === 'eo') {
        this.type = "response";
        this.restService.putJsonResponse(this.xml).subscribe(this.downloadXML, this.handleError);
      }

    }
  }

  private downloadXML = (response) => {

    const downloadLink = document.createElement("a");
    downloadLink.style.display = "none";
    document.body.appendChild(downloadLink);
    downloadLink.setAttribute("href", window.URL.createObjectURL(response));
    if (this.type === "request") {
      downloadLink.setAttribute("download", AppCostants.XML_REQUEST + ".xml");
    } else if (this.type === "response") {
      downloadLink.setAttribute("download", AppCostants.XML_RESPONSE + ".xml");
    }

    downloadLink.click();
    document.body.removeChild(downloadLink);

    this.sdkMessagePanelService.clear(this.errorPanel);
  }

  private handleError = (err: any) => {
    let errors = [];
    errors.push('ERRORS.GENERATION_XML_REQUEST');
    this.showMessages(errors, 'E');
  }

  downloadPDF(){
    let margins = {
      top: 15,
      bottom: 10,
      left: 15,
      width: 180
    };
    let doc = new jsPDF();
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
    content = this.sanitizeContentForPdf(content);
    while(content.includes('<br><br>')){
      content = content.replaceAll('<br><br>','<br>');
    }
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

}