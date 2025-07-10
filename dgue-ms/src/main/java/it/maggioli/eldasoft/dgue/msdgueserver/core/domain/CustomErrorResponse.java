package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.time.LocalDateTime;

public class CustomErrorResponse {

	private LocalDateTime timestamp;
	private String error;
	private int status;
	
	public LocalDateTime getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	
}
