/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hel.ut.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *
 * @author chadmccue
 */
@Entity
@Table(name = "CONFIGURATIONTRANSPORTMESSAGETYPES")
public class configurationTransportMessageTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "ID", nullable = false)
    private int id;

    @Column(name = "CONFIGTRANSPORTID", nullable = false)
    private int configTransportId;

    @Column(name = "CONFIGID", nullable = false)
    private int configId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getconfigId() {
        return configId;
    }

    public void setconfigId(int configId) {
        this.configId = configId;
    }

    public int getconfigTransportId() {
        return configTransportId;
    }

    public void setconfigTransportId(int configTransportId) {
        this.configTransportId = configTransportId;
    }

}
