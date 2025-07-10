package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.infrastructure;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterion;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacCriterionRequirement;
import it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl.CacRequirementGroup;
import org.springframework.util.CollectionUtils;

/**
 * Central Utility class for holding ESPD criteria, requirement groups and requirements meta information by using
 * the criterion UUID.
 * <p>
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 06, 2020
 */
public final class CriterionDefinitions {

    private static final CriterionDefinitions INSTANCE = new CriterionDefinitions();
    
    private static final CriterionDefinitions INSTANCE_TEMP = new CriterionDefinitions();

    static {
        // read the criteria only once to populate the enums
        INSTANCE.mergeCriteriaDefinitions(CriteriaDeserializer.parseJsonFile("exclusionCriteria.json"));
        INSTANCE.mergeCriteriaDefinitions(CriteriaDeserializer.parseJsonFile("selectionCriteria.json"));
        INSTANCE.mergeCriteriaDefinitions(CriteriaDeserializer.parseJsonFile("otherCriteria.json"));
    }

    private final Map<String, CacCriterion> criteria;

    private final Map<String, CacRequirementGroup> requirementGroups;

    private final Map<String, CacCriterionRequirement> requirements;

    CriterionDefinitions() {
        criteria = new HashMap<>();
        requirementGroups = new HashMap<>();
        requirements = new HashMap<>();
    }

    private void mergeCriteriaDefinitions(CriterionDefinitions toMergeWith) {
        criteria.putAll(toMergeWith.criteria);
        requirementGroups.putAll(toMergeWith.requirementGroups);
        requirements.putAll(toMergeWith.requirements);
    }

    void addCriterion(String uuid, CacCriterion criterion) {
        criteria.put(uuid, criterion);
        indexRequirementGroups(criterion);
    }

    private void indexRequirementGroups(CacCriterion crit) {
        if (CollectionUtils.isEmpty(crit.getGroups())) {
            return;
        }
        for (CacRequirementGroup group : crit.getGroups()) {
            indexRequirementGroup(group);
        }
    }

    private void indexRequirementGroup(CacRequirementGroup group) {
        addRequirementGroup(group.getId(), group);
        if (!CollectionUtils.isEmpty(group.getRequirements())) {
            for (CacCriterionRequirement requirement : group.getRequirements()) {
                addRequirement(requirement.getId(), requirement);
            }
        }
        if (CollectionUtils.isEmpty(group.getSubgroups())) {
            return;
        }
        for (CacRequirementGroup subGroup : group.getSubgroups()) {
            indexRequirementGroup(subGroup);
        }
    }

    private void addRequirementGroup(String uuid, CacRequirementGroup group) {
        requirementGroups.put(uuid, group);
    }

    /**
     * Add mappings so that when we lookup requirement groups with old ids we get the new requirement group definitions.
     *
     * @param newGroupId The id of the new requirement group which will replace all the old ones
     * @param oldIds The ids of the old requirement groups to be replaced(updated)
     */
    void addRequirementGroupMappings(String newGroupId, Collection<String> oldIds) {
        CacRequirementGroup newGroup = requirementGroups.get(newGroupId);
        if (newGroup != null && !CollectionUtils.isEmpty(oldIds)) {
            for (String oldId : oldIds) {
                addRequirementGroup(oldId, newGroup);
            }
        }
    }

    private void addRequirement(String uuid, CacCriterionRequirement requirement) {
        requirements.put(uuid, requirement);
    }

    /**
     * Add mappings so that when we lookup requirements with old ids we get the new requirement definitions.
     *
     * @param newRequirementId The id of the new requirement which will replace all the old ones
     * @param oldIds The ids of the old requirement to be replaced(updated)
     */
    void addRequirementMappings(String newRequirementId, Collection<String> oldIds) {
        CacCriterionRequirement newGroup = requirements.get(newRequirementId);
        if (newGroup != null && !CollectionUtils.isEmpty(oldIds)) {
            for (String oldId : oldIds) {
                addRequirement(oldId, newGroup);
            }
        }
    }

    public static Optional<CacCriterion> findCriterionById(String uuid) {
        return Optional.ofNullable(INSTANCE.criteria.get(uuid));
    }

    public static CacRequirementGroup findRequirementGroupById(String uuid) {
        return INSTANCE.requirementGroups.get(uuid);
    }
    
    public static CacRequirementGroup findRequirementGroupByIdCriterionAndId(String uuidCriterion, String uuidGroup) {
    	INSTANCE.criteria.get(uuidCriterion);    	
        return INSTANCE.requirementGroups.get(uuidCriterion);
    }

    public static Optional<CacCriterionRequirement> findRequirementById(String uuid) {
        return Optional.ofNullable(INSTANCE.requirements.get(uuid));
    }
}
