package it.maggioli.eldasoft.dgue.msdgueserver.core.domain.ubl;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 03, 2020
 */
public interface CacRequirementGroup  extends Serializable {

    String getId();

    Boolean getFulfillmentIndicator();
    
    String getPropertyGroupTypeCode();

    List<? extends CacCriterionRequirement> getRequirements();

    List<? extends CacRequirementGroup> getSubgroups();

	/**
	 * Certain criteria need to have a theoretically unlimited number of requirement groups. The meta definition of
	 * these groups starts from a primary one and then gets cloned as many times as needed.
	 *
	 * @return
	 */
	boolean isUnbounded();
	
	boolean isUnbounded2();
	
	boolean isUnboundedArray();
	
	String getName();
	
	boolean isArray();

}
