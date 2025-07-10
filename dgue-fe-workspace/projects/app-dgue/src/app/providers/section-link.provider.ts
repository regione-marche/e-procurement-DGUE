import { Injectable, Injector } from '@angular/core';
import { IDictionary, SdkBaseService, SdkProvider } from '@maggioli/sdk-commons';
import { Observable } from 'rxjs';

import { RestService } from '../services/rest-service';
import { StoreService } from '../services/storeService';
import { AppCostants } from '../utils/dgue-constants';

@Injectable({ providedIn: 'root' })
export class SectionLinkProvider extends SdkBaseService implements SdkProvider {

    constructor(inj: Injector,
        private restService: RestService,
        public store: StoreService) {
        super(inj);
    }

    public run(args: IDictionary<any>): Observable<String> {
        let config: any = args.data.sectionConfig;
        let uuid: string[] = config.altriDati.childUUID;
        let lang: string = this.store.viewElement(AppCostants.STORE_LANG);
        return this.restService.getEcertisInfo(uuid, lang);
    }



}