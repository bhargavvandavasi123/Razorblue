package com.enappsys.pageinfo.controller.aggregates;

import java.util.*;


import com.enappsys.data.core.aggregatecalculation.configuration.Configuration;
import com.enappsys.data.core.aggregatecalculation.configuration.Configurations;
import com.enappsys.data.core.datatype.DataType;
import com.enappsys.data.core.entityitem.EntityItem;


public class AggregateProcessor {

	
	private Map<String, List<String>> entitiesMap;
	private Configurations configurations;
	
	public AggregateProcessor(Configurations configurations) {
		this.configurations = configurations;
		
//---------For Phase 6 ---------------------------------------------------------
//		entitiesMap = new HashMap<>();
//		for (Configuration configuration : configurations.getConfigurations()) {
//            String datatype = configuration.getType();
//            String entity = configuration.getName();
////            System.out.println("datatype: "+datatype+" entity: "+entity);
//            if (!entitiesMap.containsKey(datatype)) {
//                entitiesMap.put(datatype, new ArrayList<>());
//            }
//            entitiesMap.get(datatype).add(entity);
//        }
//----------For Phase 6 ----------------------------------------------------------	
		
		
	}
	
	public String getAggregateDetails(DataType type, String name) {
		
		
		for (Configuration configuration : configurations.getConfigurations()) {
            if (configuration.getType().equalsIgnoreCase(type.getCode()) && configuration.getName().equalsIgnoreCase(name)) {
                return "Aggregate";
            }
        }
        return null;
		
//--------For Phase 6 -------------------------------------------------------------------
//		List<String> entities = entitiesMap.get(type);
//		
//		if (entities == null) {
//            return null;
//        }
//		
//		for (String entity : entities) {
//            if (entity.equals(name)) {
//                return "Aggregate";
//            }
//        }
//		
//		
//		return null;
//--------For Phase 6 ---------------------------------------------------------------------
        
	}
}