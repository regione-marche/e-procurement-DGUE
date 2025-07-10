package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

public class AdditionalDocumentReference {

	private String title;
    private String descr;
    private String tedUrl;
    private String tedReceptionId;
    private String nojcnNumber;
    private String docTypeCode;
    private String id;
    private String uuid;
    private String issueDate;
    private String issueTime;    
    
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getIssueDate() {
		return issueDate;
	}
	public void setIssueDate(String issueDate) {
		this.issueDate = issueDate;
	}
	public String getIssueTime() {
		return issueTime;
	}
	public void setIssueTime(String issueTime) {
		this.issueTime = issueTime;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescr() {
		return descr;
	}
	public void setDescr(String descr) {
		this.descr = descr;
	}
	public String getTedUrl() {
		return tedUrl;
	}
	public void setTedUrl(String tedUrl) {
		this.tedUrl = tedUrl;
	}
	public String getTedReceptionId() {
		return tedReceptionId;
	}
	public void setTedReceptionId(String tedReceptionId) {
		this.tedReceptionId = tedReceptionId;
	}
	public String getNojcnNumber() {
		return nojcnNumber;
	}
	public void setNojcnNumber(String nojcnNumber) {
		this.nojcnNumber = nojcnNumber;
	}
	public String getDocTypeCode() {
		return docTypeCode;
	}
	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}
    
    
}
