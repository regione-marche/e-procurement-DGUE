import { Component, Injectable, OnInit, ElementRef, ViewChild } from '@angular/core';
import { Router, ActivatedRoute, ParamMap, Params } from '@angular/router';
import { AppCostants } from '../../../utils/dgue-constants';
import { CookieService } from 'ngx-cookie-service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { StoreModel } from '../../../model/store-model';
import { isEmpty, isObject, each, set } from 'lodash-es';
import { SdkMessagePanelService, SdkMessagePanelTranslate } from '@maggioli/sdk-controls';
import { LocaleService } from '../../../services/locale.service';
import { Language } from '../../../model/app-settings.models';


@Component({
  selector: 'app-dgue-home-appalti',
  templateUrl: './dgue-home-appalti.component.html'
})
@Injectable()
export class DGUEHomeAppaltiComponent   implements OnInit {
  
  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('warningspanel') _warningsPanel: ElementRef;
  private cookieData:any;
  public fromPortale: boolean=true;
  public isToken: boolean=false;
  public operation:string;
  public showOptionImportCreate: boolean = false;
  fromXML: boolean = false;
  xmlSender: string = '';
  selectUser: boolean = false;
  userInfo: string = AppCostants.SA;
  public dgueRequest: string = null;
  public isHome: boolean=false;
  private browserName: string;


  @ViewChild('attachments') attachments: ElementRef;

  storeObject: StoreModel = {
    "owner":"MAGGIOLI",
    procedura: {},
    esclusione: {},
    selezione: {},
    fine: {}
  }
  
    
  constructor(private router:Router,
              private cookieService:CookieService,
              private restService: RestService,
              public store: StoreService,
              private activatedRoute: ActivatedRoute,
              public sdkMessagePanelService: SdkMessagePanelService,
              private localeService: LocaleService) { 

          this.store.clearStore();                   
  }

  

  ngOnInit() {
    this.store.addElement(AppCostants.STORE_LANG,'it');
    let language={"label": "Italiano",
    "code": "it-IT",
    "alternativeCode": "it",
    "currency": "EUR",                
    "visualDateFormat": "dd/MM/yyyy"}
    this.localeService.useLanguage(language);
    this.store.addElement(AppCostants.FROM_APPALTI,this.fromPortale);
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
          this.isHome = false;
          this.store.addElement(AppCostants.INFO_APPALTI,res.data);
          this.store.addElement(AppCostants.FROM_PORTALE,this.fromPortale);
          
          this.dgueRequest = res.data.dgueRequest;                             
          let lots=[];
          let cpvs = []

          if(isEmpty(this.dgueRequest)){          
          
            if(!isEmpty(res.data.procedura.cpv)){
              res.data.procedura.cpv.forEach(element => {                           
                cpvs.push({"cpv": element});
              });
            }
            
            if(!isEmpty(res.data.procedura.lotti)){    
              let lotti: Array<any>= res.data.procedura.lotti;
              if(lotti.length == 1){
                let lotcigJSON = {        
                  "numLot": "1"
                }  
                lots.push(lotcigJSON);
              }else{
                lotti.forEach(element => {      
                  let lotcigJSON = {        
                    "numLot": element.numLotto
                  }                          
                  lots.push(lotcigJSON);
                });
              }        
             
            }
            let nomeRup = '';
            let emailRup = '';
            let faxRup = '';
            let telRup = '';
            if(res.data.procedura.rup != undefined){
              nomeRup = res.data.procedura.rup.nome;
              emailRup = res.data.procedura.rup.email
              faxRup = res.data.procedura.rup.fax;
              telRup = res.data.procedura.rup.telefono;
            }
            let additionalDocumentReference = res.data.procedura.pubblicazioni;
            if(additionalDocumentReference != null){
              for(let i = 0; i< additionalDocumentReference.length; i++){
                if(!isEmpty(additionalDocumentReference[i])){
                  set(additionalDocumentReference[i],'docTypeCode',AppCostants.DOCUMENT_TYPE_MAP[additionalDocumentReference[i].tipoPubblicazione.id]);              
                }
              }
            }
            let mapInfo = {
              "codiceANAC":res.data.procedura.codiceANAC,
              "authority.name":res.data.sa.ragioneSociale,             
              "authority.country":res.data.sa.nazione,                       
              "authority.vatNumber":res.data.sa.partitaIva,
              "authority.website":res.data.sa.sitoWeb,
              "authority.street":res.data.sa.via,
              "authority.city":res.data.sa.citta,
              "authority.postalCode":res.data.sa.cap,
              "authority.profileURI":res.data.sa.indrizzoProfiloCommittente,              
              "authority.email":res.data.sa.email,             
              "authority.contactName":nomeRup,
              "authority.contactEmail":emailRup,        
              "authority.contactFax":faxRup,
              "authority.contactPhone":telRup,       
              "authority.serviceProvider.vatNumber":AppCostants.MAG_VAT_NUMBER,
              "authority.serviceProvider.website":res.data.serviceProviderURL,              
              "authority.serviceProvider.name":AppCostants.MAG_NAME,
              "authority.serviceProvider.country":AppCostants.MAG_COUNTRY,
              "procedureCode":AppCostants.PROCEDURE_TYPE_MAP[res.data.procedura.tipoProcedura],
              "projectType":AppCostants.PROJECT_TYPE_MAP[res.data.procedura.oggetto],
              "procedureTitle":res.data.procedura.titolo,
              "procedureShortDesc":res.data.procedura.descrizione,
              "cpvs":cpvs,
              "lots":lots,
              "numberLot":lots.length,
              "caLots.maxLot":res.data.procedura.maxLottiAggiudicabili,
              "caLots.maxLotTender":res.data.procedura.maxLottiOfferta,
              "caLots.lotSubmit":AppCostants.BID_TYPE_MAP[res.data.procedura.codiceOffertePresentate],
              "fileRefByCA":res.data.procedura.codiceGara,
              "additionalDocumentReference":additionalDocumentReference
            }
            this.storeObject['procedura'] = mapInfo;
            this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);
          
          }
          this.isHome = true;
          
        },err => {          
          setTimeout(()=>{                                
            this.isHome = true;
          }, 1000);
          console.log(err)
          messages.push("Errore in fase di redirect da appalti");
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
    if(!isEmpty(this.dgueRequest)){
      this.store.addElement(AppCostants.FROM_XML, false);
      this.store.addElement(AppCostants.STORE_USR, AppCostants.SA);
                          
        this.restService.putXmlRequest(this.dgueRequest, 'SA').subscribe((res :any)=> {
          //console.log(res['response'].test)

          let cpvs = [];
          if(res.response.espd.cpvs != null){
            res.response.espd.cpvs.forEach(element => {
              if(element != null){
                let cpv = {"cpv" : element};
                cpvs.push(cpv);
              }            
            });
          }

          let authority = res.response.espd.authority;                   
          let storeObj={
            "id":res.response.espd.id,
            "uuid":res.response.espd.uuid,
            "issueDate":res.response.espd.issueDate,
            "issueTime":res.response.espd.issueTime,        
            "authority":authority,
            "owner": res.response.espd.owner,
            "procedura":{              
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
              "projectType":res.response.espd.projectType,
              "cpvs":cpvs
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
            setTimeout(()=>{                                
              this.isHome = true;
            }, 1000);
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

  help(){
    this.router.navigate([AppCostants.PAGE_ASSISTENZA]);
  }

  

}
