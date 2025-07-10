package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.importing;

import java.util.Collection;
import java.util.List;

import org.springframework.util.CollectionUtils;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.enums.other.DocumentTypeCode;
import it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.MarshallingConstants;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.DocumentReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ExternalReferenceType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;

/**
 * Created by ratoico on 1/21/16 at 1:51 PM.
 */
final class UblDocumentReferences {

    private UblDocumentReferences() {

    }

    static List<DocumentReferenceType> filterByTypeCode(List<DocumentReferenceType> unfiltered,
		    final DocumentTypeCode typeCode) {
        if (CollectionUtils.isEmpty(unfiltered)) {
            return ImmutableList.of();
        }

        Collection<DocumentReferenceType> filtered = Collections2.filter(unfiltered,
                new DocumentReferenceTypePredicate(typeCode));
        return ImmutableList.copyOf(filtered);
    }

    static String readIdValue(DocumentReferenceType input) {
        if (input == null || input.getID() == null || hasTemporaryOjsNumber(input)) {
            return null;
        }

        return input.getID().getValue();
    }

    private static boolean hasTemporaryOjsNumber(DocumentReferenceType input) {
        return MarshallingConstants.TEMPORARY_OJS_NUMBER_SCHEME_ID.equals(input.getID().getSchemeID()) ||
                MarshallingConstants.TEMPORARY_OJS_NUMBER.equals(input.getID().getValue());
    }

    static String readFileNameValue(DocumentReferenceType input) {
        if (input == null || input.getAttachment() == null || input.getAttachment().getExternalReference() == null) {
            return null;
        }

        ExternalReferenceType externalReference = input.getAttachment().getExternalReference();
        if (externalReference.getFileName() != null) {
            return externalReference.getFileName().getValue();
        }

        return null;
    }

    static String readDescriptionValue(DocumentReferenceType input) {
        return readDescriptionValue(input, 0);
    }

    static String readDescriptionValue(DocumentReferenceType input, int position) {
        if (input == null || input.getAttachment() == null || input.getAttachment().getExternalReference() == null) {
            return null;
        }

        ExternalReferenceType externalReference = input.getAttachment().getExternalReference();
        if (externalReference.getDescription() != null && externalReference.getDescription().size() > position) {
            DescriptionType descriptionType = externalReference.getDescription().get(position);
            return descriptionType.getValue();
        }

        return null;
    }

    static String readDocumentDescriptionValue(DocumentReferenceType input) {
        if (input == null || input.getDocumentDescription() == null || input.getDocumentDescription().size() < 1) {
            return null;
        }

       return input.getDocumentDescription().get(0).getValue();
    }

    static String readUUIDValue(DocumentReferenceType input) {
        if (input.getUUID() == null) {
            return null;
        }

        return input.getUUID().getValue();
    }

    static String readIssueTimeValue(DocumentReferenceType input) {
        if (input.getIssueTime() == null) {
            return null;
        }

        return input.getIssueTime().getValue().toString();
    }

    private static class DocumentReferenceTypePredicate implements Predicate<DocumentReferenceType> {

        private final DocumentTypeCode typeCode;

        DocumentReferenceTypePredicate(DocumentTypeCode typeCode) {
            this.typeCode = typeCode;
        }

        @Override
        public boolean apply(DocumentReferenceType input) {
            if (input == null || input.getDocumentTypeCode() == null) {
                return false;
            }
            return typeCode.name().equalsIgnoreCase(input.getDocumentTypeCode().getValue());
        }
    }

}
