import { Component, Injectable, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SdkFormBuilderConfiguration} from '@maggioli/sdk-controls';
import { FormBuilderService } from '../../../services/form-builder.service';
import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';
import { FormBuilderUtilsService } from '../../../utils/form-builder-utils.service';
import { BaseComponentComponent } from '../../base/base-component/base-component.component';
import { SdkDateHelper } from '@maggioli/sdk-commons';
import { TabellatiComboProvider } from '../../../providers/tabellati-combo.provider';


@Component({
  selector: 'app-esclusione',
  templateUrl: './esclusione.component.html',
  styleUrls: ['./esclusione.component.scss']
})
@Injectable()
export class EsclusioneComponent extends BaseComponentComponent implements OnInit {

  showBreadcrumbs : boolean;
  selectUser :boolean=true;
  public isLoading = false;
  pageName: string = AppCostants.PAGE_ESCLUSIONE;
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
      classSelezione : "",
      classFine : ""
    }
    this.previousPage=AppCostants.PAGE_PROCEDURA;
    this.nextPage=AppCostants.PAGE_SELEZIONE;

  }

  ngOnInit() {
    this.isLoading = true;
    super.ngOnInit();
    
    
  }

  ngAfterViewInit(){
    setTimeout(()=>{                                
      this.isLoading = false;
    }, 1000);
  }

  public manageFormOutput(formConfig: SdkFormBuilderConfiguration) {
    this.formConfig = formConfig;
  }

 

 
}
