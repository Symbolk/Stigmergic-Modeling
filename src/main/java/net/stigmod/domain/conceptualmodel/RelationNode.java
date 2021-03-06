/*
 * Copyright 2014-2016, Stigmergic-Modeling Project
 * SEIDR, Peking University
 * All rights reserved
 *
 * Stigmergic-Modeling is used for collaborative groups to create a conceptual model.
 * It is based on UML 2.0 class diagram specifications and stigmergy theory.
 */


package net.stigmod.domain.conceptualmodel;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @author Kai Fu
 * @author Shijun Wang
 * @version 2015/11/10
 */

@NodeEntity(label = "Relationship")
public class RelationNode extends AbstractVertex {

//    private double orgEntropyValue;//表示没有乘用户数之前的节点熵值
    private double biEntropyValue;//这个表示没有乘上节点数前的节点熵值
    private double postBiEntropyValue;
    private boolean isInitEntropy;//起到判断当前熵值是否是初始熵值得作用
    private int loc;
    private Long ccmId;

    public RelationNode() {
        super();
//        this.orgEntropyValue = 0;
        this.biEntropyValue = 0;
        this.postBiEntropyValue = 0;
        this.isInitEntropy = true;
    }

    public RelationNode(Long ccmId, Long icmId) {
        this();
        this.ccmId = ccmId;
        this.icmSet.add(icmId.toString());
    }

    public RelationNode(RelationNode relationNode) {
        this.id=relationNode.getId();
        this.setIcmSet(relationNode.getIcmSet());
        this.biEntropyValue=relationNode.getBiEntropyValue();
        this.postBiEntropyValue = relationNode.getPostBiEntropyValue();
//        this.orgEntropyValue = relationNode.getOrgEntropyValue();
        this.ccmId =relationNode.getCcmId();
        this.rtcEdges=new HashSet<>(relationNode.getRtcEdges());
        this.rtvEdges=new HashSet<>(relationNode.getRtvEdges());
        this.setIsInitEntropy(relationNode.isInitEntropy());
    }

    @Relationship(type="E_CLASS",direction = Relationship.OUTGOING)
    private Set<RelationToClassEdge> rtcEdges =new HashSet<>();

    @Relationship(type="PROPERTY",direction = Relationship.OUTGOING)
    private Set<RelationToValueEdge> rtvEdges =new HashSet<>();

    // 添加 relationship->class 边
    public void addR2CEdge(RelationToClassEdge r2cEdge) {
        this.rtcEdges.add(r2cEdge);
    }

    // 添加 relationship->value 边
    public void addR2VEdge(RelationToValueEdge r2vEdge) {
        this.rtvEdges.add(r2vEdge);
    }

    /**
     * 如果该点是 ICM 中的孤立点，则将该点从 ICM 中删除
     * @param icmId icmId
     * @return 是否是孤立点
     */
    public boolean removeIcmIdIfNoEdgeAttachedInIcm(Long icmId) {

        for (RelationToClassEdge r2cEdge : this.getRtcEdges()) {
            if (r2cEdge.getIcmSet().contains(icmId)) {
                return false;  // 有边中含有 icmId，此节点暂时不能从 ICM 中删除
            }
        }
        for (RelationToValueEdge r2vEdge : this.getRtvEdges()) {
            if (r2vEdge.getIcmSet().contains(icmId)) {
                return false;  // 有边中含有 icmId，此节点暂时不能从 ICM 中删除
            }
        }

        // 此节点已经是 ICM 中的孤立点，删除
        this.removeIcmId(icmId);
        return true;
    }

//    public void UpdateRelationNode(RelationNode relationNode) {
//        this.icmSet =relationNode.getIcmSet();
//        this.biEntropyValue=relationNode.getBiEntropyValue();
//        this.orgEntropyValue = relationNode.getOrgEntropyValue();
//        this.rtcEdges=relationNode.getRtcEdges();
//        this.rtvEdges=relationNode.getRtvEdges();
//    }

    public Set<RelationToClassEdge> getRtcEdges() {
        Iterator it = this.rtcEdges.iterator();
        while (it.hasNext()) {  // 剔除错误类型的边，应对 SDN4 在 findOne() 时的一个 BUG
            Object edge = it.next();
            if (!(edge instanceof RelationToClassEdge)) {
                if (edge instanceof RelationToValueEdge) {
                    Boolean addSucceed = rtvEdges.add((RelationToValueEdge) edge);
                    System.out.println("@@ SDN relationship bug solved. [RelationNode: R2C Edge] (Add to another edge set : " + addSucceed.toString() + ")");
                }
                it.remove();
            }
        }
        return rtcEdges;
    }

    public Set<RelationToValueEdge> getRtvEdges() {
        Iterator it = this.rtvEdges.iterator();
        while (it.hasNext()) {  // 剔除错误类型的边，应对 SDN4 在 findOne() 时的一个 BUG
            Object edge = it.next();
            if (!(edge instanceof RelationToValueEdge)) {
                if (edge instanceof RelationToClassEdge) {
                    Boolean addSucceed = rtcEdges.add((RelationToClassEdge) edge);
                    System.out.println("@@ SDN relationship bug solved. [RelationNode: R2V Edge] (Add to another edge set : " + addSucceed.toString() + ")");
                }
                it.remove();
            }
        }
        return rtvEdges;
    }

    public double getBiEntropyValue() {
        return biEntropyValue;
    }

    public void setBiEntropyValue(double biEntropyValue) {
        this.biEntropyValue = biEntropyValue;
    }

    public Long getCcmId() {
        return ccmId;
    }

    public void setCcmId(Long ccmId) {
        this.ccmId = ccmId;
    }

    public int getLoc() {
        return loc;
    }

    public void setLoc(int loc) {
        this.loc = loc;
    }

    public boolean isInitEntropy() {
        return isInitEntropy;
    }

    public void setIsInitEntropy(boolean isInitEntropy) {
        this.isInitEntropy = isInitEntropy;
    }

    public double getPostBiEntropyValue() {
        return postBiEntropyValue;
    }

    public void setPostBiEntropyValue(double postBiEntropyValue) {
        this.postBiEntropyValue = postBiEntropyValue;
    }

    public void setRtcEdges(Set<RelationToClassEdge> rtcEdges) {
        this.rtcEdges = rtcEdges;
    }

    public void setRtvEdges(Set<RelationToValueEdge> rtvEdges) {
        this.rtvEdges = rtvEdges;
    }

    //    public double getOrgEntropyValue() {
//        return orgEntropyValue;
//    }
//
//    public void setOrgEntropyValue(double orgEntropyValue) {
//        this.orgEntropyValue = orgEntropyValue;
//    }
}
