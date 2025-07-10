import { Component, Injectable, OnInit, ElementRef, ViewChild, Injector } from '@angular/core';
import { Router, ActivatedRoute, ParamMap, Params } from '@angular/router';
import { AppCostants } from '../../../utils/dgue-constants';
import { CookieService } from 'ngx-cookie-service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { StoreModel } from '../../../model/store-model';
import { isEmpty, isObject, each, set } from 'lodash-es';
import { SdkMessagePanelService, SdkMessagePanelTranslate, SdkMultiselectItem } from '@maggioli/sdk-controls';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';
import { SdkBaseService, SdkHttpLoaderService, SdkHttpLoaderType } from '@maggioli/sdk-commons';
import { LocaleService } from '../../../services/locale.service';
import { Language } from '../../../model/app-settings.models';
import { DatePipe } from '@angular/common';

@Component({
  selector: 'app-dgue-home-portale',
  templateUrl: './dgue-home-portale.component.html'
})
@Injectable()
export class DGUEHomePortaleComponent implements OnInit {
  
  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('warningspanel') _warningsPanel: ElementRef;
  private cookieData:any;
  public fromPortale: boolean=true;
  public isToken: boolean=false;
  public isLoad: boolean=false;
  public isHome: boolean=false;
  public operation:string;
  public showOptionImportCreate: boolean = false;
  fromXML: boolean = false;
  xmlSender: string = '';
  selectUser: boolean = false;
  userInfo: string = AppCostants.EO;
  private browserName: string;
  
  @ViewChild('attachments') attachments: ElementRef;

  storeObject: StoreModel = {
    procedura: {},
    esclusione: {},
    selezione: {},
    fine: {}
  }

 

  private importXml: string;
  

  constructor(private router:Router,
    private cookieService:CookieService,
    private restService: RestService,
    public store: StoreService,
    private activatedRoute: ActivatedRoute,
    public sdkMessagePanelService: SdkMessagePanelService,
    public sdkHttpLoaderService: SdkHttpLoaderService,
    private localeService: LocaleService,
    public datepipe: DatePipe) { 

  this.store.clearStore();                   
  }


  ngOnInit() {    
    this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);
    this.store.addElement(AppCostants.STORE_LANG,'it');
    let language={"label": "Italiano",
    "code": "it-IT",
    "alternativeCode": "it",
    "currency": "EUR",                
    "visualDateFormat": "dd/MM/yyyy"}
    this.localeService.useLanguage(language);
    
