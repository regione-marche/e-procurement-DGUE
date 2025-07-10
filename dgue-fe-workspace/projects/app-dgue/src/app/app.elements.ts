import { Type } from '@angular/core';
import { IDictionary } from '@maggioli/sdk-commons';
import { CpvModalWidget } from './layout/pages/cpv-modal/cpv-modal.widget';




export function customElementsMap(): IDictionary<Type<any>> {
    return {
       
        'cpv-modal-widget': CpvModalWidget,
        
    }
}
