import { Injector } from '@angular/core';
import { SdkBaseService, SdkValidator, SdkValidatorInput, SdkValidatorOutput } from '@maggioli/sdk-commons';
import {
    SdkFormBuilderConfiguration,
    SdkFormBuilderField,
    SdkFormFieldGroupConfiguration,
    SdkMessagePanelConfig,
    SdkMessagePanelService,
} from '@maggioli/sdk-controls';
import { each, every, isEmpty, isObject } from 'lodash-es';

import { MessagesPanelUtilsService } from '../messages-panel-utils.service';

export abstract class AbstractValidationUtilsService extends SdkBaseService {

    constructor(inj: Injector) {
        super(inj);
    }

    /**
     * Metodo che esegue la validazione di una form
     * @param formBuilderConfig La configurazione del form builder da validare
     * @param messagesPanel Il pannello HTML per appendere i messaggi
     * @returns Booleano che indica se la form e' valida
     */
    public executeValidations(formBuilderConfig: SdkFormBuilderConfiguration, messagesPanel: HTMLElement): boolean {
        this.sdkMessagePanelService.clear(messagesPanel);
        if (isObject(formBuilderConfig)) {
            let messagesConfig: Array<SdkMessagePanelConfig> = new Array();
            let valids: Array<boolean> = new Array();
            each(formBuilderConfig.fields, (field: SdkFormBuilderField) => {
                if (field.visible !== false) {
                    if (field.type === 'FORM-SECTION') {
                        valids = this.elaborateValidSection(field, valids, messagesPanel, messagesConfig);
                    } else if (field.type === 'FORM-GROUP') {
                        valids = this.elaborateValidGroup(field, valids, messagesPanel, messagesConfig);
                    } else {
                        valids = this.elaborateValidOne(field, valids, messagesPanel, messagesConfig);
                    }
                }
            });
            let valid: boolean = every(valids, one => one === true);
            if (valid === false) {
                this.sdkMessagePanelService.show(messagesPanel, messagesConfig);
            }
            valid = true;
            each(messagesConfig, (m: SdkMessagePanelConfig)=>{
                if(m.type === 'error'){
                    valid = false;
                }
            });
            return valid;
        }
        return false;
    }

    protected abstract elaborateValidOne(field: SdkFormBuilderField, valids: Array<boolean>, messagesPanel: HTMLElement, messagesConfig: Array<SdkMessagePanelConfig>): Array<boolean>;

    protected executeValidators(field: SdkFormBuilderField, messagesConfig: Array<SdkMessagePanelConfig>): boolean {

        let notValids: Array<SdkValidatorOutput> = new Array();

        each(field.validators, (one: SdkValidator<any>) => {
            
            let validatorInput: SdkValidatorInput<any> = {
                config: one.config,
                data: field.data
            }
            
            let out: SdkValidatorOutput = one.validator(validatorInput);

            if (out.valid === false) {
                messagesConfig = this.messagesPanelUtilsService.fillValidatorMessages(messagesConfig, out);
                notValids.push(out);
            }

        });

        return isEmpty(notValids);
    }

    private elaborateValidSection(field: SdkFormBuilderField, valids: Array<boolean>, messagesPanel: HTMLElement, messagesConfig: Array<SdkMessagePanelConfig>): Array<boolean> {
        each(field.fieldSections, (one: SdkFormBuilderField) => {
            if (field.visible !== false) {
                if (one.type === 'FORM-SECTION') {
                    valids = this.elaborateValidSection(one, valids, messagesPanel, messagesConfig);
                } else if (one.type === 'FORM-GROUP') {
                    valids = this.elaborateValidGroup(one, valids, messagesPanel, messagesConfig);
                } else {
                    valids = this.elaborateValidOne(one, valids, messagesPanel, messagesConfig);
                }
            }
        });
        return valids;
    }

    private elaborateValidGroup(field: SdkFormBuilderField, valids: Array<boolean>, messagesPanel: HTMLElement, messagesConfig: Array<SdkMessagePanelConfig>): Array<boolean> {
        each(field.fieldGroups, (group: SdkFormFieldGroupConfiguration, index: number) => {
            each(group.fields, (one: SdkFormBuilderField) => {
                if (field.visible !== false) {
                    if (one.type === 'FORM-SECTION') {
                        valids = this.elaborateValidSection(one, valids, messagesPanel, messagesConfig);
                    } else if (one.type === 'FORM-GROUP') {
                        valids = this.elaborateValidGroup(one, valids, messagesPanel, messagesConfig);
                    } else {
                        valids = this.elaborateValidOne(one, valids, messagesPanel, messagesConfig);
                    }
                }
            });
            field.fieldGroups[index] = group;
        });
        return valids;
    }

    // #region Getters

    protected get sdkMessagePanelService(): SdkMessagePanelService { return this.injectable(SdkMessagePanelService) }

    protected get messagesPanelUtilsService(): MessagesPanelUtilsService { return this.injectable(MessagesPanelUtilsService) }

    // #endregion
}