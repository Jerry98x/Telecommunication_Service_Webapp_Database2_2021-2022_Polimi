package it.polimi.db2_project_20212022_fontana_gerosa.beans.services;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity @Table(name = "mobile_internet")
public class MobileInternet extends Service{
    @Column(nullable = false)
    private int GBs;
    private int extraGBFee_euro;
}
