import { Component, OnInit, ViewChild, ElementRef, HostListener } from '@angular/core';
import { Router } from '@angular/router';

import {
  SdkScrollToService, SdkDebounce,
} from '@maggioli/sdk-commons';
import { StoreService } from 'projects/app-dgue/src/app/services/storeService';
import { AppCostants } from 'projects/app-dgue/src/app/utils/dgue-constants';
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {

  @ViewChild('scrollToTop') public _scrollToTop: ElementRef;
  @ViewChild('scrollToBottom') public _scrollToBottom: ElementRef;

  public fromPortale = false;
  constructor(private sdkScrollToService : SdkScrollToService,private router:Router,public store: StoreService,) { }

  ngOnInit() {    
    this.fromPortale = this.store.viewElement(AppCostants.FROM_PORTALE);
  }

  public executeScrollToTop(): void {
    this.sdkScrollToService.scrollTo(document.body, 0, 400); // For Safari
    this.sdkScrollToService.scrollTo(document.documentElement, 0, 400); // other
  }

  public executeScrollToBottom(): void {
    this.sdkScrollToService.scrollTo(document.body, document.body.scrollHeight, 400); // For Safari
    this.sdkScrollToService.scrollTo(document.documentElement, document.body.scrollHeight, 400); // other
  }

  @SdkDebounce()
  @HostListener('window:scroll', [])
  public onScroll(): void { this.collapse(window.pageYOffset, 0) }

  // #endregion

  // #region Private

  private get scrollToTop(): HTMLElement {
      return this._scrollToTop.nativeElement;
  }

  private get scrollToBottom(): HTMLElement {
      return this._scrollToBottom.nativeElement;
  }

  private collapse(pageYOffset: number, initialHeaderBottomOffset: number): void {
      if (pageYOffset > initialHeaderBottomOffset) {
          this.scrollToTop.classList.add('go-to-show');
      } else {
          this.scrollToTop.classList.remove('go-to-show');
      }

      if ((window.innerHeight + window.scrollY) >= (document.body.offsetHeight - 2)) {
          this.scrollToBottom.classList.remove('go-to-show');
      } else {
          this.scrollToBottom.classList.add('go-to-show');
      }
  }

  public goToCookies(): void{
    this.router.navigate([AppCostants.PAGE_COOKIES]);
  }

  public goToCredits(): void{
    this.router.navigate([AppCostants.PAGE_CREDITS]);
  }

  public goToDisclaimer(): void{
    this.router.navigate([AppCostants.PAGE_DISCLAMER]);
  }

  public goToAssistenza(): void{
    this.router.navigate([AppCostants.PAGE_ASSISTENZA]);
  }

  public goToPrivacy(): void{
    this.router.navigate([AppCostants.PAGE_PRIVACY]);
  }


}
