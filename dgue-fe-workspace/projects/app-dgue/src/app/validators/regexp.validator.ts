import { Injectable, Injector } from '@angular/core';
import {
    SdkBaseService,
    SdkValidationMessage,
    SdkValidatorConfig,
    SdkValidatorFunction,
    SdkValidatorInput,
    SdkValidatorOutput,
    SdkValidatorServiceFunction,
} from '@maggioli/sdk-commons';
import { TranslateService } from '@ngx-translate/core';
import { each, get, isEmpty, isNull, isUndefined } from 'lodash-es';

@Injectable({ providedIn: 'root' })
export class RegexpValidator extends SdkBaseService implements SdkValidatorServiceFunction {

    constructor(inj: Injector) {
        super(inj);
    }

    public run(messages: Array<SdkValidationMessage>): SdkValidatorFunction<any> {
        return (input: SdkValidatorInput<any>): SdkValidatorOutput => {
            let config: SdkValidatorConfig = input.config;
            let regexp: RegExp = new RegExp(get(config, 'regexp'));
            let data: any = input.data;
            let valid: boolean = isUndefined(data) || isNull(data) ? true : regexp.test(data);
            return {
                valid,
                messages: this.parseMessages(messages)
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