import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SdkFormBuilderConfiguration } from '@maggioli/sdk-controls';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';

@Component({
  selector: 'app-fine',
  templateUrl: './fine.component.html',
  styleUrls: ['./fine.component.scss']
})
export class FineComponent  extends BaseComponentComponent implements OnInit {

  showBreadcrumbs : boolean;
  pageName: string = "fine";
  selectUser :boolean=true;
  
  constructor(public formBuildService : FormBuilderService, 
    public formBuildUtilsService : FormBuilderUtilsService,
     public store :StoreService,
     public dateHelper: SdkDateHelper,
     public tabellatiProvider: TabellatiComboProvider,
     public router:Router) {
      super(formBuildService, formBuildUtilsService,store,dateHelper,tabellatiProvider,router);
    this.showBreadcrumbs=true;
    this.breadcrumbsConfig = {
      classAvvio : "active disabled",
      classProcedura : "active",
      classEsclusione : "active",
      classSelezione : "active",
      classFine : "active"
    }
    
    this.previousPage=AppCostants.PAGE_SELEZIONE;
    this.nextPage="";
    
  }

  ngOnInit() {
    super.ngOnInit();
  }

  public manageFormOutput(formConfig: SdkFormBuilderConfiguration) {
    this.formConfig = formConfig;
  }

}
