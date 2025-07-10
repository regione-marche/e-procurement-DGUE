package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other;

import java.util.Optional;
import java.util.stream.Stream;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CodeList;
import lombok.Getter;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 04, 2020
 */
@Getter
public enum ProcedureType implements CodeList {

    PRIOR("PRIOR", "Prior information or periodic indicative notice", "Informazioni preliminari o avviso periodico indicativo"),
    OPEN("OPEN", "Open procedure", "Procedura aperta"),
    RESTRICTED("RESTRICTED", "Restricted procedure", "Procedura ristretta"),
    RESTRICTED_ACCELERATED("RESTRICTED_ACCELERATED", "Accelerated restricted procedure", "Procedura ristretta accelerata"),
    NEGOTIATED("NEGOTIATED", "Negotiated procedure", "Procedura negoziata senza previa pubblicazione del bando"),
    OPEN_RECUR("OPEN_RECUR", "Open procedure with recurring quantities", "Procedura aperta con quantità ricorrenti "),
    NEGOTIATED_ACCELERATED("NEGOTIATED_ACCELERATED", "Accelerated negotiated procedure", "Procedura negoziata accelerata"),
    AWARD("AWARD", "Contract awards", "Aggiudicazioni"),
    INFO("INFO", "General information", "Informazione generale"),
    AWARD_DIRECT("AWARD_DIRECT", "Direct Award", "Affidamento diretto ex art. 5 della legge n.381/91"),
    COMP_NEGOTIATION("COMP_NEGOTIATION", "Competitive procedure with negotiation", "Procedura competitiva con negoziazione"),
    COMP_DIALOGUE("COMP_DIALOGUE", "Competitive dialogue", "Dialogo competitivo"),
    DESIGN_CONTEST("DESIGN_CONTEST", "Design contest", "Concorso di progettazione"),
    CONCESSION("CONCESSION", "Concession award procedure", "Procedura di aggiudicazione della concessione"),
    CONCESSION_WO_PUB("CONCESSION_WO_PUB", "Concession award without prior concession notice", "Aggiudicazione di una concessione senza previa pubblicazione di un bando di concessione"),
    INNOVATION("INNOVATION", "Innovation partnership", "Partenariato per l'innovazione"),
    AMI("AMI", "Call for expressions of interest", "Invito a manifestazione di interesse"),
    NOT_SPECIFIED("NOT_SPECIFIED", "Not specified", "Non specificato"),
    QUAL("QUAL", "Qualification system", "Sistema di qualificazione"),
    CONTESTS_RESULT("CONTESTS_RESULT", "Results of design contests", "Risultati dei concorsi di progettazione"),
    NEGOTIATED_WO_CALL("NEGOTIATED_WO_CALL", "Negotiated without a prior call for competition", "Procedura negoziata previa pubblicazione del bando"),
    AWARD_WO_PUB("AWARD_WO_PUB", "Contract award without prior publication", "Aggiudicazione dell’appalto senza pubblicazione preliminare"),
    OTHER("OTHER", "Other", "Altro"),
    NOT_APPLICABLE("NOT_APPLICABLE", "Not applicable", "Non applicabile"),
    LOV("LOV", "Negotiated Procedure for low value contracts", "Affidamento in economia - cottimo fiduciario"),
    VDL("VDL", "Vendor's list", "Lista di fornitori");

    private final String code;
    private final String description;
    private final String translation;

    ProcedureType(final String code, final String description, final String translation) {
        this.code = code;
        this.description = description;
        this.translation = translation;
    }

    /**
     *
     * @param code
     * @return
     */
    public static Optional<ProcedureType> getProcedureTypeByCode(final String code) {
        return Stream.of(ProcedureType.values())
                .filter(procedureType -> code.equals(procedureType.getCode()))
                .findFirst();
    }

    /**
     *
     * @return
     */
    @Override
    public String getListVersionId() {
        return "1.0";
    }

    /**
     *
     * @return
     */
    @Override
    public String getListId() {
        return "ProcedureType";
    }

    @Override
    public String getListAgencyName() {
        return Agency.EU_COM_OP.getIdentifier();
    }


}
