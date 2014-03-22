package com.futurice.festapp.domain.to;

public class HTTPBackendResponse {
	
	private String content;
	private boolean valid;
	private String eTag;
	
	public HTTPBackendResponse() {
		
	}
	
	public HTTPBackendResponse(String content, boolean valid, String eTag) {
		this.content = content;
		this.valid = valid;
		this.eTag = eTag;
	}

	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getEtag() {
		return eTag;
	}
	public void setEtag(String eTag) {
		this.eTag = eTag;
	}
	
	
	

}
