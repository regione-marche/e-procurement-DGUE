export interface ValoreTabellato {
    codice: string;
    descrizione: string;
    codistat?: string;
    archiviato: string;
    cap?: string;
}

export interface DynamicValue {
    codice?: number;
    descrizione?: string;
    value?: number;
}