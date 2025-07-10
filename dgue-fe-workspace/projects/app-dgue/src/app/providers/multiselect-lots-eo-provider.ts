import { Injectable, Injector } from '@angular/core';
import { IDictionary, SdkBaseService, SdkProvider } from '@maggioli/sdk-commons';
import { SdkMultiselectItem } from '@maggioli/sdk-controls';
import { isEmpty } from 'lodash-es';
import { of } from 'rxjs';

import { AppCostants } from '../utils/dgue-constants';


@Injectable({ providedIn: 'root' })
export class MultiselectLotsEoProvider extends SdkBaseService implements SdkProvider {

    constructor(inj: Injector) {
        super(inj);
    }

    public run(args: IDictionary<any>): Function {
        let jsonStorage = JSON.parse(localStorage.getItem(AppCostants.STORE_OBJ));
        let jsonLots = null;
        if (jsonStorage.procedura.lotsEoTendersTo != null) {
            jsonLots = jsonStorage.procedura.lotsEoTendersTo.lotId;
        }
        let cigArray: Array<SdkMultiselectItem> = []
        if (!isEmpty(jsonLots)) {
            jsonLots.forEach(element => {
                if (!isEmpty(element)) {
                    cigArray.push({
                        key: element.key,
                        value: element.key
                    });
                }
            });
            return () => {
                return of(cigArray);
            }
        } else {
            return () => {
                return of(cigArray);
            }
        }
    }
}