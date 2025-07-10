import { Component, Input, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { SdkDialogConfig } from '@maggioli/sdk-controls';
import { TranslateService } from '@ngx-translate/core';
import { isFunction } from 'lodash-es';
import { Observable, of, Subject } from 'rxjs';

import { StoreService } from '../../../services/storeService';
import { AppCostants } from '../../../utils/dgue-constants';


@Component({
  selector: 'app-navigation-button',
  templateUrl: './navigation-button.component.html',
  styleUrls: ['./navigation-button.component.scss']
})
export class NavigationButtonComponent implements OnInit {


  @Input() nextPage: string;
  @Input() previousPage: string;
  @Input() selectUser: boolean;
  @Input() nextCallback: Function;
  @Input() previousCallback: Function;
  @Input() download: Function;
  @Input() subDownload: boolean;
  @Input() fromPortale: boolean;
  @Input() valid: boolean;
  @Input() env: string;
  @Input() pagename: string;

  private dialogConfig: SdkDialogConfig;
  public dialogConfigObs: Observable<SdkDialogConfig>;
  public visibleHiddenFunction = false;

  constructor(private router: Router, public translateService: TranslateService, public store: StoreService,) { }

  ngOnInit() {
    this.visibleHiddenFunction = this.store.viewElement(AppCostants.VISIBLE_HIDDEN_FUNCTION);
    this.initDialog();
  }

  previous() {
    if (isFunction(this.previousCallback)) {
      this.previousCallback();
    }
    this.router.navigate([this.previousPage]);

  }

  next() {
    if (isFunction(this.nextCallback)) {
      this.nextCallback();
    }
    this.router.navigate([this.nextPage]);
  }

  save() {
    if (isFunction(this.nextCallback)) {
      this.nextCallback();
    }
  }

  downLoadFile(type) {
    if (isFunction(this.download)) {
      this.download(type);
    }
  }

  public cancel(): any {
    let func = this.backConfirm();
    this.dialogConfig.open.next(func);

  }

  public backConfirm(): any {
    return () => {
      this.router.navigate([AppCostants.PAGE_DGUE]);
    }
  }

  private initDialog(): void {
    this.dialogConfig = {
      header: this.translateService.instant('DIALOG.BACK-TITLE'),
      message: this.translateService.instant('DIALOG.BACK-TEXT'),
      acceptLabel: this.translateService.instant('DIALOG.CONFIRM-ACTION'),
      rejectLabel: this.translateService.instant('DIALOG.CANCEL-ACTION'),
      open: new Subject()
    };
    this.dialogConfigObs = of(this.dialogConfig);
  }

}
