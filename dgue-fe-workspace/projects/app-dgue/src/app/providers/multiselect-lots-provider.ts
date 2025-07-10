import { Injectable, Injector } from '@angular/core';
import { IDictionary, SdkBaseService, SdkProvider } from '@maggioli/sdk-commons';
import { SdkMultiselectItem } from '@maggioli/sdk-controls';
import { isEmpty } from 'lodash-es';
import { of } from 'rxjs';

import { AppCostants } from '../utils/dgue-constants';


@Injectable({ providedIn: 'root' })
export class MultiselectLotsProvider extends SdkBaseService implements SdkProvider {

    constructor(inj: Injector) {
        super(inj);
    }

    public run(args: IDictionary<any>): Function {
        let jsonStorage = JSON.parse(localStorage.getItem(AppCostants.STORE_OBJ));
        let jsonLots = jsonStorage.procedura.lots;
        let cigArray : Array<SdkMultiselectItem>= []
        if(!isEmpty(jsonLots)){
            jsonLots.forEach(element => {
                if(!isEmpty(element.numLot)){
                    cigArray.push({
                        key: element.numLot,
                        value: element.numLot
                    });
                }
            });
            return () => {
                return of(cigArray);
            }
        } else {
            return  () => {
                return of(cigArray);
            }
        }
    }
}