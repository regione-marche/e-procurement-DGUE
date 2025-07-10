import { Component, ElementRef, Injectable, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { SdkFormBuilderConfiguration, SdkMessagePanelService, SdkMessagePanelTranslate } from '@maggioli/sdk-controls';
import { each, isObject } from 'lodash-es';
import { environment } from 'projects/app-dgue/src/environments/environment';

import { StoreModel } from '../../../model/store-model';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';
import { FormBuilderService } from '../../../services/form-builder.service';
import { RestService } from '../../../services/rest-service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';

declare var require: any;
@Component({
  selector: 'app-avvio',
  templateUrl: './avvio.component.html',
  styleUrls: ['./avvio.component.scss']
})
@Injectable()
export class AvvioComponent extends BaseComponentComponent implements OnInit {

  pageName: string = AppCostants.PAGE_AVVIO;
  showBreadcrumbs: boolean;
  showOption: boolean = false;
  showOptionImportCreate: boolean = false;
  fromXML: boolean = false;
  xmlSender: string = '';
  showOptionSA: boolean = false;
  public isLoading: boolean = false;
  public env: string = '';
  public visibleHiddenFunction = false;
  public isCaricamento = false;

  @ViewChild('errorspanel') _errorsPanel: ElementRef;
  @ViewChild('warningspanel') _warningspanel: ElementRef;
  @ViewChild('attachments') attachments: ElementRef;

  userInfo: string = "";

  storeObject: StoreModel = {
    procedura: {},
    esclusione: {},
    selezione: {},
    fine: {}
  }



  selectUser: boolean = false;



  constructor(public formBuildService: FormBuilderService,
    public formBuildUtilsService: FormBuilderUtilsService,
    public store: StoreService,
    public router: Router,
    public sdkMessagePanelService: SdkMessagePanelService,
    private restService: RestService,
    public dateHelper: SdkDateHelper,
    public tabellatiProvider: TabellatiComboProvider,
    private route: ActivatedRoute) {

    super(formBuildService, formBuildUtilsService, store, dateHelper, tabellatiProvider, router);

    this.showBreadcrumbs = true;
    this.breadcrumbsConfig = {
      classAvvio: "active",
      classProcedura: "disabled",
      classEsclusione: "disabled",
      classSelezione: "disabled",
      classFine: "disabled"
    }
    this.previousPage = AppCostants.PAGE_DGUE;
    this.nextPage = AppCostants.PAGE_PROCEDURA;
  }

  ngOnInit() {
    if (environment['env'] != null && environment['env'] === 'dev') {
      this.env = 'dev';
    } else if (environment['env'] != null && environment['env'] === 'prod') {
      this.env = 'prod';
    }
    this.sdkMessagePanelService.clear(this.errorPanel);
    // Metodo 2: Ottenere un singolo queryParam specifico
    const isCaricamento = this.route.snapshot.queryParamMap.get('isCaricamento');
    this.isCaricamento=isCaricamento == "true";
    
    super.ngOnInit();
  }

  private get errorPanel(): HTMLElement {
    return isObject(this._errorsPanel) ? this._errorsPanel.nativeElement : undefined;
  }

  private get warningsPanel(): HTMLElement {
    return isObject(this._warningspanel) ? this._warningspanel.nativeElement : undefined;
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
      } else if (file != null && files[0].type === 'application/json') {

        let fileReader = new FileReader();
        fileReader.readAsArrayBuffer(file);
        fileReader.onload = this.handleReaderLoadedJson;

        this.selectUser = true;
        this.sdkMessagePanelService.clear(this.errorPanel);
        this.fromXML = true;
      } else if (file != null && files[0].type !== 'text/xml') {
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

  public handleReaderLoadedJson = (ev: any) => {
    this.isLoading = true;
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    const array = new Uint8Array(ev.target.result);


    var string = new TextDecoder().decode(array);
    let obj = JSON.parse(string);
    this.store.addElement(AppCostants.STORE_OBJ, obj);
    this.selectUser = true;
    setTimeout(() => {
      this.isLoading = false;
    }, 2000);

  }
  public handleReaderLoadedXml = (ev: any) => {
    let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
    let messages: Array<string> = [];
    const array = new Uint8Array(ev.target.result);
    this.sdkMessagePanelService.clear(this.warningsPanel);
    let xmlBase64 = this.arrayBufferToBase64(array);

    if(this.isCaricamento){
      const decodedXml = atob(xmlBase64);
      if(decodedXml != null && (decodedXml.includes('<espd-res:QualificationApplicationResponse') || decodedXml.includes('urn:oasis:names:specification:ubl:schema:xsd:QualificationApplicationResponse-2'))){
        this.xmlSender = 'OE';
        this.store.addElement(AppCostants.STORE_USR, AppCostants.EO);
      } else if(decodedXml != null && (decodedXml.includes('<espd-req:QualificationApplicationRequest') || decodedXml.includes('urn:oasis:names:specification:ubl:schema:xsd:QualificationApplicationRequest-2'))){
        this.xmlSender = 'SA';
        this.store.addElement(AppCostants.STORE_USR, AppCostants.SA);
      }      
    }
    const fileByteArray = [];
    for (let i = 0; i < array.length; i++) {
      fileByteArray.push(array[i]);
    }
    //console.log(this.xmlSender);
    if (this.xmlSender === 'SA') {


      this.selectUser = false;
      this.restService.putXmlRequest(xmlBase64, this.xmlSender).subscribe((res: any) => {
        if(res?.response?.espd?.owner != 'MAGGIOLI'){
          let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
          
          let singleMessage : SdkMessagePanelTranslate = {
            message : "WARNINGS.UPLOAD_FILE_NOT_MAGGIOLI"
          }          
          messagesForPanel.push( singleMessage);          
          this.sdkMessagePanelService.clear(this.warningsPanel);
          this.sdkMessagePanelService.showWarning(this.warningsPanel,messagesForPanel);
        }
        this.isLoading = true;
        let authority = res.response.espd.authority;

        let cpvs = [];
        if (res.response.espd.cpvs != null) {
          res.response.espd.cpvs.forEach(element => {
            if (element != null) {
              let cpv = { "cpv": element };
              cpvs.push(cpv);
            }
          });
        }

        let storeObj = {
          "id": res.response.espd.id,
          "uuid": res.response.espd.uuid,
          "issueDate": res.response.espd.issueDate,
          "issueTime": res.response.espd.issueTime,
          "authority": authority,
          "owner": res.response.espd.owner,
          "procedura": {
            "codiceANAC": res.response.espd.codiceANAC,
            "procedureCode": res.response.espd.procedureCode,
            "authority": authority,
            "procedureTitle": res.response.espd.procedureTitle,
            "procedureShortDesc": res.response.espd.procedureShortDesc,
            "fileRefByCA": res.response.espd.fileRefByCA,
            "additionalDocumentReference": res.response.espd.additionalDocumentReference,
            "lots": res.response.espd.lots,
            "eoShelteredWorkshop": res.response.espd.eoShelteredWorkshop,
            "eoRegistered": res.response.espd.eoRegistered,
            "eoTogetherWithOthers": res.response.espd.eoTogetherWithOthers,
            "reliedEntities": res.response.espd.reliedEntities,
            "notReliedEntities": res.response.espd.notReliedEntities,
            "lotsEoTendersTo": res.response.espd.lotsEoTendersTo,
            "eoReductionOfCandidates": res.response.espd.eoReductionOfCandidates,
            "caLots": res.response.espd.caLots,
            "eoSme": res.response.espd.eoSme,
            "suppEvidence": res.response.espd.suppEvidence,
            "suppEvidenceOther": res.response.espd.suppEvidenceOther,
            "projectType": res.response.espd.projectType,
            "cpvs": cpvs
          },
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
        //modifico solo se una SA carica una request
        if (this.userInfo == AppCostants.SA) {
          this.modifyMultiselectLot(storeObj.selezione);
        }
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);
        this.store.addElement(AppCostants.IMPORTED_XML, { ...storeObj.procedura, ...storeObj.esclusione, ...storeObj.selezione, ...storeObj.fine });
        this.store.addElement(AppCostants.WHO_IMPORTED, this.xmlSender);
        this.selectUser = true;
        setTimeout(() => {
          this.isLoading = false;
        }, 2000);
      },
        err => {
          setTimeout(() => {
            this.isLoading = false;
          }, 1000);
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

    if (this.xmlSender === 'OE') {



      this.isLoading = true;
      this.selectUser = false;
      this.restService.putXmlResponse(xmlBase64, this.xmlSender).subscribe((res: any) => {
        if(res?.response?.espd?.owner != 'MAGGIOLI'){
          let messagesForPanel: Array<SdkMessagePanelTranslate> = [];
          
          let singleMessage : SdkMessagePanelTranslate = {
            message : "WARNINGS.UPLOAD_FILE_NOT_MAGGIOLI"
          }          
          messagesForPanel.push( singleMessage);          
          this.sdkMessagePanelService.clear(this.warningsPanel);
          this.sdkMessagePanelService.showWarning(this.warningsPanel,messagesForPanel);
        }
        let lotId = [];
        if (res.response.espd.lotsEoTendersTo != null && res.response.espd.lotsEoTendersTo.lotId != null) {
          res.response.espd.lotsEoTendersTo.lotId.forEach(element => {
            lotId.push({ "lotId": element });
          });
        }
        let authority = res.response.espd.authority;

        let storeObj = {
          "id": res.response.espd.id,
          "uuid": res.response.espd.uuid,
          "issueDate": res.response.espd.issueDate,
          "issueTime": res.response.espd.issueTime,
          "authority": authority,
          "owner": res.response.espd.owner,
          "procedura": {
            "codiceANAC": res.response.espd.codiceANAC,
            "procedureCode": res.response.espd.procedureCode,
            "authority": authority,
            "procedureTitle": res.response.espd.procedureTitle,
            "procedureShortDesc": res.response.espd.procedureShortDesc,
            "fileRefByCA": res.response.espd.fileRefByCA,
            "additionalDocumentReference": res.response.espd.additionalDocumentReference,
            "lots": res.response.espd.lots,
            "economicOperator": res.response.espd.economicOperator,
            "eoShelteredWorkshop": res.response.espd.eoShelteredWorkshop,
            "eoRegistered": res.response.espd.eoRegistered,
            "eoTogetherWithOthers": res.response.espd.eoTogetherWithOthers,
            "reliedEntities": res.response.espd.reliedEntities,
            "notReliedEntities": res.response.espd.notReliedEntities,
            "lotsEoTendersTo": res.response.espd.lotsEoTendersTo,
            "eoReductionOfCandidates": res.response.espd.eoReductionOfCandidates,
            "caLots": res.response.espd.caLots,
            "eoSme": res.response.espd.eoSme,
            "suppEvidence": res.response.espd.suppEvidence,
            "suppEvidenceOther": res.response.espd.suppEvidenceOther,
            "projectType": res.response.espd.projectType
          },
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
          "fine": {
            "signature": res.response.espd.signature,
            "location": res.response.espd.location,
            "signatureDate": res.response.espd.signatureDate
          },
        };

        this.modifyMultiselectLotEoTenderTo(storeObj);
        this.modifyMultiselectLotScReferences(storeObj);
        this.store.addElement(AppCostants.STORE_OBJ, storeObj);
        this.store.addElement(AppCostants.IMPORTED_XML, { ...storeObj.procedura, ...storeObj.esclusione, ...storeObj.selezione, ...storeObj.fine });
        this.store.addElement(AppCostants.WHO_IMPORTED, this.xmlSender);
        this.selectUser = true;

        setTimeout(() => {
          this.isLoading = false;
        }, 2000);
      },
        err => {
          setTimeout(() => {
            this.isLoading = false;
          }, 1000);
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

  /*
  public handleReaderLoaded = (readerEvt: any) => {
    var xmlJs = require("xml-js")
    let xml = readerEvt.target.result;
    let options = {compact: true, 
      textFn: this.removeJsonTextAttribute,
      elementNameFn: this.elementNameFn
    };
    let jsonFinale = xmlJs.xml2json(xml, options);
    this.store.addElement(AppCostants.STORE_OBJ,JSON.parse(jsonFinale).root);
  }
  */
  private modifyMultiselectLotEoTenderTo(storeObj: any) {
    if (storeObj.procedura.lotsEoTendersTo != null) {
      let lotId = [];
      if (storeObj.procedura.lotsEoTendersTo.lotId != null && storeObj.procedura.lotsEoTendersTo.lotId.length > 0) {

        for (let i = 0; i < storeObj.procedura.lotsEoTendersTo.lotId.length; i++) {
          let lot = {};
          lot = { key: storeObj.procedura.lotsEoTendersTo.lotId[i].lotId };
          lotId.push(lot);
        }
        storeObj.procedura.lotsEoTendersTo.lotId = lotId;

      }
    }
  }

  private modifyMultiselectLotScReferences(storeObj: any) {
    let workContractsPerformanceOfWorks = storeObj.selezione.workContractsPerformanceOfWorks;
    if (workContractsPerformanceOfWorks != null) {
      let lotId = [];
      if (workContractsPerformanceOfWorks.unboundedGroups != null && workContractsPerformanceOfWorks.unboundedGroups.length > 0) {        
        for (let i = 0; i < workContractsPerformanceOfWorks.unboundedGroups.length; i++) {
          let lots = workContractsPerformanceOfWorks.unboundedGroups[i].lotId;
          if (lots != null && lots.length > 0) {
            for (let i = 0; i < lots.length; i++) {
              let lot = {};
              lot = { key: lots[i], value: lots[i] };
              lotId.push(lot);
            }
            storeObj.selezione.workContractsPerformanceOfWorks.unboundedGroups[i].lotId = lotId   
            lotId = [];
          }
        }        
      }
    }

    let supplyContractsPerformanceDeliveries = storeObj.selezione.supplyContractsPerformanceDeliveries;
    if (supplyContractsPerformanceDeliveries != null) {
      let lotId = [];
      if (supplyContractsPerformanceDeliveries.unboundedGroups != null && supplyContractsPerformanceDeliveries.unboundedGroups.length > 0) {        
        for (let i = 0; i < supplyContractsPerformanceDeliveries.unboundedGroups.length; i++) {
          let lots = supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId;
          if (lots != null && lots.length > 0) {
            for (let i = 0; i < lots.length; i++) {
              let lot = {};
              lot = { key: lots[i], value: lots[i] };
              lotId.push(lot);
            }            
            storeObj.selezione.supplyContractsPerformanceDeliveries.unboundedGroups[i].lotId = lotId
            lotId = [];            
          }
        }        
      }
    }

    let serviceContractsPerformanceServices = storeObj.selezione.serviceContractsPerformanceServices;
    if (serviceContractsPerformanceServices != null) {
      let lotId = [];
      if (serviceContractsPerformanceServices.unboundedGroups != null && serviceContractsPerformanceServices.unboundedGroups.length > 0) {        
        for (let i = 0; i < serviceContractsPerformanceServices.unboundedGroups.length; i++) {
          let lots = serviceContractsPerformanceServices.unboundedGroups[i].lotId;
          if (lots != null && lots.length > 0) {
            for (let i = 0; i < lots.length; i++) {
              let lot = {};
              lot = { key: lots[i], value: lots[i] };
              lotId.push(lot);
            }
            storeObj.selezione.serviceContractsPerformanceServices.unboundedGroups[i].lotId = lotId    
            lotId = [];        
          }
        }        
      }
    }
  }

  private modifyMultiselectLot(storeObj: any) {

    if (storeObj.enrolmentProfessionalRegister != null && storeObj.enrolmentProfessionalRegister.unboundedGroups != null && storeObj.enrolmentProfessionalRegister.unboundedGroups.length != null && storeObj.enrolmentProfessionalRegister.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.enrolmentProfessionalRegister.unboundedGroups.length; i++) {
        let lotids = [];
        if(storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId != null){
          for (let j = 0; j < storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId.length; j++) {
            if (storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId[j] != null) {              
              lotids.push({ key: storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId[j], value: storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId[j] });
            }
            storeObj.enrolmentProfessionalRegister.unboundedGroups[i] = { ...storeObj.enrolmentProfessionalRegister.unboundedGroups[i], ...{ lotids: lotids } };
          }
          delete storeObj.enrolmentProfessionalRegister.unboundedGroups[i].lotId;
        }        
      }
    }

    if (storeObj.enrolmentTradeRegister != null && storeObj.enrolmentTradeRegister.unboundedGroups != null && storeObj.enrolmentTradeRegister.unboundedGroups.length != null && storeObj.enrolmentTradeRegister.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.enrolmentTradeRegister.unboundedGroups.length; i++) {
        let lotids = [];
        if(storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId != null){
          for (let j = 0; j < storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId.length; j++) {
            if (storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId[j] != null) {
              lotids.push({ key: storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId[j], value: storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId[j] });
            }
            storeObj.enrolmentTradeRegister.unboundedGroups[i] = { ...storeObj.enrolmentTradeRegister.unboundedGroups[i], ...{ lotids: lotids } };
          }
          delete storeObj.enrolmentTradeRegister.unboundedGroups[i].lotId;
        }        
      }
    }

    if (storeObj.serviceContractsAuthorisation != null && storeObj.serviceContractsAuthorisation.unboundedGroups != null && storeObj.serviceContractsAuthorisation.unboundedGroups.length != null && storeObj.serviceContractsAuthorisation.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.serviceContractsAuthorisation.unboundedGroups.length; i++) {
        let lotids = [];
        if(storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId != null){
          for (let j = 0; j < storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId.length; j++) {
            if (storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId[j] != null) {
              lotids.push({ key: storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId[j], value: storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId[j] });
            }
            storeObj.serviceContractsAuthorisation.unboundedGroups[i] = { ...storeObj.serviceContractsAuthorisation.unboundedGroups[i], ...{ lotids: lotids } };
          }
          delete storeObj.serviceContractsAuthorisation.unboundedGroups[i].lotId;
        }
        
      }
    }

    if (storeObj.serviceContractsMembership != null && storeObj.serviceContractsMembership.unboundedGroups != null && storeObj.serviceContractsMembership.unboundedGroups.length != null && storeObj.serviceContractsMembership.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.serviceContractsMembership.unboundedGroups.length; i++) {
        let lotids = [];
        if(storeObj.serviceContractsMembership.unboundedGroups[i].lotId != null){
          for (let j = 0; j < storeObj.serviceContractsMembership.unboundedGroups[i].lotId.length; j++) {
            if (storeObj.serviceContractsMembership.unboundedGroups[i].lotId[j] != null) {
              lotids.push({ key: storeObj.serviceContractsMembership.unboundedGroups[i].lotId[j], value: storeObj.serviceContractsMembership.unboundedGroups[i].lotId[j] });
            }
            storeObj.serviceContractsMembership.unboundedGroups[i] = { ...storeObj.serviceContractsMembership.unboundedGroups[i], ...{ lotids: lotids } };
          }
          delete storeObj.serviceContractsMembership.unboundedGroups[i].lotId;
        }
        
      }
    }

    if (storeObj.professionalRiskInsurance != null && storeObj.professionalRiskInsurance.unboundedGroups != null && storeObj.professionalRiskInsurance.unboundedGroups.length != null && storeObj.professionalRiskInsurance.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.professionalRiskInsurance.unboundedGroups.length; i++) {
        let lotids = [];
        for (let j = 0; j < storeObj.professionalRiskInsurance.unboundedGroups[i].lotId.length; j++) {
          if (storeObj.professionalRiskInsurance.unboundedGroups[i].lotId[j] != null) {
            lotids.push({ key: storeObj.professionalRiskInsurance.unboundedGroups[i].lotId[j], value: storeObj.professionalRiskInsurance.unboundedGroups[i].lotId[j] });
          }
          storeObj.professionalRiskInsurance.unboundedGroups[i] = { ...storeObj.professionalRiskInsurance.unboundedGroups[i], ...{ lotids: lotids } };
        }
        delete storeObj.professionalRiskInsurance.unboundedGroups[i].lotId;
      }
    }

    if (storeObj.otherEconomicFinancialRequirements != null && storeObj.otherEconomicFinancialRequirements.unboundedGroups != null && storeObj.otherEconomicFinancialRequirements.unboundedGroups.length != null && storeObj.otherEconomicFinancialRequirements.unboundedGroups.length > 0) {
      for (let i = 0; i < storeObj.otherEconomicFinancialRequirements.unboundedGroups.length; i++) {
        let lotids = [];
        for (let j = 0; j < storeObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotId.length; j++) {
          if (storeObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotId[j] != null) {
            lotids.push({ key: storeObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotId[j], value: storeObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotId[j] });
          }
          storeObj.otherEconomicFinancialRequirements.unboundedGroups[i] = { ...storeObj.otherEconomicFinancialRequirements.unboundedGroups[i], ...{ lotids: lotids } };
        }
        delete storeObj.otherEconomicFinancialRequirements.unboundedGroups[i].lotId;
      }
    }

    if (storeObj.workContractsPerformanceOfWorks != null) {
      let lotId = [];
      if (storeObj.workContractsPerformanceOfWorks.lotId != null && storeObj.workContractsPerformanceOfWorks.lotId.length > 0) {

        for (let i = 0; i < storeObj.workContractsPerformanceOfWorks.lotId.length; i++) {
          let lot = {};
          lot = { key: storeObj.workContractsPerformanceOfWorks.lotId[i].lotId, value: storeObj.workContractsPerformanceOfWorks.lotId[i].lotId };
          lotId.push(lot);
        }
        storeObj.workContractsPerformanceOfWorks.lotId = lotId;

      }
    }

    if (storeObj.supplyContractsPerformanceDeliveries != null) {
      let lotId = [];
      if (storeObj.supplyContractsPerformanceDeliveries.lotId != null && storeObj.supplyContractsPerformanceDeliveries.lotId.length > 0) {

        for (let i = 0; i < storeObj.supplyContractsPerformanceDeliveries.lotId.length; i++) {
          let lot = {};
          lot = { key: storeObj.supplyContractsPerformanceDeliveries.lotId[i].lotId, value: storeObj.supplyContractsPerformanceDeliveries.lotId[i].lotId };
          lotId.push(lot);
        }
        storeObj.supplyContractsPerformanceDeliveries.lotId = lotId;

      }
    }

    if (storeObj.serviceContractsPerformanceServices != null) {
      let lotId = [];
      if (storeObj.serviceContractsPerformanceServices.lotId != null && storeObj.serviceContractsPerformanceServices.lotId.length > 0) {

        for (let i = 0; i < storeObj.serviceContractsPerformanceServices.lotId.length; i++) {
          let lot = {};
          lot = { key: storeObj.serviceContractsPerformanceServices.lotId[i].lotId, value: storeObj.serviceContractsPerformanceServices.lotId[i].lotId };
          lotId.push(lot);
        }
        storeObj.serviceContractsPerformanceServices.lotId = lotId;

      }
    }




  }

  removeJsonTextAttribute = (value, parentElement) => {
    try {
      var keyNo = Object.keys(parentElement._parent).length;
      var keyName = Object.keys(parentElement._parent)[keyNo - 1];
      parentElement._parent[keyName] = value;
    }
    catch (e) { }
  }

  elementNameFn = (value, parentElement) => {
    let xmlarray = AppCostants.XML_ARRAY_CODE;
    if (xmlarray.includes(value)) {
      if (!Array.isArray(parentElement[value])) {
        parentElement[value] = [];
      }
    }
    return value;
  }


  public manageFormOutput(formConfig: SdkFormBuilderConfiguration) {
    this.formConfig = formConfig;
  }

  onItemSelectUser(values) {
    if (values.target.value === AppCostants.EO) {
      this.showOptionImportCreate = false;
      this.showOption = true;
      this.userInfo = AppCostants.EO;
      this.selectUser = false;
      this.showOptionSA = false;
    } else {
      this.showOption = false;
      this.showOptionSA = true;
      this.showOptionImportCreate = false;
      this.userInfo = AppCostants.SA;
      this.selectUser = false;
    }
    /*let infoAppalti=this.store.viewElement('INFO_APPALTI');
    if(infoAppalti != null || infoAppalti != undefined){     
      
    }*/
    this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);

    this.fromXML = false;
  }


  onItemImportCreate(value: any, whoIs?: string) {
    this.sdkMessagePanelService.clear(this.warningsPanel);
    if (whoIs !== this.xmlSender && this.attachments !== undefined) {
      this.attachments.nativeElement.value = ""
    }
    if (value.target.value === AppCostants.EO_IMPORT) {
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.EO_COMPILE, false);
    } else if (value.target.value === AppCostants.SA_IMPORT) {
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.SA_COMPILE, false);
    } else if (value.target.value === AppCostants.EO_JSON) {
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.EO_COMPILE, false);
    } else if (value.target.value === AppCostants.SA_JSON) {
      this.showOptionImportCreate = true;
      this.selectUser = false;
      this.store.addElement(AppCostants.SA_COMPILE, false);
    } else {
     
        this.showOptionImportCreate = false;
        this.selectUser = true;
        this.store.addElement(AppCostants.EO_COMPILE, true);
        let allExists =
        {
          ...this.storeObject,          
          "owner":"MAGGIOLI",
          "procedura": {
          },
          "esclusione": {
            "criminalConvictions": {
              "exists": true
            },
            "corruption": {
              "exists": true
            },
            "fraud": {
              "exists": true
            },
            "terroristOffences": {
              "exists": true
            },
            "moneyLaundering": {
              "exists": true
            },
            "childLabour": {
              "exists": true
            },
            "paymentTaxes": {
              "exists": true
            },
            "paymentSocialSecurity": {
              "exists": true
            },
            "breachingObligationsEnvironmental": {
              "exists": true
            },
            "breachingObligationsSocial": {
              "exists": true
            },
            "breachingObligationsLabour": {
              "exists": true
            },
            "bankruptcy": {
              "exists": true
            },
            "insolvency": {
              "exists": true
            },
            "arrangementWithCreditors": {
              "exists": true
            },
            "analogousSituation": {
              "exists": true
            },
            "assetsAdministeredByLiquidator": {
              "exists": true
            },
            "businessActivitiesSuspended": {
              "exists": true
            },
            "guiltyGrave": {
              "exists": true
            },
            "agreementsWithOtherEO": {
              "exists": true
            },
            "conflictInterest": {
              "exists": true
            },
            "involvementPreparationProcurement": {
              "exists": true
            },
            "earlyTermination": {
              "exists": true
            },
            "guiltyMisinterpretation": {
              "exists": true
            },
            "nationalExclusionGrounds": {
              "exists": true
            }
          },
          "selezione": {
            "enrolmentProfessionalRegister": {
              "exists": true
            },
            "enrolmentTradeRegister": {
              "exists": true
            },
            "serviceContractsAuthorisation": {
              "exists": true
            },
            "serviceContractsMembership": {
              "exists": true
            },
            "generalYearlyTurnover": {
              "exists": true
            },
            "averageYearlyTurnover": {
              "exists": true
            },
            "specificYearlyTurnover": {
              "exists": true
            },
            "specificAverageTurnover": {
              "exists": true
            },
            "setupEconomicOperator": {
              "exists": true
            },
            "financialRatio": {
              "exists": true
            },
            "professionalRiskInsurance": {
              "exists": true
            },
            "otherEconomicFinancialRequirements": {
              "exists": true
            },
            "workContractsPerformanceOfWorks": {
              "exists": true
            },
            "supplyContractsPerformanceOfDeliveries": {
              "exists": true
            },
            "serviceContractsPerformanceServices": {
              "exists": true
            },
            "techniciansTechnicalBodies": {
              "exists": true
            },
            "workContractsTechnicians": {
              "exists": true
            },
            "technicalFacilitiesMeasures": {
              "exists": true
            },
            "studyResearchFacilities": {
              "exists": true
            },
            "supplyChainManagement": {
              "exists": true
            },
            "environmentalManagementFeatures": {
              "exists": true
            },
            "toolsPlantTechnicalEquipment": {
              "exists": true
            },
            "educationalProfessionalQualifications": {
              "exists": true
            },
            "allowanceOfChecks": {
              "exists": true
            },
            "numberManagerialStaff": {
              "exists": true
            },
            "averageAnnualManpower": {
              "exists": true
            },
            "subcontractingProportion": {
              "exists": true
            },
            "supplyContractsSamplesDescriptionsWithoutCa": {
              "exists": true
            },
            "supplyContractsSamplesDescriptionsWithCa": {
              "exists": true
            },
            "supplyContractsCertificatesQc": {
              "exists": true
            },
            "certificateIndependentBodiesAboutQa": {
              "exists": true
            },
            "certificateIndependentBodiesAboutEnvironmental": {
              "exists": true
            }
          }
        }
        this.store.addElement(AppCostants.STORE_OBJ, allExists);
      

    }

    this.fromXML = false;
    this.xmlSender = whoIs;
  }

  public preNext = () => {
    this.store.addElement(AppCostants.STORE_USR, this.userInfo);
    if (this.userInfo === 'sa' && this.fromXML === false) {
      this.storeObject = {        
        "owner":"MAGGIOLI",
        procedura: {},
        esclusione: {},
        selezione: {},
        fine: {}
      }
      this.store.addElement(AppCostants.STORE_OBJ, this.storeObject);
    }
    this.store.addElement(AppCostants.FROM_XML, this.fromXML);
  }


  public prePrevious = () => {
    this.store.clearStore();
  }

  public hiddenFunction() {
    this.visibleHiddenFunction = !this.visibleHiddenFunction;
    this.store.addElement(AppCostants.VISIBLE_HIDDEN_FUNCTION, this.visibleHiddenFunction);
    this.showOptionImportCreate = false;
    this.sdkMessagePanelService.clear(this.warningsPanel);
    this.sdkMessagePanelService.clear(this.errorPanel);
  }

  navigate(){
    this.router.navigate([AppCostants.PAGE_QUADRO_GENERALE_CARICAMENTO]);
  }

  back(){
    this.router.navigate([AppCostants.PAGE_DGUE]);
  }


}













