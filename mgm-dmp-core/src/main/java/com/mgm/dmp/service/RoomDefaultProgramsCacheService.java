package com.mgm.dmp.service;

import com.mgm.dmp.common.vo.RoomProgramsResponse;

public interface RoomDefaultProgramsCacheService {

    RoomProgramsResponse getTierProgramDetails(String propertyId, String tier);

    RoomProgramsResponse getDefaultProgramDetails(String propertyId);
    
    boolean isDefaultTierProgramId(String propertyId, String programId);
}
