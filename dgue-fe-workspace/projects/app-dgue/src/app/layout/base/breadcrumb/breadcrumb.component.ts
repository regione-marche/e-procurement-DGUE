import { Component, Input, OnInit } from '@angular/core';
import { isFunction } from 'lodash-es';

import { BreadcrumbsClassConfig } from '../../../model/breadcrumbs-model';

@Component({
  selector: 'app-breadcrumb',
  templateUrl: './breadcrumb.component.html',
  styleUrls: ['./breadcrumb.component.scss']
})
export class BreadcrumbComponent implements OnInit {

  @Input() showBreadcrumbs: boolean;
  @Input() procedura: Function;
  @Input() esclusione: Function;
  @Input() selezione: Function;
  @Input() fine: Function;
  @Input() classConfig: BreadcrumbsClassConfig;

  constructor() { }

  ngOnInit() {

  }

  goToProcedura() {
    if (isFunction(this.procedura)) {
      this.procedura();
    }
  }
  goToEsclusione() {
    if (isFunction(this.esclusione)) {
      this.esclusione();
    }
  }
  goToSelezione() {
    if (isFunction(this.selezione)) {
      this.selezione();
    }
  }
  goToFine() {
    if (isFunction(this.fine)) {
      this.fine();
    }
  }


}
