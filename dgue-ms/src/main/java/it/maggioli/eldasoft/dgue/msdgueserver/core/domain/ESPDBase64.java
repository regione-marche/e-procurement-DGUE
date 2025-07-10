package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import javax.validation.constraints.NotEmpty;

import com.sun.istack.NotNull;

public class ESPDBase64 {

	@NotNull
	@NotEmpty
	private String nameFile;
	@NotNull
	@NotEmpty
	private String base64Xml;
	public String getNameFile() {
		return nameFile;
	}
	public void setNameFile(String nameFile) {
		this.nameFile = nameFile;
	}
	public String getBase64Xml() {
		return base64Xml;
	}
	public void setBase64Xml(String base64Xml) {
		this.base64Xml = base64Xml;
	}
	
	
}
