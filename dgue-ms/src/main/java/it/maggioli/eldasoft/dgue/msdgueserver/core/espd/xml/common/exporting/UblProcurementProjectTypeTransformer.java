package it.maggioli.eldasoft.dgue.msdgueserver.core.espd.xml.common.exporting;

import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.isBlank;
import static it.maggioli.eldasoft.dgue.msdgueserver.utils.StringUtils.trimToEmpty;

import java.util.Collection;
import java.util.stream.Collectors;

import com.google.common.base.Function;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacProcurementProject;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.CommodityClassificationType;
import oasis.names.specification.ubl.schema.xsd.commonaggregatecomponents_2.ProcurementProjectType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.DescriptionType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ItemClassificationCodeType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.NameType;
import oasis.names.specification.ubl.schema.xsd.commonbasiccomponents_2.ProcurementTypeCodeType;
import org.springframework.stereotype.Component;

/**
 * Componente per la trasformazione da un oggetto {@link CacProcurementProject} a {@link ProcurementProjectType}.
 * <p/>
 * Usare {@link ProcurementProjectType} per identificare e descrivere la procedura amministrativa di gara.
 * Se la procedura di gara è divisa in lotti usare il componente ProcurementProcedureLot
 * per fornire dettagli specifici sui lotti e utilizzare il componente ProcurementProject
 * per descrivere le caratteristiche globali della procedura
 * <p/>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 05, 2020
 */
@Component
public class UblProcurementProjectTypeTransformer implements Function<CacProcurementProject, ProcurementProjectType> {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public ProcurementProjectType apply(final CacProcurementProject input) {
        if (input == null) {
            return null;
        }

        final ProcurementProjectType procurementProjectType = new ProcurementProjectType();
        addProcurementProjectNameInformation(input, procurementProjectType);
        addProcurementProjectDescriptionInformation(input, procurementProjectType);
        addProcurementProjectTypeInformation(input, procurementProjectType);
        addProcurementProjectMainCommonFacilityInformation(input, procurementProjectType);
        return procurementProjectType;
    }

    /**
     * Titolo della procedura di gara
     *
     * @param input
     * @param procurementProjectType
     */
    private void addProcurementProjectNameInformation(final CacProcurementProject input, final ProcurementProjectType procurementProjectType) {
        if (isBlank(input.getName())) {
            return;
        }
        final NameType projectName = new NameType();
        projectName.setValue(trimToEmpty(input.getName()));
        procurementProjectType.getName().add(projectName);
    }

    /**
     * Descrizione della procedura di gara
     *
     * @param input
     * @param procurementProjectType
     */
    private void addProcurementProjectDescriptionInformation(final CacProcurementProject input, final ProcurementProjectType procurementProjectType) {
        if (isBlank(input.getDescription())) {
            return;
        }
        final DescriptionType projectDescription = new DescriptionType();
        projectDescription.setValue(trimToEmpty(input.getDescription()));
        procurementProjectType.getDescription().add(projectDescription);
    }

    private void addProcurementProjectTypeInformation(final CacProcurementProject input, final ProcurementProjectType procurementProjectType) {
        if (input.getType() == null) {
            return;
        }
        final ProcurementTypeCodeType procurementTypeCodeType = new ProcurementTypeCodeType();
        procurementTypeCodeType.setValue(input.getType().getCode());
        procurementTypeCodeType.setListID(input.getType().getListId());
        procurementTypeCodeType.setListVersionID(input.getType().getListVersionId());
        procurementTypeCodeType.setListAgencyName(input.getType().getListAgencyName());
        procurementProjectType.setProcurementTypeCode(procurementTypeCodeType);
    }

    private void addProcurementProjectMainCommonFacilityInformation(final CacProcurementProject input, final ProcurementProjectType procurementProjectType) {
        if (input.getCpv() == null || input.getCpv().size() == 0) {
            return;
        }

        final Collection<CommodityClassificationType> mainCommodities = input.getCpv().stream()
                .map(cpv -> {
                    final CommodityClassificationType commodityClassificationType = new CommodityClassificationType();
                    final ItemClassificationCodeType itemClassificationCodeType = new ItemClassificationCodeType();
                    itemClassificationCodeType.setValue(cpv.getCode());
                    itemClassificationCodeType.setListID(cpv.getListId());
                    itemClassificationCodeType.setListVersionID(cpv.getListVersionId());
                    itemClassificationCodeType.setListAgencyName(cpv.getListAgencyName());
                    commodityClassificationType.setItemClassificationCode(itemClassificationCodeType);
                    return commodityClassificationType;
                })
                .collect(Collectors.toList());

        procurementProjectType.getMainCommodityClassification().addAll(mainCommodities);
    }

}
