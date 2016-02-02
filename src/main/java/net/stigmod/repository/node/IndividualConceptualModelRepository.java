/*
 * Copyright 2014-2016, Stigmergic-Modeling Project
 * SEIDR, Peking University
 * All rights reserved
 *
 * Stigmergic-Modeling is used for collaborative groups to create a conceptual model.
 * It is based on UML 2.0 class diagram specifications and stigmergy theory.
 */

package net.stigmod.repository.node;


import net.stigmod.domain.node.IndividualConceptualModel;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.GraphRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * ICM Repository
 *
 * @version     2016/02/03
 * @author 	    Shijun Wang
 */
@Repository
public interface IndividualConceptualModelRepository extends GraphRepository<IndividualConceptualModel> {

//    @Query("MATCH (user:User)-[r:OWNS]->(icm:ICM)" +
//            "WHERE id(user)={0} AND icm.name={1}" +
//            "RETURN icm")
//    IndividualConceptualModel findIndividualConceptualModel(long userId, String modelName);

    @Query( "MATCH (user:User)-[:OWNS]->(icm:ICM)" +
            "WHERE ID(user) = {userId}" +
            "RETURN icm")
    Set<IndividualConceptualModel> getAllIcmsOfUser(@Param("userId") Long userId);
}
