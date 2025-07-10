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
  selector: 'app-quadro-generale-appalti',
  templateUrl: './quadro-generale-appalti.component.html'
})
@Injectable()
export class QuadroGeneraleAppaltiComponent implements OnInit {

  public isConverted = false;
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
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    let cookieTokenString: string = this.cookieService.get(AppCostants.SSO_COOKIE_KEY);
    this.cookieService.deleteAll(AppCostants.SSO_COOKIE_KEY);
    //console.log("cookieTokenString: "+cookieTokenString);
    if (cookieTokenString === null || cookieTokenString === '') {
      //TODO se non trovo il cookie string effettivamente dal cookie lo vado a prendere dai parametri
      this.activatedRoute.queryParams
        .subscribe((el: Params) => {
          console.log(el);
          cookieTokenString = el.t;
          console.log(cookieTokenString);
        }
        );
    }
    //console.log("cookieTokenString in q: "+cookieTokenString);
    if (cookieTokenString != null && cookieTokenString != '') {
      //console.log(this.parseJwt(cookieTokenString));
      this.cookieData = this.parseJwt(cookieTokenString);
      this.restService.getInfoTest(this.cookieData.data.urlServizio, cookieTokenString).subscribe((res: any) => {


        this.dgueRequest = res.data.dgueRequest;
        this.dgueResponse = res.data.dgueResponse;


        if (isEmpty(this.dgueRequest)) {
          //ALLORA C'è LA RESPONSE
          this.store.addElement(AppCostants.STORE_USR, AppCostants.EO);
          this.restService.putXmlResponse(this.dgueResponse, 'OE').subscribe((res: any) => {
            if(res?.response?.espd?.owner != 'MAGGIOLI'){
              let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
              
              let singleMessage : SdkMessagePanelTranslate = {
                message : "WARNINGS.UPLOAD_FILE_NOT_MAGGIOLI"
              }          
              messagesForPanel.push( singleMessage);          
              this.sdkMessagePanelService.clear(this.warningPanel);
              this.sdkMessagePanelService.showWarning(this.warningPanel,messagesForPanel);
            }
            let storeObj = {             
              "esclusione": {
                "criminalConvictions": res.response.espd.criminalConvictions,
                "corruption": res.response.espd.corruption,
                "fraud": res.response.espd.fraud,
                "terroristOffences": res.response.espd.terroristOffences,
                "moneyLaundering": res.response.espd.moneyLaundering,
                "childLabour": res.response.espd.childLabour,
                "paymentTaxes": res.response.espd.paymentTaxes,
                "paymentSocialSecurity": res.response.espd.paymentSocialSecurity,
                "breachingObligationsEnvironmental": res.response.espd.breachingObligationsEnvironmental == null ? { "exists": false } : res.response.espd.breachingObligationsEnvironmental,
                "breachingObligationsSocial": res.response.espd.breachingObligationsSocial == null ? { "exists": false } : res.response.espd.breachingObligationsSocial,
                "breachingObligationsLabour": res.response.espd.breachingObligationsLabour == null ? { "exists": false } : res.response.espd.breachingObligationsLabour,
                "bankruptcy": res.response.espd.bankruptcy == null ? { "exists": false } : res.response.espd.bankruptcy,
                "insolvency": res.response.espd.insolvency == null ? { "exists": false } : res.response.espd.insolvency,
                "arrangementWithCreditors": res.response.espd.arrangementWithCreditors == null ? { "exists": false } : res.response.espd.arrangementWithCreditors,
                "analogousSituation": res.response.espd.analogousSituation == null ? { "exists": false } : res.response.espd.analogousSituation,
                "assetsAdministeredByLiquidator": res.response.espd.assetsAdministeredByLiquidator == null ? { "exists": false } : res.response.espd.assetsAdministeredByLiquidator,
                "businessActivitiesSuspended": res.response.espd.businessActivitiesSuspended == null ? { "exists": false } : res.response.espd.businessActivitiesSuspended,
                "guiltyGrave": res.response.espd.guiltyGrave == null ? { "exists": false } : res.response.espd.guiltyGrave,
                "agreementsWithOtherEO": res.response.espd.agreementsWithOtherEO == null ? { "exists": false } : res.response.espd.agreementsWithOtherEO,
                "conflictInterest": res.response.espd.conflictInterest == null ? { "exists": false } : res.response.espd.conflictInterest,
                "involvementPreparationProcurement": res.response.espd.involvementPreparationProcurement == null ? { "exists": false } : res.response.espd.involvementPreparationProcurement,
                "earlyTermination": res.response.espd.earlyTermination == null ? { "exists": false } : res.response.espd.earlyTermination,
                "guiltyMisinterpretation": res.response.espd.guiltyMisinterpretation == null ? { "exists": false } : res.response.espd.guiltyMisinterpretation,
                "nationalExclusionGrounds": res.response.espd.nationalExclusionGrounds == null ? { "exists": false } : res.response.espd.nationalExclusionGrounds
              },
              "selezione": {
                "enrolmentProfessionalRegister": res.response.espd.enrolmentProfessionalRegister == null ? { "exists": false } : res.response.espd.enrolmentProfessionalRegister,
                "enrolmentTradeRegister": res.response.espd.enrolmentTradeRegister == null ? { "exists": false } : res.response.espd.enrolmentTradeRegister,
                "serviceContractsAuthorisation": res.response.espd.serviceContractsAuthorisation == null ? { "exists": false } : res.response.espd.serviceContractsAuthorisation,
                "serviceContractsMembership": res.response.espd.serviceContractsMembership == null ? { "exists": false } : res.response.espd.serviceContractsMembership,
                "generalYearlyTurnover": res.response.espd.generalYearlyTurnover == null ? { "exists": false } : res.response.espd.generalYearlyTurnover,
                "averageYearlyTurnover": res.response.espd.averageYearlyTurnover == null ? { "exists": false } : res.response.espd.averageYearlyTurnover,
                "specificYearlyTurnover": res.response.espd.specificYearlyTurnover == null ? { "exists": false } : res.response.espd.specificYearlyTurnover,
                "specificAverageTurnover": res.response.espd.specificAverageTurnover == null ? { "exists": false } : res.response.espd.specificAverageTurnover,
                "setupEconomicOperator": res.response.espd.setupEconomicOperator == null ? { "exists": false } : res.response.espd.setupEconomicOperator,
                "financialRatio": res.response.espd.financialRatio == null ? { "exists": false } : res.response.espd.financialRatio,
                "professionalRiskInsurance": res.response.espd.professionalRiskInsurance == null ? { "exists": false } : res.response.espd.professionalRiskInsurance,
                "otherEconomicFinancialRequirements": res.response.espd.otherEconomicFinancialRequirements == null ? { "exists": false } : res.response.espd.otherEconomicFinancialRequirements,
                "workContractsPerformanceOfWorks": res.response.espd.workContractsPerformanceOfWorks == null ? { "exists": false } : res.response.espd.workContractsPerformanceOfWorks,
                "supplyContractsPerformanceDeliveries": res.response.espd.supplyContractsPerformanceDeliveries == null ? { "exists": false } : res.response.espd.supplyContractsPerformanceDeliveries,
                "serviceContractsPerformanceServices": res.response.espd.serviceContractsPerformanceServices == null ? { "exists": false } : res.response.espd.serviceContractsPerformanceServices,
                "techniciansTechnicalBodies": res.response.espd.techniciansTechnicalBodies == null ? { "exists": false } : res.response.espd.techniciansTechnicalBodies,
                "workContractsTechnicians": res.response.espd.workContractsTechnicians == null ? { "exists": false } : res.response.espd.workContractsTechnicians,
                "technicalFacilitiesMeasures": res.response.espd.technicalFacilitiesMeasures == null ? { "exists": false } : res.response.espd.technicalFacilitiesMeasures,
                "studyResearchFacilities": res.response.espd.studyResearchFacilities == null ? { "exists": false } : res.response.espd.studyResearchFacilities,
                "supplyChainManagement": res.response.espd.supplyChainManagement == null ? { "exists": false } : res.response.espd.supplyChainManagement,
                "environmentalManagementFeatures": res.response.espd.environmentalManagementFeatures == null ? { "exists": false } : res.response.espd.environmentalManagementFeatures,
                "toolsPlantTechnicalEquipment": res.response.espd.toolsPlantTechnicalEquipment == null ? { "exists": false } : res.response.espd.toolsPlantTechnicalEquipment,
                "educationalProfessionalQualifications": res.response.espd.educationalProfessionalQualifications == null ? { "exists": false } : res.response.espd.educationalProfessionalQualifications,
                "allowanceOfChecks": res.response.espd.allowanceOfChecks == null ? { "exists": false } : res.response.espd.allowanceOfChecks,
                "numberManagerialStaff": res.response.espd.numberManagerialStaff == null ? { "exists": false } : res.response.espd.numberManagerialStaff,
                "averageAnnualManpower": res.response.espd.averageAnnualManpower == null ? { "exists": false } : res.response.espd.averageAnnualManpower,
                "subcontractingProportion": res.response.espd.subcontractingProportion == null ? { "exists": false } : res.response.espd.subcontractingProportion,
                "supplyContractsSamplesDescriptionsWithoutCa": res.response.espd.supplyContractsSamplesDescriptionsWithoutCa == null ? { "exists": false } : res.response.espd.supplyContractsSamplesDescriptionsWithoutCa,
                "supplyContractsSamplesDescriptionsWithCa": res.response.espd.supplyContractsSamplesDescriptionsWithCa == null ? { "exists": false } : res.response.espd.supplyContractsSamplesDescriptionsWithCa,
                "supplyContractsCertificatesQc": res.response.espd.supplyContractsCertificatesQc == null ? { "exists": false } : res.response.espd.supplyContractsCertificatesQc,
                "certificateIndependentBodiesAboutQa": res.response.espd.certificateIndependentBodiesAboutQa == null ? { "exists": false } : res.response.espd.certificateIndependentBodiesAboutQa,
                "certificateIndependentBodiesAboutEnvironmental": res.response.espd.certificateIndependentBodiesAboutEnvironmental == null ? { "exists": false } : res.response.espd.certificateIndependentBodiesAboutEnvironmental
              },
              "fine": {},
            };
            this.xml = res.response.espd;
            this.xml.criminalConvictions = this.xml.criminalConvictions == null ? { "exists": false } : this.xml.criminalConvictions;
            this.xml.corruption = this.xml.corruption == null ? { "exists": false } : this.xml.corruption;
            this.xml.fraud = this.xml.fraud == null ? { "exists": false } : this.xml.fraud;
            this.xml.terroristOffences = this.xml.terroristOffences == null ? { "exists": false } : this.xml.terroristOffences;
            this.xml.moneyLaundering = this.xml.moneyLaundering == null ? { "exists": false } : this.xml.moneyLaundering;
            this.xml.childLabour = this.xml.childLabour == null ? { "exists": false } : this.xml.childLabour;
            this.xml.paymentTaxes = this.xml.paymentTaxes == null ? { "exists": false } : this.xml.paymentTaxes;
            this.xml.paymentSocialSecurity = this.xml.paymentSocialSecurity == null ? { "exists": false } : this.xml.paymentSocialSecurity;
            this.xml.breachingObligationsEnvironmental = this.xml.breachingObligationsEnvironmental == null ? { "exists": false } : this.xml.breachingObligationsEnvironmental;
            this.xml.breachingObligationsSocial = this.xml.breachingObligationsSocial == null ? { "exists": false } : this.xml.breachingObligationsSocial;
            this.xml.breachingObligationsLabour = this.xml.breachingObligationsLabour == null ? { "exists": false } : this.xml.breachingObligationsLabour;
            this.xml.bankruptcy = this.xml.bankruptcy == null ? { "exists": false } : this.xml.bankruptcy;
            this.xml.insolvency = this.xml.insolvency == null ? { "exists": false } : this.xml.insolvency;
            this.xml.arrangementWithCreditors = this.xml.arrangementWithCreditors == null ? { "exists": false } : this.xml.arrangementWithCreditors;
            this.xml.analogousSituation = this.xml.analogousSituation == null ? { "exists": false } : this.xml.analogousSituation;
            this.xml.assetsAdministeredByLiquidator = this.xml.assetsAdministeredByLiquidator == null ? { "exists": false } : this.xml.assetsAdministeredByLiquidator;
            this.xml.businessActivitiesSuspended = this.xml.businessActivitiesSuspended == null ? { "exists": false } : this.xml.businessActivitiesSuspended;
            this.xml.guiltyGrave = this.xml.guiltyGrave == null ? { "exists": false } : this.xml.guiltyGrave;
            this.xml.agreementsWithOtherEO = this.xml.agreementsWithOtherEO == null ? { "exists": false } : this.xml.agreementsWithOtherEO;
            this.xml.conflictInterest = this.xml.conflictInterest == null ? { "exists": false } : this.xml.conflictInterest;
            this.xml.involvementPreparationProcurement = this.xml.involvementPreparationProcurement == null ? { "exists": false } : this.xml.involvementPreparationProcurement;
            this.xml.earlyTermination = this.xml.earlyTermination == null ? { "exists": false } : this.xml.earlyTermination;
            this.xml.guiltyMisinterpretation = this.xml.guiltyMisinterpretation == null ? { "exists": false } : this.xml.guiltyMisinterpretation;
            this.xml.nationalExclusionGrounds = this.xml.nationalExclusionGrounds == null ? { "exists": false } : this.xml.nationalExclusionGrounds;
            this.xml.enrolmentProfessionalRegister = this.xml.enrolmentProfessionalRegister == null ? { "exists": false } : this.xml.enrolmentProfessionalRegister;
            this.xml.enrolmentTradeRegister = this.xml.enrolmentTradeRegister == null ? { "exists": false } : this.xml.enrolmentTradeRegister;
            this.xml.serviceContractsAuthorisation = this.xml.serviceContractsAuthorisation == null ? { "exists": false } : this.xml.serviceContractsAuthorisation;
            this.xml.serviceContractsMembership = this.xml.serviceContractsMembership == null ? { "exists": false } : this.xml.serviceContractsMembership;
            this.xml.generalYearlyTurnover = this.xml.generalYearlyTurnover == null ? { "exists": false } : this.xml.generalYearlyTurnover;
            this.xml.averageYearlyTurnover = this.xml.averageYearlyTurnover == null ? { "exists": false } : this.xml.averageYearlyTurnover;
            this.xml.specificYearlyTurnover = this.xml.specificYearlyTurnover == null ? { "exists": false } : this.xml.specificYearlyTurnover;
            this.xml.specificAverageTurnover = this.xml.specificAverageTurnover == null ? { "exists": false } : this.xml.specificAverageTurnover;
            this.xml.setupEconomicOperator = this.xml.setupEconomicOperator == null ? { "exists": false } : this.xml.setupEconomicOperator;
            this.xml.financialRatio = this.xml.financialRatio == null ? { "exists": false } : this.xml.financialRatio;
            this.xml.professionalRiskInsurance = this.xml.professionalRiskInsurance == null ? { "exists": false } : this.xml.professionalRiskInsurance;
            this.xml.otherEconomicFinancialRequirements = this.xml.otherEconomicFinancialRequirements == null ? { "exists": false } : this.xml.otherEconomicFinancialRequirements;
            this.xml.workContractsPerformanceOfWorks = this.xml.workContractsPerformanceOfWorks == null ? { "exists": false } : this.xml.workContractsPerformanceOfWorks;
            this.xml.supplyContractsPerformanceDeliveries = this.xml.supplyContractsPerformanceDeliveries == null ? { "exists": false } : this.xml.supplyContractsPerformanceDeliveries;
            this.xml.serviceContractsPerformanceServices = this.xml.serviceContractsPerformanceServices == null ? { "exists": false } : this.xml.serviceContractsPerformanceServices;
            this.xml.techniciansTechnicalBodies = this.xml.techniciansTechnicalBodies == null ? { "exists": false } : this.xml.techniciansTechnicalBodies;
            this.xml.workContractsTechnicians = this.xml.workContractsTechnicians == null ? { "exists": false } : this.xml.workContractsTechnicians;
            this.xml.technicalFacilitiesMeasures = this.xml.technicalFacilitiesMeasures == null ? { "exists": false } : this.xml.technicalFacilitiesMeasures;
            this.xml.studyResearchFacilities = this.xml.studyResearchFacilities == null ? { "exists": false } : this.xml.studyResearchFacilities;
            this.xml.supplyChainManagement = this.xml.supplyChainManagement == null ? { "exists": false } : this.xml.supplyChainManagement;
            this.xml.environmentalManagementFeatures = this.xml.environmentalManagementFeatures == null ? { "exists": false } : this.xml.environmentalManagementFeatures;
            this.xml.toolsPlantTechnicalEquipment = this.xml.toolsPlantTechnicalEquipment == null ? { "exists": false } : this.xml.toolsPlantTechnicalEquipment;
            this.xml.educationalProfessionalQualifications = this.xml.educationalProfessionalQualifications == null ? { "exists": false } : this.xml.educationalProfessionalQualifications;
            this.xml.allowanceOfChecks = this.xml.allowanceOfChecks == null ? { "exists": false } : this.xml.allowanceOfChecks;
            this.xml.numberManagerialStaff = this.xml.numberManagerialStaff == null ? { "exists": false } : this.xml.numberManagerialStaff;
            this.xml.averageAnnualManpower = this.xml.averageAnnualManpower == null ? { "exists": false } : this.xml.averageAnnualManpower;
            this.xml.subcontractingProportion = this.xml.subcontractingProportion == null ? { "exists": false } : this.xml.subcontractingProportion;
            this.xml.supplyContractsSamplesDescriptionsWithoutCa = this.xml.supplyContractsSamplesDescriptionsWithoutCa == null ? { "exists": false } : this.xml.supplyContractsSamplesDescriptionsWithoutCa;
            this.xml.supplyContractsSamplesDescriptionsWithCa = this.xml.supplyContractsSamplesDescriptionsWithCa == null ? { "exists": false } : this.xml.supplyContractsSamplesDescriptionsWithCa;
            this.xml.supplyContractsCertificatesQc = this.xml.supplyContractsCertificatesQc == null ? { "exists": false } : this.xml.supplyContractsCertificatesQc;
            this.xml.certificateIndependentBodiesAboutQa = this.xml.certificateIndependentBodiesAboutQa == null ? { "exists": false } : this.xml.certificateIndependentBodiesAboutQa;
            this.xml.certificateIndependentBodiesAboutEnvironmental = this.xml.certificateIndependentBodiesAboutEnvironmental == null ? { "exists": false } : this.xml.certificateIndependentBodiesAboutEnvironmental;
            this.xml.eoShelteredWorkshop = this.xml.eoShelteredWorkshop == null ? { "exists": false } : this.xml.eoShelteredWorkshop;
            this.xml.eoRegistered = this.xml.eoRegistered == null ? { "exists": false } : this.xml.eoRegistered;
            this.xml.eoTogetherWithOthers = this.xml.eoTogetherWithOthers == null ? { "exists": false } : this.xml.eoTogetherWithOthers;
            this.xml.reliedEntities = this.xml.reliedEntities == null ? { "exists": false } : this.xml.reliedEntities;
            this.xml.notReliedEntities = this.xml.notReliedEntities == null ? { "exists": false } : this.xml.notReliedEntities;
            this.xml.lotsEoTendersTo = this.xml.lotsEoTendersTo == null ? { "exists": false } : this.xml.lotsEoTendersTo;
            this.xml.eoReductionOfCandidates = this.xml.eoReductionOfCandidates == null ? { "exists": false } : this.xml.eoReductionOfCandidates;
            this.xml.caLots = this.xml.caLots == null ? { "exists": false } : this.xml.caLots;
            this.xml.eoSme = this.xml.eoSme == null ? { "exists": false } : this.xml.eoSme;
            this.xml.suppEvidence = this.xml.suppEvidence == null ? { "exists": false } : this.xml.suppEvidence;
            this.xml.suppEvidenceOther = this.xml.suppEvidenceOther == null ? { "exists": false } : this.xml.suppEvidenceOther;

            this.store.addElement(AppCostants.STORE_OBJ, storeObj);
            this.store.addElement(AppCostants.IMPORTED_XML, this.xml);
            this.store.addElement(AppCostants.WHO_IMPORTED, 'OE');

            this.populateForm(AppCostants.EO, this.xml);
            this.checkExclusionExists();            
            this.isConverted = true;
          },
            err => {
              this.isConverted = true;
              console.log(err)
              messages.push("Errore in fase di import della response");
              each(messages, (mess: string) => {
                let singleMessage: SdkMessagePanelTranslate = {
                  message: mess
                };
                messagesForPanel.push(singleMessage);
              });
              this.sdkMessagePanelService.clear(this.errorPanel);
              this.sdkMessagePanelService.showError(this.errorPanel, messagesForPanel);
            });
        } else {

          this.store.addElement(AppCostants.STORE_USR, AppCostants.SA);

          this.restService.putXmlRequest(this.dgueRequest, 'SA').subscribe((res: any) => {
            if(res?.response?.espd?.owner != 'MAGGIOLI'){
              let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
              
              let singleMessage : SdkMessagePanelTranslate = {
                message : "WARNINGS.UPLOAD_FILE_NOT_MAGGIOLI"
              }          
              messagesForPanel.push( singleMessage);          
              this.sdkMessagePanelService.clear(this.warningPanel);
              this.sdkMessagePanelService.showWarning(this.warningPanel,messagesForPanel);
            }
            let cpvs = [];
            if (res.response.espd.cpvs != null) {
              res.response.espd.cpvs.forEach(element => {
                let cpv = { "cpv": element }
                cpvs.push(cpv);
              });

            }
            
            this.xml = res.response.espd;
            this.xml.cpvs = cpvs;
            this.xml.criminalConvictions = this.xml.criminalConvictions == null ? { "exists": false } : this.xml.criminalConvictions;
            this.xml.corruption = this.xml.corruption == null ? { "exists": false } : this.xml.corruption;
            this.xml.fraud = this.xml.fraud == null ? { "exists": false } : this.xml.fraud;
            this.xml.terroristOffences = this.xml.terroristOffences == null ? { "exists": false } : this.xml.terroristOffences;
            this.xml.moneyLaundering = this.xml.moneyLaundering == null ? { "exists": false } : this.xml.moneyLaundering;
            this.xml.childLabour = this.xml.childLabour == null ? { "exists": false } : this.xml.childLabour;
            this.xml.paymentTaxes = this.xml.paymentTaxes == null ? { "exists": false } : this.xml.paymentTaxes;
            this.xml.paymentSocialSecurity = this.xml.paymentSocialSecurity == null ? { "exists": false } : this.xml.paymentSocialSecurity;
            this.xml.breachingObligationsEnvironmental = this.xml.breachingObligationsEnvironmental == null ? { "exists": false } : this.xml.breachingObligationsEnvironmental;
            this.xml.breachingObligationsSocial = this.xml.breachingObligationsSocial == null ? { "exists": false } : this.xml.breachingObligationsSocial;
            this.xml.breachingObligationsLabour = this.xml.breachingObligationsLabour == null ? { "exists": false } : this.xml.breachingObligationsLabour;
            this.xml.bankruptcy = this.xml.bankruptcy == null ? { "exists": false } : this.xml.bankruptcy;
            this.xml.insolvency = this.xml.insolvency == null ? { "exists": false } : this.xml.insolvency;
            this.xml.arrangementWithCreditors = this.xml.arrangementWithCreditors == null ? { "exists": false } : this.xml.arrangementWithCreditors;
            this.xml.analogousSituation = this.xml.analogousSituation == null ? { "exists": false } : this.xml.analogousSituation;
            this.xml.assetsAdministeredByLiquidator = this.xml.assetsAdministeredByLiquidator == null ? { "exists": false } : this.xml.assetsAdministeredByLiquidator;
            this.xml.businessActivitiesSuspended = this.xml.businessActivitiesSuspended == null ? { "exists": false } : this.xml.businessActivitiesSuspended;
            this.xml.guiltyGrave = this.xml.guiltyGrave == null ? { "exists": false } : this.xml.guiltyGrave;
            this.xml.agreementsWithOtherEO = this.xml.agreementsWithOtherEO == null ? { "exists": false } : this.xml.agreementsWithOtherEO;
            this.xml.conflictInterest = this.xml.conflictInterest == null ? { "exists": false } : this.xml.conflictInterest;
            this.xml.involvementPreparationProcurement = this.xml.involvementPreparationProcurement == null ? { "exists": false } : this.xml.involvementPreparationProcurement;
            this.xml.earlyTermination = this.xml.earlyTermination == null ? { "exists": false } : this.xml.earlyTermination;
            this.xml.guiltyMisinterpretation = this.xml.guiltyMisinterpretation == null ? { "exists": false } : this.xml.guiltyMisinterpretation;
            this.xml.nationalExclusionGrounds = this.xml.nationalExclusionGrounds == null ? { "exists": false } : this.xml.nationalExclusionGrounds;
            this.xml.enrolmentProfessionalRegister = this.xml.enrolmentProfessionalRegister == null ? { "exists": false } : this.xml.enrolmentProfessionalRegister;
            this.xml.enrolmentTradeRegister = this.xml.enrolmentTradeRegister == null ? { "exists": false } : this.xml.enrolmentTradeRegister;
            this.xml.serviceContractsAuthorisation = this.xml.serviceContractsAuthorisation == null ? { "exists": false } : this.xml.serviceContractsAuthorisation;
            this.xml.serviceContractsMembership = this.xml.serviceContractsMembership == null ? { "exists": false } : this.xml.serviceContractsMembership;
            this.xml.generalYearlyTurnover = this.xml.generalYearlyTurnover == null ? { "exists": false } : this.xml.generalYearlyTurnover;
            this.xml.averageYearlyTurnover = this.xml.averageYearlyTurnover == null ? { "exists": false } : this.xml.averageYearlyTurnover;
            this.xml.specificYearlyTurnover = this.xml.specificYearlyTurnover == null ? { "exists": false } : this.xml.specificYearlyTurnover;
            this.xml.specificAverageTurnover = this.xml.specificAverageTurnover == null ? { "exists": false } : this.xml.specificAverageTurnover;
            this.xml.setupEconomicOperator = this.xml.setupEconomicOperator == null ? { "exists": false } : this.xml.setupEconomicOperator;
            this.xml.financialRatio = this.xml.financialRatio == null ? { "exists": false } : this.xml.financialRatio;
            this.xml.professionalRiskInsurance = this.xml.professionalRiskInsurance == null ? { "exists": false } : this.xml.professionalRiskInsurance;
            this.xml.otherEconomicFinancialRequirements = this.xml.otherEconomicFinancialRequirements == null ? { "exists": false } : this.xml.otherEconomicFinancialRequirements;
            this.xml.workContractsPerformanceOfWorks = this.xml.workContractsPerformanceOfWorks == null ? { "exists": false } : this.xml.workContractsPerformanceOfWorks;
            this.xml.supplyContractsPerformanceDeliveries = this.xml.supplyContractsPerformanceDeliveries == null ? { "exists": false } : this.xml.supplyContractsPerformanceDeliveries;
            this.xml.serviceContractsPerformanceServices = this.xml.serviceContractsPerformanceServices == null ? { "exists": false } : this.xml.serviceContractsPerformanceServices;
            this.xml.techniciansTechnicalBodies = this.xml.techniciansTechnicalBodies == null ? { "exists": false } : this.xml.techniciansTechnicalBodies;
            this.xml.workContractsTechnicians = this.xml.workContractsTechnicians == null ? { "exists": false } : this.xml.workContractsTechnicians;
            this.xml.technicalFacilitiesMeasures = this.xml.technicalFacilitiesMeasures == null ? { "exists": false } : this.xml.technicalFacilitiesMeasures;
            this.xml.studyResearchFacilities = this.xml.studyResearchFacilities == null ? { "exists": false } : this.xml.studyResearchFacilities;
            this.xml.supplyChainManagement = this.xml.supplyChainManagement == null ? { "exists": false } : this.xml.supplyChainManagement;
            this.xml.environmentalManagementFeatures = this.xml.environmentalManagementFeatures == null ? { "exists": false } : this.xml.environmentalManagementFeatures;
            this.xml.toolsPlantTechnicalEquipment = this.xml.toolsPlantTechnicalEquipment == null ? { "exists": false } : this.xml.toolsPlantTechnicalEquipment;
            this.xml.educationalProfessionalQualifications = this.xml.educationalProfessionalQualifications == null ? { "exists": false } : this.xml.educationalProfessionalQualifications;
            this.xml.allowanceOfChecks = this.xml.allowanceOfChecks == null ? { "exists": false } : this.xml.allowanceOfChecks;
            this.xml.numberManagerialStaff = this.xml.numberManagerialStaff == null ? { "exists": false } : this.xml.numberManagerialStaff;
            this.xml.averageAnnualManpower = this.xml.averageAnnualManpower == null ? { "exists": false } : this.xml.averageAnnualManpower;
            this.xml.subcontractingProportion = this.xml.subcontractingProportion == null ? { "exists": false } : this.xml.subcontractingProportion;
            this.xml.supplyContractsSamplesDescriptionsWithoutCa = this.xml.supplyContractsSamplesDescriptionsWithoutCa == null ? { "exists": false } : this.xml.supplyContractsSamplesDescriptionsWithoutCa;
            this.xml.supplyContractsSamplesDescriptionsWithCa = this.xml.supplyContractsSamplesDescriptionsWithCa == null ? { "exists": false } : this.xml.supplyContractsSamplesDescriptionsWithCa;
            this.xml.supplyContractsCertificatesQc = this.xml.supplyContractsCertificatesQc == null ? { "exists": false } : this.xml.supplyContractsCertificatesQc;
            this.xml.certificateIndependentBodiesAboutQa = this.xml.certificateIndependentBodiesAboutQa == null ? { "exists": false } : this.xml.certificateIndependentBodiesAboutQa;
            this.xml.certificateIndependentBodiesAboutEnvironmental = this.xml.certificateIndependentBodiesAboutEnvironmental == null ? { "exists": false } : this.xml.certificateIndependentBodiesAboutEnvironmental;
            this.xml.eoShelteredWorkshop = this.xml.eoShelteredWorkshop == null ? { "exists": false } : this.xml.eoShelteredWorkshop;
            this.xml.eoRegistered = this.xml.eoRegistered == null ? { "exists": false } : this.xml.eoRegistered;
            this.xml.eoTogetherWithOthers = this.xml.eoTogetherWithOthers == null ? { "exists": false } : this.xml.eoTogetherWithOthers;
            this.xml.reliedEntities = this.xml.reliedEntities == null ? { "exists": false } : this.xml.reliedEntities;
            this.xml.notReliedEntities = this.xml.notReliedEntities == null ? { "exists": false } : this.xml.notReliedEntities;
            this.xml.lotsEoTendersTo = this.xml.lotsEoTendersTo == null ? { "exists": false } : this.xml.lotsEoTendersTo;
            this.xml.eoReductionOfCandidates = this.xml.eoReductionOfCandidates == null ? { "exists": false } : this.xml.eoReductionOfCandidates;
            this.xml.caLots = this.xml.caLots == null ? { "exists": false } : this.xml.caLots;
            this.xml.eoSme = this.xml.eoSme == null ? { "exists": false } : this.xml.eoSme;
            this.xml.suppEvidence = this.xml.suppEvidence == null ? { "exists": false } : this.xml.suppEvidence;
            this.xml.suppEvidenceOther = this.xml.suppEvidenceOther == null ? { "exists": false } : this.xml.suppEvidenceOther;
            this.populateForm(AppCostants.SA, this.xml);
            this.store.addElement(AppCostants.IMPORTED_XML, this.xml);            
            this.store.addElement(AppCostants.WHO_IMPORTED, 'SA');
            this.isConverted = true;
          },
            err => {
              this.isConverted = true;
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
      }, err => {
        this.isConverted = true;
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


  parseJwt(token) {
    var base64Url = token.split('.')[1];
    var base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    var jsonPayload = decodeURIComponent(atob(base64).split('').map(function (c) {
      return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
    }).join(''));

    return JSON.parse(jsonPayload);
  }

  populateForm(userInfo: any, restObject: any): any {
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
