import { Type } from '@angular/core';
import { IDictionary, SdkProvider } from '@maggioli/sdk-commons';
import { MultiselectLotsProvider } from './providers/multiselect-lots-provider';
import { MultiselectLotsSCPSProvider } from './providers/multiselect-lots-scps-provider';
import { SectionLinkProvider } from './providers/section-link.provider';

import { TabellatiComboProvider } from './providers/tabellati-combo.provider';
import { MultiselectLotsWCPWProvider } from './providers/multiselect-lots-wcpw-provider';
import { MultiselectLotsSCPDProvider } from './providers/multiselect-lots-scpd-provider';
import { MultiselectLotsEoProvider } from './providers/multiselect-lots-eo-provider';

export function providers(): IDictionary<Type<SdkProvider>> {
    return {
        DGUE_TABELLATI_COMBO: TabellatiComboProvider,
        SECTION_LINK_PROVIDER: SectionLinkProvider,
        MULTISELECT_LOTS_PROVIDER: MultiselectLotsProvider,
        MULTISELECT_LOTS_SCPS_PROVIDER: MultiselectLotsSCPSProvider,
        MULTISELECT_LOTS_SCPD_PROVIDER: MultiselectLotsSCPDProvider,
        MULTISELECT_LOTS_WCPW_PROVIDER: MultiselectLotsWCPWProvider,
        MULTISELECT_LOTS_EO_PROVIDER: MultiselectLotsEoProvider
    };
}
