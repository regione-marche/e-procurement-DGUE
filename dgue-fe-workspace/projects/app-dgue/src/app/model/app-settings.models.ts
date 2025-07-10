/**
 * @description
 * @author Fabio Gomiero
 * @date 2019-12-10
 * @export
 * @interface AppSettings
 */
export interface AppSettings {
    i18n: {
        availableLangs: Array<Language>,
        sessionKey: string
    };
}

/**
 * @description
 * @author Fabio Gomiero
 * @date 2019-12-10
 * @export
 * @interface Language
 */
export interface Language {
    code: string;
    alternativeCode: string;
    label: string;
    currency: string;
    default?: boolean;
    visualDateFormat?: string;
}
