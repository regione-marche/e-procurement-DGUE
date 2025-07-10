import { Injectable, Injector } from '@angular/core';
import {
    SdkBaseService,
    SdkValidationMessage,
    SdkValidatorFunction,
    SdkValidatorInput,
    SdkValidatorOutput,
    SdkValidatorServiceFunction,
} from '@maggioli/sdk-commons';
import { TranslateService } from '@ngx-translate/core';
import { each, isArray, isEmpty, isNull, isObject, isUndefined } from 'lodash-es';

@Injectable({ providedIn: 'root' })
export class MandatoryValidator extends SdkBaseService implements SdkValidatorServiceFunction {

    constructor(inj: Injector) {
        super(inj);
    }

    public run(messages?: Array<SdkValidationMessage>): SdkValidatorFunction<any> {
        return (input: SdkValidatorInput<any>): SdkValidatorOutput => {
            let valid = !isUndefined(input.data) && !isNull(input.data) && (isObject(input.data) || !isEmpty(`${input.data}`.trim()));
            if(isArray(input.data) && input.data.length == 0){
                valid = false;
            }
            let defaultMessages: Array<SdkValidationMessage> = [
                {
                    level: 'error',
                    text: this.translateService.instant('VALIDATORS.MANDATORY')
                }
            ];
            return {
                valid,
                messages: !isEmpty(messages) ? this.parseMessages(messages) : defaultMessages
            };
        }
    }

    private parseMessages(messages: Array<SdkValidationMessage>): Array<SdkValidationMessage> {
        if (isEmpty(messages)) {
            return new Array();
        }
        each(messages, (one: SdkValidationMessage) => {
            one.text = this.translateService.instant(one.text);
        });
        return messages;
    }

    // #region Getters

    private get translateService(): TranslateService { return this.injectable(TranslateService) }

    // #endregion

}