package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

public class ESPDInfoResponse {
	
	public static final String ERRORE_LETTURA  = "Si è verificato un errore durante la traduzione del file XML";
	public static final String ERRORE_UNMARSHALLING  ="Il file non può essere tradotto";
	private ESPDInfo info;
	private CustomErrorResponse error;
	public ESPDInfo getInfo() {
		return info;
	}
	public void setInfo(ESPDInfo info) {
		this.info = info;
	}
	public CustomErrorResponse getError() {
		return error;
	}
	public void setError(CustomErrorResponse error) {
		this.error = error;
	}
	
	
}
