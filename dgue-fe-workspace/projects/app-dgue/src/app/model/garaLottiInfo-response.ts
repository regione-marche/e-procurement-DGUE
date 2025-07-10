import { LottiInfo } from './lottiInfo-model';
import { GaraLottiInfo } from './garalotti-model';

export interface GaraLottiinfoResponse{
    garaLottiInfo: GaraLottiInfo ;
    lottiInfo: Array<LottiInfo> ;
    lottiInfoCig: String;
}