/**
 * 
 */
package com.mgm.dmp.dao;

import java.util.Map;
/** Added by MGM Support in R1.7 for MRIC-1735 **/

import com.mgm.dmp.common.model.ShowEvent;
import com.mgm.dmp.common.model.phoenix.TicketingProgramHoldValues;

public interface PhoenixTicketingProgramHoldValueDAO {
	Map<String, TicketingProgramHoldValues> getTicketingProgramsHoldIdAndName();
}
