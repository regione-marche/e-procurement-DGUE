package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

public class ESPDInfo {
	
	private boolean owner;
	private String id;
	private String espdName;
	private String economicOperatorName;	
	private String economicOperatorCode;
	private String economicOperatorCF;
	private String economicOperatorRole;
	private String RTIGroupName;
	private String RTIPartName;
	private String setConsorzioInfo;
	private Boolean isConsorzio;
	private Boolean isGruppo;
	private boolean exclusionCriterion;
	private List<OtherOe> otherOe;
		
	public boolean isOwner() {
		return owner;
	}
	public void setOwner(boolean owner) {
		this.owner = owner;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEspdName() {
		return espdName;
	}
	public void setEspdName(String espdName) {
		this.espdName = espdName;
	}
	public String getEconomicOperatorName() {
		return economicOperatorName;
	}
	public void setEconomicOperatorName(String economicOperatorName) {
		this.economicOperatorName = economicOperatorName;
	}
	public String getEconomicOperatorCode() {
		return economicOperatorCode;
	}
	public void setEconomicOperatorCode(String economicOperatorCode) {
		this.economicOperatorCode = economicOperatorCode;
	}
	public String getEconomicOperatorRole() {
		return economicOperatorRole;
	}
	public void setEconomicOperatorRole(String economicOperatorRole) {
		this.economicOperatorRole = economicOperatorRole;
	}
	public boolean isExclusionCriterion() {
		return exclusionCriterion;
	}
	public void setExclusionCriterion(boolean exclusionCriterion) {
		this.exclusionCriterion = exclusionCriterion;
	}
	public List<OtherOe> getOtherOe() {
		return otherOe;
	}
	public void setOtherOe(List<OtherOe> otherOe) {
		this.otherOe = otherOe;
	}
	public String getEconomicOperatorCF() {
		return economicOperatorCF;
	}
	public void setEconomicOperatorCF(String economicOperatorCF) {
		this.economicOperatorCF = economicOperatorCF;
	}
	public String getRTIGroupName() {
		return RTIGroupName;
	}
	public void setRTIGroupName(String rTIGroupName) {
		RTIGroupName = rTIGroupName;
	}
	public String getRTIPartName() {
		return RTIPartName;
	}
	public void setRTIPartName(String rTIPartName) {
		RTIPartName = rTIPartName;
	}
	public Boolean getIsConsorzio() {
		return isConsorzio;
	}
	public void setIsConsorzio(Boolean isConsorzio) {
		this.isConsorzio = isConsorzio;
	}
	public Boolean getIsGruppo() {
		return isGruppo;
	}
	public void setIsGruppo(Boolean isGruppo) {
		this.isGruppo = isGruppo;
	}
	public String getSetConsorzioInfo() {
		return setConsorzioInfo;
	}
	public void setSetConsorzioInfo(String setConsorzioInfo) {
		this.setConsorzioInfo = setConsorzioInfo;
	}	
	
	
}
