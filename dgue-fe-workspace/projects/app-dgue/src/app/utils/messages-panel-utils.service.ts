import { Injectable, Injector } from '@angular/core';
import { IDictionary, SdkBaseService, SdkValidationMessage, SdkValidatorOutput } from '@maggioli/sdk-commons';
import {
    SdkMessagePanelConfig,
    SdkMessagePanelService,
    SdkMessagePanelTranslate,
    TSdkMEssagePanelType,
} from '@maggioli/sdk-controls';
import { each, findIndex, get, has, isUndefined, set } from 'lodash-es';

@Injectable({
    providedIn: 'root',
  })
export class MessagesPanelUtilsService extends SdkBaseService {

    constructor(inj: Injector) {
        super(inj);
    }

    /**
     * Metodo per mostrare un pannello di messaggio dato un errore di validazione
     * @param validatorResponse Risposta della validazione
     * @param messagesPanel Pannello HTML
     */
    public showValidatorMessages(validatorResponse: SdkValidatorOutput, messagesPanel: HTMLElement): void {
        let messages: IDictionary<Array<SdkMessagePanelTranslate>> = {};
        each(validatorResponse.messages, (mess: SdkValidationMessage) => {
            if (!has(messages, mess.level)) {
                set(messages, mess.level, []);
            }
            let arr: Array<SdkMessagePanelTranslate> = get(messages, mess.level);
            arr = [...arr, { message: mess.text }];
            set(messages, mess.level, arr);
        });
        each(messages, (value: Array<SdkMessagePanelTranslate>, key: string) => {
            switch (key) {
                case 'error':
                    this.sdkMessagePanelService.showError(messagesPanel, value);
                    break;
                case 'success':
                    this.sdkMessagePanelService.showSuccess(messagesPanel, value);
                    break;
                case 'warning':
                    this.sdkMessagePanelService.showWarning(messagesPanel, value);
                    break;
                case 'info':
                    this.sdkMessagePanelService.showInfo(messagesPanel, value);
                    break;
            }
        });
    }

    /**
     * Metodo per mostrare un pannello di messaggio dato un errore di validazione
     * @param validatorResponse Risposta della validazione
     * @param messagesPanel Pannello HTML
     */
    public fillValidatorMessages(messagesConfig: Array<SdkMessagePanelConfig>, validatorResponse: SdkValidatorOutput): Array<SdkMessagePanelConfig> {
        let messages: IDictionary<Array<SdkMessagePanelTranslate>> = {};
        each(validatorResponse.messages, (mess: SdkValidationMessage) => {
            if (!has(messages, mess.level)) {
                set(messages, mess.level, []);
            }
            let arr: Array<SdkMessagePanelTranslate> = get(messages, mess.level);
            arr = [...arr, { message: mess.text }];
            set(messages, mess.level, arr);
        });
        each(messages, (value: Array<SdkMessagePanelTranslate>, key: string) => {
            switch (key) {
                case 'error':
                    messagesConfig = this.fillError(messagesConfig, value);
                    break;
                case 'success':
                    messagesConfig = this.fillSuccess(messagesConfig, value);
                    break;
                case 'warning':
                    messagesConfig = this.fillWarning(messagesConfig, value);
                    break;
                case 'info':
                    messagesConfig = this.fillInfo(messagesConfig, value);
                    break;
            }
        });
        return messagesConfig
    }

    /**
     * Metodo per generare una configurazione multipannello di messaggi di errore
     * @param config La configurazione
     * @param messages i messaggi
     * @returns la configurazione aggiornata
     */
    public fillError(config: Array<SdkMessagePanelConfig>, messages: Array<SdkMessagePanelTranslate>): Array<SdkMessagePanelConfig> {
        return this.fill(config, messages, 'error');
    }

    /**
     * Metodo per generare una configurazione multipannello di messaggi di warning
     * @param config La configurazione
     * @param messages i messaggi
     * @returns la configurazione aggiornata
     */
    public fillWarning(config: Array<SdkMessagePanelConfig>, messages: Array<SdkMessagePanelTranslate>): Array<SdkMessagePanelConfig> {
        return this.fill(config, messages, 'warning');
    }

    /**
     * Metodo per generare una configurazione multipannello di messaggi di info
     * @param config La configurazione
     * @param messages i messaggi
     * @returns la configurazione aggiornata
     */
    public fillInfo(config: Array<SdkMessagePanelConfig>, messages: Array<SdkMessagePanelTranslate>): Array<SdkMessagePanelConfig> {
        return this.fill(config, messages, 'info');
    }

    /**
     * Metodo per generare una configurazione multipannello di messaggi di success
     * @param config La configurazione
     * @param messages i messaggi
     * @returns la configurazione aggiornata
     */
    public fillSuccess(config: Array<SdkMessagePanelConfig>, messages: Array<SdkMessagePanelTranslate>): Array<SdkMessagePanelConfig> {
        return this.fill(config, messages, 'success');
    }

    private fill(messagesConfig: Array<SdkMessagePanelConfig>, messages: Array<SdkMessagePanelTranslate>, type: TSdkMEssagePanelType): Array<SdkMessagePanelConfig> {
        let index: number = findIndex(messagesConfig, (one: SdkMessagePanelConfig) => one.type === type);
        if (index === -1) {
            let config: SdkMessagePanelConfig = {
                type,
                messages: [...messages]
            };
            messagesConfig.push(config);
        } else {
            if (isUndefined(messagesConfig[index].messages)) {
                messagesConfig[index].messages = new Array();
            }
            messagesConfig[index].messages.push(...messages);
        }
        return messagesConfig;
    }

    // #region Getters

    private get sdkMessagePanelService(): SdkMessagePanelService { return this.injectable(SdkMessagePanelService) }

    // #endregion
}