    this.store.addElement(AppCostants.FROM_PORTALE,this.fromPortale);
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
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
    if (cookieTokenString != null && cookieTokenString != '') { 
      this.isToken=true;              
      this.cookieData=this.parseJwt(cookieTokenString);      
      this.restService.getInfoTest(this.cookieData.data.urlServizio,cookieTokenString).subscribe((res: any)=>{   
          this.isHome=false;
          this.store.addElement(AppCostants.INFO_APPALTI,res.data);
          this.store.addElement(AppCostants.FROM_PORTALE,this.fromPortale);
          this.isLoad=false;
          let lotti = res.data.lotti;
          let info = {anagOE:res.data.infoOE}
          this.importXml = res.data.dgueRequest;          
          let lots: any[] = [];
          if(!isEmpty(res.data.infoOE)){
        
            let cigArray : Array<SdkMultiselectItem>= []
            lotti.forEach(element => {             
              
              cigArray.push({
                key: element.lotId,
                value: element.lotId
              });
              
            });
            

            let representatives = [];

            info.anagOE.legaleRappresentanti.forEach(element => {
              let legaleRappresentante = {};
              legaleRappresentante = {...legaleRappresentante,...{"firstName":element.nome}};
              legaleRappresentante = {...legaleRappresentante,...{"lastName":element.cognome}};  
              legaleRappresentante = {...legaleRappresentante,...{"fiscalCode":element.codiceFiscale}};  
              const words = element.dataNascita.split('/');
              element.dataNascita = new Date(words[2],words[1]-1,words[0]);
              let latest_date =this.datepipe.transform(element.dataNascita, 'yyyy-MM-dd');
              legaleRappresentante = {...legaleRappresentante,...{"dateOfBirth":latest_date}};
              legaleRappresentante = {...legaleRappresentante,...{"placeOfBirth":element.comuneNascita}};
              legaleRappresentante = {...legaleRappresentante,...{"street":element.indirizzo +', '+ element.numCivico}};
              legaleRappresentante = {...legaleRappresentante,...{"postalCode":element.cap}};
              legaleRappresentante = {...legaleRappresentante,...{"city":element.comune}};
              legaleRappresentante = {...legaleRappresentante,...{"country":element.nazione}};
              legaleRappresentante = {...legaleRappresentante,...{"position":element.tipoSoggetto}};
              representatives.push(legaleRappresentante);
            });

            let componentiRti = [];
            info.anagOE.componentiRti.forEach(element => {
              let componenteRti = {};
              componenteRti = {...componenteRti,...{"description1":element.ragioneSociale}};
              if(isEmpty(element.partitaIVA)){
                componenteRti = {...componenteRti,...{"description2":element.idFiscaleEstero}};
              }else{
                componenteRti = {...componenteRti,...{"description2":element.partitaIVA}};
              }
              componenteRti = {...componenteRti,...{"description3":element.tipoImpresa}};      
              componentiRti.push(componenteRti);
            });

            let ruolo = {};
            if(componentiRti != null && componentiRti.length > 0){
              ruolo = "LE";
            }else{
              ruolo =  "SC";
            }

            let eoTogetherWithOthers = {"answer":false,
            "description1":"",
            "description2":"",
            "description3":""};
            if(info.anagOE.componentiRti != null && info.anagOE.componentiRti.length > 0){
              let nomeRti = '';                           
              info.anagOE.componentiRti.forEach(element => {                
                nomeRti = element.ragioneSociale + ',' + nomeRti;
              });

              eoTogetherWithOthers.answer = true
              eoTogetherWithOthers.description1 = 'LE';
              eoTogetherWithOthers.description2 = nomeRti;
              eoTogetherWithOthers.description3 = info.anagOE.denominazioneRTI;
            }

            
            let reliedEntitiesAnswer = false;
            if(componentiRti.length > 0){
              reliedEntitiesAnswer = true;
            }
            
            
            let mapInfo = {
              "lotsEoTendersTo":{"lotId":cigArray},             
              "economicOperator.profileURI":info.anagOE.webSite,
              "economicOperator.role":ruolo,
              "economicOperator.postalCode":info.anagOE.cap,
              "economicOperator.vatNumber":info.anagOE.partitaIva,
              "economicOperator.anotherNationalId":info.anagOE.codiceFiscale,
              "economicOperator.country":info.anagOE.nazione,
              "economicOperator.name":info.anagOE.ragioneSociale,
              "economicOperator.contactPhone":info.anagOE.telefono,
              "economicOperator.contactEmail":info.anagOE.email,
              "economicOperator.city":info.anagOE.citta,
              "economicOperator.street":info.anagOE.indirizzo,
              "economicOperator.representatives":representatives,              
              //"reliedEntities.unboundedGroups":componentiRti,
              //"reliedEntities.answer":reliedEntitiesAnswer,
              "eoTogetherWithOthers":eoTogetherWithOthers,
              "economicOperator.serviceProvider.vatNumber":AppCostants.MAG_VAT_NUMBER,
              "economicOperator.serviceProvider.website":res.data.serviceProviderURL,              
              "economicOperator.serviceProvider.name":AppCostants.MAG_NAME,
              "economicOperator.serviceProvider.country":AppCostants.MAG_COUNTRY
            }
            
            this.storeObject['procedura'] = mapInfo;
            this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);
          }else{
            if(!isEmpty(lotti)){
              lotti.forEach(element => {             
                let lotcigJSON = {        
                  "lotId": element
                }
        
                lots.push(lotcigJSON);
              });
              let mapInfo = {
                "lotsEoTendersTo":{"lotId":lots}
              }
              this.storeObject['procedura'] = mapInfo;
              this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);
            }
          }
          this.isLoad=true;
          this.isHome=true;
        },err => {
          this.isHome=true;
          console.log(err)
          messages.push("Errore in fase di redirect dal portale appalti");
          each(messages, (mess: string) => {
            let singleMessage: SdkMessagePanelTranslate = {
              message: mess
            };
            messagesForPanel.push(singleMessage);
          });
          this.sdkMessagePanelService.clear(this.errorPanel);
          this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
        }     
      );    
    }else{
      this.isHome = true;
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

  private get warningPanel(): HTMLElement {
    return isObject(this._warningsPanel) ? this._warningsPanel.nativeElement : undefined;
  }

  parseJwt (token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
        return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  };

  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

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


  start(){
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    if(this.isToken && !isEmpty(this.importXml)){
      this.store.addElement(AppCostants.FROM_XML, false);
      this.store.addElement(AppCostants.STORE_USR, AppCostants.EO);
                          
        this.restService.putXmlRequest(this.importXml, 'SA').subscribe((res :any)=> {
          //console.log(res['response'].test)
          let authority = res.response.espd.authority;
          let storeObj={
            "id":res.response.espd.id,
            "uuid":res.response.espd.uuid,
            "issueDate":res.response.espd.issueDate,
            "issueTime":res.response.espd.issueTime,        
            "authority":authority,
            "owner": res.response.espd.owner,
            "procedura":{
              "codiceANAC":res.response.espd.codiceANAC,
              "procedureCode":res.response.espd.procedureCode,
              "authority":authority,
              "procedureTitle":res.response.espd.procedureTitle,
              "procedureShortDesc":res.response.espd.procedureShortDesc,
              "fileRefByCA":res.response.espd.fileRefByCA,
              "additionalDocumentReference":res.response.espd.additionalDocumentReference,
              "lots":res.response.espd.lots,
              "eoShelteredWorkshop":res.response.espd.eoShelteredWorkshop,
              "eoRegistered":res.response.espd.eoRegistered,
              "eoTogetherWithOthers":res.response.espd.eoTogetherWithOthers,
              "reliedEntities":res.response.espd.reliedEntities,
              "notReliedEntities":res.response.espd.notReliedEntities,
              "lotsEoTendersTo":res.response.espd.lotsEoTendersTo,
              "eoReductionOfCandidates":res.response.espd.eoReductionOfCandidates,
              "caLots":res.response.espd.caLots,
              "eoSme":res.response.espd.eoSme,
              "suppEvidence":res.response.espd.suppEvidence,
              "suppEvidenceOther":res.response.espd.suppEvidenceOther,
              "projectType":res.response.espd.projectType
            },
            "esclusione":{
              "criminalConvictions":res.response.espd.criminalConvictions,
              "corruption":res.response.espd.corruption,
              "fraud":res.response.espd.fraud,
              "terroristOffences":res.response.espd.terroristOffences,
              "moneyLaundering":res.response.espd.moneyLaundering,
              "childLabour":res.response.espd.childLabour,
              "paymentTaxes":res.response.espd.paymentTaxes,
              "paymentSocialSecurity":res.response.espd.paymentSocialSecurity,
              "breachingObligationsEnvironmental":res.response.espd.breachingObligationsEnvironmental == null ? {"exists":false} : res.response.espd.breachingObligationsEnvironmental,
              "breachingObligationsSocial":res.response.espd.breachingObligationsSocial == null ? {"exists":false} : res.response.espd.breachingObligationsSocial,
              "breachingObligationsLabour":res.response.espd.breachingObligationsLabour == null ? {"exists":false} : res.response.espd.breachingObligationsLabour,
              "bankruptcy":res.response.espd.bankruptcy == null ? {"exists":false} : res.response.espd.bankruptcy,
              "insolvency":res.response.espd.insolvency == null ? {"exists":false} : res.response.espd.insolvency,
              "arrangementWithCreditors":res.response.espd.arrangementWithCreditors == null ? {"exists":false} : res.response.espd.arrangementWithCreditors,
              "analogousSituation":res.response.espd.analogousSituation == null ? {"exists":false} : res.response.espd.analogousSituation,
              "assetsAdministeredByLiquidator":res.response.espd.assetsAdministeredByLiquidator == null ? {"exists":false} : res.response.espd.assetsAdministeredByLiquidator,
              "businessActivitiesSuspended":res.response.espd.businessActivitiesSuspended == null ? {"exists":false} : res.response.espd.businessActivitiesSuspended,
              "guiltyGrave":res.response.espd.guiltyGrave == null ? {"exists":false} : res.response.espd.guiltyGrave,
              "agreementsWithOtherEO":res.response.espd.agreementsWithOtherEO == null ? {"exists":false} : res.response.espd.agreementsWithOtherEO,
              "conflictInterest":res.response.espd.conflictInterest == null ? {"exists":false} : res.response.espd.conflictInterest,
              "involvementPreparationProcurement":res.response.espd.involvementPreparationProcurement == null ? {"exists":false} : res.response.espd.involvementPreparationProcurement,
              "earlyTermination":res.response.espd.earlyTermination == null ? {"exists":false} : res.response.espd.earlyTermination,
              "guiltyMisinterpretation":res.response.espd.guiltyMisinterpretation == null ? {"exists":false} : res.response.espd.guiltyMisinterpretation,
              "nationalExclusionGrounds":res.response.espd.nationalExclusionGrounds == null ? {"exists":false} : res.response.espd.nationalExclusionGrounds
            },
            "selezione": {
              "enrolmentProfessionalRegister":res.response.espd.enrolmentProfessionalRegister == null ? {"exists":false} : res.response.espd.enrolmentProfessionalRegister,
              "enrolmentTradeRegister":res.response.espd.enrolmentTradeRegister == null ? {"exists":false} : res.response.espd.enrolmentTradeRegister,
              "serviceContractsAuthorisation":res.response.espd.serviceContractsAuthorisation == null ? {"exists":false} : res.response.espd.serviceContractsAuthorisation,
              "serviceContractsMembership":res.response.espd.serviceContractsMembership == null ? {"exists":false} : res.response.espd.serviceContractsMembership,
              "generalYearlyTurnover":res.response.espd.generalYearlyTurnover == null ? {"exists":false} : res.response.espd.generalYearlyTurnover,
              "averageYearlyTurnover":res.response.espd.averageYearlyTurnover == null ? {"exists":false} : res.response.espd.averageYearlyTurnover,
              "specificYearlyTurnover":res.response.espd.specificYearlyTurnover == null ? {"exists":false} : res.response.espd.specificYearlyTurnover,
              "specificAverageTurnover":res.response.espd.specificAverageTurnover == null ? {"exists":false} : res.response.espd.specificAverageTurnover,
              "setupEconomicOperator":res.response.espd.setupEconomicOperator == null ? {"exists":false} : res.response.espd.setupEconomicOperator,
              "financialRatio":res.response.espd.financialRatio == null ? {"exists":false} : res.response.espd.financialRatio,
              "professionalRiskInsurance":res.response.espd.professionalRiskInsurance == null ? {"exists":false} : res.response.espd.professionalRiskInsurance,
              "otherEconomicFinancialRequirements":res.response.espd.otherEconomicFinancialRequirements == null ? {"exists":false} : res.response.espd.otherEconomicFinancialRequirements,
              "workContractsPerformanceOfWorks":res.response.espd.workContractsPerformanceOfWorks == null ? {"exists":false} : res.response.espd.workContractsPerformanceOfWorks,
              "supplyContractsPerformanceDeliveries":res.response.espd.supplyContractsPerformanceDeliveries == null ? {"exists":false} : res.response.espd.supplyContractsPerformanceDeliveries,
              "serviceContractsPerformanceServices":res.response.espd.serviceContractsPerformanceServices == null ? {"exists":false} : res.response.espd.serviceContractsPerformanceServices,
              "techniciansTechnicalBodies":res.response.espd.techniciansTechnicalBodies == null ? {"exists":false} : res.response.espd.techniciansTechnicalBodies,
              "workContractsTechnicians":res.response.espd.workContractsTechnicians == null ? {"exists":false} : res.response.espd.workContractsTechnicians,
              "technicalFacilitiesMeasures":res.response.espd.technicalFacilitiesMeasures == null ? {"exists":false} : res.response.espd.technicalFacilitiesMeasures,
              "studyResearchFacilities":res.response.espd.studyResearchFacilities == null ? {"exists":false} : res.response.espd.studyResearchFacilities,
              "supplyChainManagement":res.response.espd.supplyChainManagement == null ? {"exists":false} : res.response.espd.supplyChainManagement,
              "environmentalManagementFeatures":res.response.espd.environmentalManagementFeatures == null ? {"exists":false} : res.response.espd.environmentalManagementFeatures,
              "toolsPlantTechnicalEquipment":res.response.espd.toolsPlantTechnicalEquipment == null ? {"exists":false} : res.response.espd.toolsPlantTechnicalEquipment,
              "educationalProfessionalQualifications":res.response.espd.educationalProfessionalQualifications == null ? {"exists":false} : res.response.espd.educationalProfessionalQualifications,
              "allowanceOfChecks":res.response.espd.allowanceOfChecks == null ? {"exists":false} : res.response.espd.allowanceOfChecks,
              "numberManagerialStaff":res.response.espd.numberManagerialStaff == null ? {"exists":false} : res.response.espd.numberManagerialStaff,
              "averageAnnualManpower":res.response.espd.averageAnnualManpower == null ? {"exists":false} : res.response.espd.averageAnnualManpower,
              "subcontractingProportion":res.response.espd.subcontractingProportion == null ? {"exists":false} : res.response.espd.subcontractingProportion,
              "supplyContractsSamplesDescriptionsWithoutCa":res.response.espd.supplyContractsSamplesDescriptionsWithoutCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithoutCa,
              "supplyContractsSamplesDescriptionsWithCa":res.response.espd.supplyContractsSamplesDescriptionsWithCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithCa,
              "supplyContractsCertificatesQc":res.response.espd.supplyContractsCertificatesQc == null ? {"exists":false} : res.response.espd.supplyContractsCertificatesQc,
              "certificateIndependentBodiesAboutQa":res.response.espd.certificateIndependentBodiesAboutQa == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutQa,
              "certificateIndependentBodiesAboutEnvironmental":res.response.espd.certificateIndependentBodiesAboutEnvironmental == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutEnvironmental
            },
            "fine":{},
          };
          let storeObjectPresent=this.store.viewElement(AppCostants.STORE_OBJ);        
          if(!isEmpty(storeObjectPresent)){
            storeObj['procedura']={...storeObj.procedura,...storeObjectPresent.procedura}
          }else{
            storeObj['procedura']={...storeObj.procedura}
          }
          
          this.store.addElement(AppCostants.STORE_OBJ, storeObj);      
          this.store.addElement(AppCostants.IMPORTED_XML, {...storeObj.procedura,...storeObj.esclusione,...storeObj.selezione,...storeObj.fine});
          this.store.addElement(AppCostants.WHO_IMPORTED, 'SA');
          this.router.navigate([AppCostants.PAGE_PROCEDURA]);
        },
          err => {
            console.log(err)
            messages.push("Errore in fase di conversione espd request");
            each(messages, (mess: string) => {
              let singleMessage: SdkMessagePanelTranslate = {
                message: mess
              };
              messagesForPanel.push(singleMessage);
            });
            this.sdkMessagePanelService.clear(this.errorPanel);
            this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
          
          }
        );
      
     
    }else{      
      this.store.addElement(AppCostants.STORE_USR, this.userInfo);     
      this.store.addElement(AppCostants.FROM_XML, this.fromXML);
      this.router.navigate([AppCostants.PAGE_PROCEDURA]);
    }
  }

  public manageFileSelect(event: any): void {
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    let target: HTMLInputElement = event.target;

    if (isObject(target)) {
      let files: FileList = target.files;
      let file: File = files.item(0);

      if (file != null && files[0].type === 'text/xml') {
        let fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = this.handleReaderLoadedXml;

        /*
        let reader = new FileReader();
        reader.onload = this.handleReaderLoaded;
        reader.readAsBinaryString(file);
        */
        this.selectUser = true;
        this.sdkMessagePanelService.clear(this.errorPanel);
        this.fromXML = true;
      }
      else if (file != null && files[0].type !== 'text/xml') {
        this.selectUser = false;
        messages.push(AppCostants.ERROR_XML);
        each(messages, (mess: string) => {
          let singleMessage: SdkMessagePanelTranslate = {
            message: mess
          };
          messagesForPanel.push(singleMessage);
        });
        this.sdkMessagePanelService.clear(this.errorPanel);
        this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
        this.fromXML = false;
      } else {
        this.selectUser = false;
        this.sdkMessagePanelService.clear(this.errorPanel);
        this.fromXML = false;
      }
    } else {
      this.selectUser = false;
      this.fromXML = false;

    }
  }

  onItemImportCreate(value: any, whoIs?: string) {
    if (whoIs !== this.xmlSender && this.attachments !== undefined) {
      this.attachments.nativeElement.value = ""
    }
    if (value.target.value === AppCostants.EO_IMPORT ) {
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.EO_COMPILE,false);
    } else if(value.target.value === AppCostants.SA_IMPORT){
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.SA_COMPILE,false);
    }
    this.fromXML = false;
    this.xmlSender = whoIs;
  }





  public handleReaderLoadedXml = (ev: any) => {
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    const array = new Uint8Array(ev.target.result);

    let xmlBase64 = this.arrayBufferToBase64(array);

    const fileByteArray = [];
    for (let i = 0; i < array.length; i++) {
      fileByteArray.push(array[i]);
    }    
    if(this.xmlSender === 'SA') {

    
      this.selectUser=false;
      this.restService.putXmlRequest(xmlBase64, this.xmlSender).subscribe((res :any)=> {        
        let authority = res.response.espd.authority;    
        let storeObj={
          "id":res.response.espd.id,
          "uuid":res.response.espd.uuid,
          "issueDate":res.response.espd.issueDate,
          "issueTime":res.response.espd.issueTime,        
          "authority":authority,
          "owner": res.response.espd.owner,
          "procedura":{
            "codiceANAC":res.response.espd.codiceANAC,
            "procedureCode":res.response.espd.procedureCode,
            "authority":authority,
            "procedureTitle":res.response.espd.procedureTitle,
            "procedureShortDesc":res.response.espd.procedureShortDesc,
            "fileRefByCA":res.response.espd.fileRefByCA,
            "additionalDocumentReference":res.response.espd.additionalDocumentReference,
            "lots":res.response.espd.lots,
            "eoShelteredWorkshop":res.response.espd.eoShelteredWorkshop,
            "eoRegistered":res.response.espd.eoRegistered,
            "eoTogetherWithOthers":res.response.espd.eoTogetherWithOthers,
            "reliedEntities":res.response.espd.reliedEntities,
            "notReliedEntities":res.response.espd.notReliedEntities,
            "lotsEoTendersTo":res.response.espd.lotsEoTendersTo,
            "eoReductionOfCandidates":res.response.espd.eoReductionOfCandidates,
            "caLots":res.response.espd.caLots,
            "eoSme":res.response.espd.eoSme,
            "suppEvidence":res.response.espd.suppEvidence,
            "suppEvidenceOther":res.response.espd.suppEvidenceOther
          },
          "esclusione":{
            "criminalConvictions":res.response.espd.criminalConvictions,
              "corruption":res.response.espd.corruption,
              "fraud":res.response.espd.fraud,
              "terroristOffences":res.response.espd.terroristOffences,
              "moneyLaundering":res.response.espd.moneyLaundering,
              "childLabour":res.response.espd.childLabour,
              "paymentTaxes":res.response.espd.paymentTaxes,
              "paymentSocialSecurity":res.response.espd.paymentSocialSecurity,
              "breachingObligationsEnvironmental":res.response.espd.breachingObligationsEnvironmental == null ? {"exists":false} : res.response.espd.breachingObligationsEnvironmental,
              "breachingObligationsSocial":res.response.espd.breachingObligationsSocial == null ? {"exists":false} : res.response.espd.breachingObligationsSocial,
              "breachingObligationsLabour":res.response.espd.breachingObligationsLabour == null ? {"exists":false} : res.response.espd.breachingObligationsLabour,
              "bankruptcy":res.response.espd.bankruptcy == null ? {"exists":false} : res.response.espd.bankruptcy,
              "insolvency":res.response.espd.insolvency == null ? {"exists":false} : res.response.espd.insolvency,
              "arrangementWithCreditors":res.response.espd.arrangementWithCreditors == null ? {"exists":false} : res.response.espd.arrangementWithCreditors,
              "analogousSituation":res.response.espd.analogousSituation == null ? {"exists":false} : res.response.espd.analogousSituation,
              "assetsAdministeredByLiquidator":res.response.espd.assetsAdministeredByLiquidator == null ? {"exists":false} : res.response.espd.assetsAdministeredByLiquidator,
              "businessActivitiesSuspended":res.response.espd.businessActivitiesSuspended == null ? {"exists":false} : res.response.espd.businessActivitiesSuspended,
              "guiltyGrave":res.response.espd.guiltyGrave == null ? {"exists":false} : res.response.espd.guiltyGrave,
              "agreementsWithOtherEO":res.response.espd.agreementsWithOtherEO == null ? {"exists":false} : res.response.espd.agreementsWithOtherEO,
              "conflictInterest":res.response.espd.conflictInterest == null ? {"exists":false} : res.response.espd.conflictInterest,
              "involvementPreparationProcurement":res.response.espd.involvementPreparationProcurement == null ? {"exists":false} : res.response.espd.involvementPreparationProcurement,
              "earlyTermination":res.response.espd.earlyTermination == null ? {"exists":false} : res.response.espd.earlyTermination,
              "guiltyMisinterpretation":res.response.espd.guiltyMisinterpretation == null ? {"exists":false} : res.response.espd.guiltyMisinterpretation,
              "nationalExclusionGrounds":res.response.espd.nationalExclusionGrounds == null ? {"exists":false} : res.response.espd.nationalExclusionGrounds
          },
          "selezione": {
            "enrolmentProfessionalRegister":res.response.espd.enrolmentProfessionalRegister == null ? {"exists":false} : res.response.espd.enrolmentProfessionalRegister,
            "enrolmentTradeRegister":res.response.espd.enrolmentTradeRegister == null ? {"exists":false} : res.response.espd.enrolmentTradeRegister,
            "serviceContractsAuthorisation":res.response.espd.serviceContractsAuthorisation == null ? {"exists":false} : res.response.espd.serviceContractsAuthorisation,
            "serviceContractsMembership":res.response.espd.serviceContractsMembership == null ? {"exists":false} : res.response.espd.serviceContractsMembership,
            "generalYearlyTurnover":res.response.espd.generalYearlyTurnover == null ? {"exists":false} : res.response.espd.generalYearlyTurnover,
            "averageYearlyTurnover":res.response.espd.averageYearlyTurnover == null ? {"exists":false} : res.response.espd.averageYearlyTurnover,
            "specificYearlyTurnover":res.response.espd.specificYearlyTurnover == null ? {"exists":false} : res.response.espd.specificYearlyTurnover,
            "specificAverageTurnover":res.response.espd.specificAverageTurnover == null ? {"exists":false} : res.response.espd.specificAverageTurnover,
            "setupEconomicOperator":res.response.espd.setupEconomicOperator == null ? {"exists":false} : res.response.espd.setupEconomicOperator,
            "financialRatio":res.response.espd.financialRatio == null ? {"exists":false} : res.response.espd.financialRatio,
            "professionalRiskInsurance":res.response.espd.professionalRiskInsurance == null ? {"exists":false} : res.response.espd.professionalRiskInsurance,
            "otherEconomicFinancialRequirements":res.response.espd.otherEconomicFinancialRequirements == null ? {"exists":false} : res.response.espd.otherEconomicFinancialRequirements,
            "workContractsPerformanceOfWorks":res.response.espd.workContractsPerformanceOfWorks == null ? {"exists":false} : res.response.espd.workContractsPerformanceOfWorks,
            "supplyContractsPerformanceDeliveries":res.response.espd.supplyContractsPerformanceDeliveries == null ? {"exists":false} : res.response.espd.supplyContractsPerformanceDeliveries,
            "serviceContractsPerformanceServices":res.response.espd.serviceContractsPerformanceServices == null ? {"exists":false} : res.response.espd.serviceContractsPerformanceServices,
            "techniciansTechnicalBodies":res.response.espd.techniciansTechnicalBodies == null ? {"exists":false} : res.response.espd.techniciansTechnicalBodies,
            "workContractsTechnicians":res.response.espd.workContractsTechnicians == null ? {"exists":false} : res.response.espd.workContractsTechnicians,
            "technicalFacilitiesMeasures":res.response.espd.technicalFacilitiesMeasures == null ? {"exists":false} : res.response.espd.technicalFacilitiesMeasures,
            "studyResearchFacilities":res.response.espd.studyResearchFacilities == null ? {"exists":false} : res.response.espd.studyResearchFacilities,
            "supplyChainManagement":res.response.espd.supplyChainManagement == null ? {"exists":false} : res.response.espd.supplyChainManagement,
            "environmentalManagementFeatures":res.response.espd.environmentalManagementFeatures == null ? {"exists":false} : res.response.espd.environmentalManagementFeatures,
            "toolsPlantTechnicalEquipment":res.response.espd.toolsPlantTechnicalEquipment == null ? {"exists":false} : res.response.espd.toolsPlantTechnicalEquipment,
            "educationalProfessionalQualifications":res.response.espd.educationalProfessionalQualifications == null ? {"exists":false} : res.response.espd.educationalProfessionalQualifications,
            "allowanceOfChecks":res.response.espd.allowanceOfChecks == null ? {"exists":false} : res.response.espd.allowanceOfChecks,
            "numberManagerialStaff":res.response.espd.numberManagerialStaff == null ? {"exists":false} : res.response.espd.numberManagerialStaff,
            "averageAnnualManpower":res.response.espd.averageAnnualManpower == null ? {"exists":false} : res.response.espd.averageAnnualManpower,
            "subcontractingProportion":res.response.espd.subcontractingProportion == null ? {"exists":false} : res.response.espd.subcontractingProportion,
            "supplyContractsSamplesDescriptionsWithoutCa":res.response.espd.supplyContractsSamplesDescriptionsWithoutCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithoutCa,
            "supplyContractsSamplesDescriptionsWithCa":res.response.espd.supplyContractsSamplesDescriptionsWithCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithCa,
            "supplyContractsCertificatesQc":res.response.espd.supplyContractsCertificatesQc == null ? {"exists":false} : res.response.espd.supplyContractsCertificatesQc,
            "certificateIndependentBodiesAboutQa":res.response.espd.certificateIndependentBodiesAboutQa == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutQa,
            "certificateIndependentBodiesAboutEnvironmental":res.response.espd.certificateIndependentBodiesAboutEnvironmental == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutEnvironmental
          },
          "fine":{},
        };
       
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);     
        this.store.addElement(AppCostants.IMPORTED_XML, {...storeObj.procedura,...storeObj.esclusione,...storeObj.selezione,...storeObj.fine});
        this.store.addElement(AppCostants.WHO_IMPORTED, this.xmlSender);
        this.selectUser=true;
      },
        err => {
          console.log(err)
          messages.push("Errore in fase di import della request");
          each(messages, (mess: string) => {
            let singleMessage: SdkMessagePanelTranslate = {
              message: mess
            };
            messagesForPanel.push(singleMessage);
          });
          this.sdkMessagePanelService.clear(this.errorPanel);
          this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
        }
      );
    }

    if(this.xmlSender === 'OE') {
      

    
  
      this.selectUser=false;
      this.restService.putXmlResponse(xmlBase64, this.xmlSender).subscribe((res :any)=> {
        //console.log(res['response'].test)
        let lotId=[];
        if(res.response.espd.lotsEoTendersTo != null && res.response.espd.lotsEoTendersTo.lotId != null){
          res.response.espd.lotsEoTendersTo.lotId.forEach(element => {
            lotId.push({"lotId":element});
          });
        }
        let storeObj={
          "id":res.response.espd.id,
          "uuid":res.response.espd.uuid,
          "issueDate":res.response.espd.issueDate,
          "issueTime":res.response.espd.issueTime,        
          "authority":res.response.espd.authority,
          "procedura":{
            "codiceANAC":res.response.espd.codiceANAC,
            "procedureCode":res.response.espd.procedureCode,
            "authority":res.response.espd.authority,
            "procedureTitle":res.response.espd.procedureTitle,
            "procedureShortDesc":res.response.espd.procedureShortDesc,
            "fileRefByCA":res.response.espd.fileRefByCA,
            "additionalDocumentReference":res.response.espd.additionalDocumentReference,
            "lots":res.response.espd.lots,
            "economicOperator":res.response.espd.economicOperator,
            "eoShelteredWorkshop":res.response.espd.eoShelteredWorkshop,
            "eoRegistered":res.response.espd.eoRegistered,
            "eoTogetherWithOthers":res.response.espd.eoTogetherWithOthers,
            "reliedEntities":res.response.espd.reliedEntities,
            "notReliedEntities":res.response.espd.notReliedEntities,
            "lotsEoTendersTo":res.response.espd.lotsEoTendersTo,
            "eoReductionOfCandidates":res.response.espd.eoReductionOfCandidates,
            "caLots":res.response.espd.caLots,
            "eoSme":res.response.espd.eoSme,
            "suppEvidence":res.response.espd.suppEvidence,
            "suppEvidenceOther":res.response.espd.suppEvidenceOther
          },
          "esclusione":{
            "criminalConvictions":res.response.espd.criminalConvictions,
              "corruption":res.response.espd.corruption,
              "fraud":res.response.espd.fraud,
              "terroristOffences":res.response.espd.terroristOffences,
              "moneyLaundering":res.response.espd.moneyLaundering,
              "childLabour":res.response.espd.childLabour,
              "paymentTaxes":res.response.espd.paymentTaxes,
              "paymentSocialSecurity":res.response.espd.paymentSocialSecurity,
              "breachingObligationsEnvironmental":res.response.espd.breachingObligationsEnvironmental == null ? {"exists":false} : res.response.espd.breachingObligationsEnvironmental,
              "breachingObligationsSocial":res.response.espd.breachingObligationsSocial == null ? {"exists":false} : res.response.espd.breachingObligationsSocial,
              "breachingObligationsLabour":res.response.espd.breachingObligationsLabour == null ? {"exists":false} : res.response.espd.breachingObligationsLabour,
              "bankruptcy":res.response.espd.bankruptcy == null ? {"exists":false} : res.response.espd.bankruptcy,
              "insolvency":res.response.espd.insolvency == null ? {"exists":false} : res.response.espd.insolvency,
              "arrangementWithCreditors":res.response.espd.arrangementWithCreditors == null ? {"exists":false} : res.response.espd.arrangementWithCreditors,
              "analogousSituation":res.response.espd.analogousSituation == null ? {"exists":false} : res.response.espd.analogousSituation,
              "assetsAdministeredByLiquidator":res.response.espd.assetsAdministeredByLiquidator == null ? {"exists":false} : res.response.espd.assetsAdministeredByLiquidator,
              "businessActivitiesSuspended":res.response.espd.businessActivitiesSuspended == null ? {"exists":false} : res.response.espd.businessActivitiesSuspended,
              "guiltyGrave":res.response.espd.guiltyGrave == null ? {"exists":false} : res.response.espd.guiltyGrave,
              "agreementsWithOtherEO":res.response.espd.agreementsWithOtherEO == null ? {"exists":false} : res.response.espd.agreementsWithOtherEO,
              "conflictInterest":res.response.espd.conflictInterest == null ? {"exists":false} : res.response.espd.conflictInterest,
              "involvementPreparationProcurement":res.response.espd.involvementPreparationProcurement == null ? {"exists":false} : res.response.espd.involvementPreparationProcurement,
              "earlyTermination":res.response.espd.earlyTermination == null ? {"exists":false} : res.response.espd.earlyTermination,
              "guiltyMisinterpretation":res.response.espd.guiltyMisinterpretation == null ? {"exists":false} : res.response.espd.guiltyMisinterpretation,
              "nationalExclusionGrounds":res.response.espd.nationalExclusionGrounds == null ? {"exists":false} : res.response.espd.nationalExclusionGrounds
          },
          "selezione": {
            "enrolmentProfessionalRegister":res.response.espd.enrolmentProfessionalRegister == null ? {"exists":false} : res.response.espd.enrolmentProfessionalRegister,
            "enrolmentTradeRegister":res.response.espd.enrolmentTradeRegister == null ? {"exists":false} : res.response.espd.enrolmentTradeRegister,
            "serviceContractsAuthorisation":res.response.espd.serviceContractsAuthorisation == null ? {"exists":false} : res.response.espd.serviceContractsAuthorisation,
            "serviceContractsMembership":res.response.espd.serviceContractsMembership == null ? {"exists":false} : res.response.espd.serviceContractsMembership,
            "generalYearlyTurnover":res.response.espd.generalYearlyTurnover == null ? {"exists":false} : res.response.espd.generalYearlyTurnover,
            "averageYearlyTurnover":res.response.espd.averageYearlyTurnover == null ? {"exists":false} : res.response.espd.averageYearlyTurnover,
            "specificYearlyTurnover":res.response.espd.specificYearlyTurnover == null ? {"exists":false} : res.response.espd.specificYearlyTurnover,
            "specificAverageTurnover":res.response.espd.specificAverageTurnover == null ? {"exists":false} : res.response.espd.specificAverageTurnover,
            "setupEconomicOperator":res.response.espd.setupEconomicOperator == null ? {"exists":false} : res.response.espd.setupEconomicOperator,
            "financialRatio":res.response.espd.financialRatio == null ? {"exists":false} : res.response.espd.financialRatio,
            "professionalRiskInsurance":res.response.espd.professionalRiskInsurance == null ? {"exists":false} : res.response.espd.professionalRiskInsurance,
            "otherEconomicFinancialRequirements":res.response.espd.otherEconomicFinancialRequirements == null ? {"exists":false} : res.response.espd.otherEconomicFinancialRequirements,
            "workContractsPerformanceOfWorks":res.response.espd.workContractsPerformanceOfWorks == null ? {"exists":false} : res.response.espd.workContractsPerformanceOfWorks,
            "supplyContractsPerformanceDeliveries":res.response.espd.supplyContractsPerformanceDeliveries == null ? {"exists":false} : res.response.espd.supplyContractsPerformanceDeliveries,
            "serviceContractsPerformanceServices":res.response.espd.serviceContractsPerformanceServices == null ? {"exists":false} : res.response.espd.serviceContractsPerformanceServices,
            "techniciansTechnicalBodies":res.response.espd.techniciansTechnicalBodies == null ? {"exists":false} : res.response.espd.techniciansTechnicalBodies,
            "workContractsTechnicians":res.response.espd.workContractsTechnicians == null ? {"exists":false} : res.response.espd.workContractsTechnicians,
            "technicalFacilitiesMeasures":res.response.espd.technicalFacilitiesMeasures == null ? {"exists":false} : res.response.espd.technicalFacilitiesMeasures,
            "studyResearchFacilities":res.response.espd.studyResearchFacilities == null ? {"exists":false} : res.response.espd.studyResearchFacilities,
            "supplyChainManagement":res.response.espd.supplyChainManagement == null ? {"exists":false} : res.response.espd.supplyChainManagement,
            "environmentalManagementFeatures":res.response.espd.environmentalManagementFeatures == null ? {"exists":false} : res.response.espd.environmentalManagementFeatures,
            "toolsPlantTechnicalEquipment":res.response.espd.toolsPlantTechnicalEquipment == null ? {"exists":false} : res.response.espd.toolsPlantTechnicalEquipment,
            "educationalProfessionalQualifications":res.response.espd.educationalProfessionalQualifications == null ? {"exists":false} : res.response.espd.educationalProfessionalQualifications,
            "allowanceOfChecks":res.response.espd.allowanceOfChecks == null ? {"exists":false} : res.response.espd.allowanceOfChecks,
            "numberManagerialStaff":res.response.espd.numberManagerialStaff == null ? {"exists":false} : res.response.espd.numberManagerialStaff,
            "averageAnnualManpower":res.response.espd.averageAnnualManpower == null ? {"exists":false} : res.response.espd.averageAnnualManpower,
            "subcontractingProportion":res.response.espd.subcontractingProportion == null ? {"exists":false} : res.response.espd.subcontractingProportion,
            "supplyContractsSamplesDescriptionsWithoutCa":res.response.espd.supplyContractsSamplesDescriptionsWithoutCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithoutCa,
            "supplyContractsSamplesDescriptionsWithCa":res.response.espd.supplyContractsSamplesDescriptionsWithCa == null ? {"exists":false} : res.response.espd.supplyContractsSamplesDescriptionsWithCa,
            "supplyContractsCertificatesQc":res.response.espd.supplyContractsCertificatesQc == null ? {"exists":false} : res.response.espd.supplyContractsCertificatesQc,
            "certificateIndependentBodiesAboutQa":res.response.espd.certificateIndependentBodiesAboutQa == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutQa,
            "certificateIndependentBodiesAboutEnvironmental":res.response.espd.certificateIndependentBodiesAboutEnvironmental == null ? {"exists":false} : res.response.espd.certificateIndependentBodiesAboutEnvironmental
          },
          "fine":{
            "signature":res.response.espd.signature,
            "location":res.response.espd.location,
            "signatureDate":res.response.espd.signatureDate
          },
        };        
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);      
        this.store.addElement(AppCostants.IMPORTED_XML, {...storeObj.procedura,...storeObj.esclusione,...storeObj.selezione,...storeObj.fine});
        this.store.addElement(AppCostants.WHO_IMPORTED, this.xmlSender);
        this.selectUser=true;
      },
        err => {
          console.log(err)
          console.log(err.error.text)
          messages.push("Errore in fase di import della response");
          each(messages, (mess: string) => {
            let singleMessage: SdkMessagePanelTranslate = {
              message: mess
            };
            messagesForPanel.push(singleMessage);
          });
          this.sdkMessagePanelService.clear(this.errorPanel);
          this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
        }
      );
    }
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
}
