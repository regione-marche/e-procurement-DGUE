package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Getter
@AllArgsConstructor
public enum DocumentTypeCode implements CodeList {

    TED_CN("TED_CN", "Contract Notice published in TeD (Official Journal of the European Publications Office)", "Avviso di Appalto Pubblico pubblicato sul TED (Gazzetta Ufficiale dell'Ufficio Pubblicazioni dell'UE)"),
    ESPD_REQUEST("ESPD_REQUEST", "European Single Procurement Document Request issued by a Contracting Authority", "Richiesta di Documento di Gara Unico Europeo emessa da una Stazione Appaltante"),
    TECC016("TECC016", "Letter of Intent ", "Lettera di intenti"),
    TECC017("TECC017", "Status of the legal entity ", "Status della Persona Giuridica"),
    TECC018("TECC018", "Notice of appointment of the persons authorised to represent the tenderer", "Avviso di nomina delle persone autorizzate a rappresentare l'offerente"),
    TECC019("TECC019", "VAT registration document  ", "Documento di registrazione IVA"),
    TECC020("TECC020", "Identity document ", "Documento di identità"),
    TECC021("TECC021", "Reason for VAT exemption ", "Motivi di esenzione dall'IVA"),
    TECC022("TECC022", "Subcontracting justification ", "Giustificazione per il subappalto "),
    TECC024("TECC024", "Power of Attorney ", "Potere di rappresentanza"),
    Other("Other", "Other type of reference to a document", "Altro tipo di riferimento ad un documento"),
    NOJCN("NOJCN", "Contract Notice (CN) published on a National Government Official Journal", "Avviso di Appalto Pubblico (CN) pubblicato su una Gazzetta Ufficiale Nazionale"),
    ROJCN("ROJCN", "Contract Notice (CN) published on a Regional Government Official Journal", "Avviso di Appalto Pubblico (CN) pubblicato su una Gazzetta Ufficiale Regionale"),
    LOJCN("LOJCN", "Contract Notice (CN) published on a Local Government Official Journal", "Avviso di Appalto Pubblico (CN) Pubblicato su un Bollettino Locale"),
    NOJPIN("NOJPIN", "Prior Information Notice (PIN) published on a National Government Official Journal", "Avviso di Preinformazione (PIN) pubblicato su una Gazzetta Ufficiale Nazionale"),
    ROJPIN("ROJPIN", "Prior Information Notice (PIN) published on a Regional Government Official Journal", "Avviso di Preinformazione (PIN) pubblicato su una Gazzetta Ufficiale Regionale"),
    LOJPIN("LOJPIN", "Prior Information Notice (PIN) published on a Local Government Official Journal", "Avviso di Preinformazione (PIN) Pubblicato su un Bollettino Locale"),
    NOJCAN("NOJCAN", "Contract Award Notice (CAN) published on a National Government Official Journal", "Avviso di Aggiudicazione (CAN) pubblicato su una Gazzetta Ufficiale Nazionale"),
    ROJCAN("ROJCAN", "Contract Award Notice (CAN) published on a Regional Government Official Journal", "Avviso di Aggiudicazione (CAN) pubblicato su una Gazzetta Ufficiale Regionale"),
    LOJCAN("LOJCAN", "Contract Award Notice (CAN) published on a Local Government Official Journal", "Avviso di Aggiudicazione (CAN) Pubblicato su un Bollettino Locale");

    private final String code;
    private final String name;
    private final String translation;

    @Override
    public String getListVersionId() {
        return "2.1.0";
    }

    @Override
    public String getListId() {
        return "DocRefContentType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_GROW.getIdentifier();
    }
}
