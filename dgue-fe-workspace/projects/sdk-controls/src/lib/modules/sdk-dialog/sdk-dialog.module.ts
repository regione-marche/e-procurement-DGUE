import { CommonModule } from '@angular/common';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SdkClickModule } from '@maggioli/sdk-commons';
import { ConfirmationService } from 'primeng/api';

import { PrimeNGModule } from '../../imports/primeng.module';
import { SdkDialogComponent } from './components/sdk-dialog/sdk-dialog.component';
import { SdkMotivazioneDialogComponent } from './components/sdk-motivazione-dialog/sdk-motivazione-dialog.component';

@NgModule({
  declarations: [
    SdkDialogComponent,
    SdkMotivazioneDialogComponent
  ],
  imports: [
    CommonModule,
    FormsModule,
    PrimeNGModule,
    SdkClickModule
  ],
  providers: [
    ConfirmationService
  ],
  exports: [
    SdkDialogComponent,
    SdkMotivazioneDialogComponent
  ]
})
export class SdkDialogModule { }
