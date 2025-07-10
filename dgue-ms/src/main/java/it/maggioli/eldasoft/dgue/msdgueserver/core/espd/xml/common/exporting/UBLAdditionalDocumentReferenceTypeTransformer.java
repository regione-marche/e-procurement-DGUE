package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.AdditionalDocumentReference;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ESPDDocument;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import org.springframework.stereotype.Component;

/**
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Component
public class UBLAdditionalDocumentReferenceTypeTransformer implements Function<ESPDDocument, Collection<DocumentReferenceType>> {

    @Override
    public Collection<DocumentReferenceType> apply(ESPDDocument input) {
        final List<DocumentReferenceType> documents = new ArrayList<>();
        // ESPD_REQUEST
        if (input.getId() != null && input.getUUID() != null && input.getIssueDate() != null && input.getIssueTime() != null) {
            documents.add(CommonUblFactory.buildEspdRequestReferenceType(input.getId(),input.getUUID(),input.getIssueDate(),input.getIssueTime()));
        }

        // TED_CN
        for (AdditionalDocumentReference adr : input.getAdditionalDocumentReference()) {
			
        	documents.add(CommonUblFactory.buildProcurementProcedureType(adr));
		}
      
        
        return documents;
    }
}
