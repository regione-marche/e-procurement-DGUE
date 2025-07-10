package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.List;

import javax.validation.constraints.NotEmpty;
import org.springframework.web.multipart.MultipartFile;
import javax.validation.constraints.NotNull;

public class ImportedXml {
	
	@NotEmpty
	@NotNull
	private byte[] xml;
	
	@NotEmpty
	@NotNull
	private List<MultipartFile> attachments;
	
	@NotEmpty
	@NotNull
	private String whoIs;
	
	private String base64Xml;
	
	public byte[] getXml() {
		return xml;
	}
	public void setXml(byte[] xml) {
		this.xml = xml;
	}
	public String getWhoIs() {
		return whoIs;
	}
	public void setWhoIs(String whoIs) {
		this.whoIs = whoIs;
	}
	public List<MultipartFile> getAttachments() {
		return attachments;
	}
	public void setAttachments(List<MultipartFile> attachments) {
		this.attachments = attachments;
	}
	public String getBase64Xml() {
		return base64Xml;
	}
	public void setBase64Xml(String base64Xml) {
		this.base64Xml = base64Xml;
	}
	
	
}
