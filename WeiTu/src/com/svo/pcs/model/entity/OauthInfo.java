package com.svo.pcs.model.entity;

public class OauthInfo {
	private String accessToken;
	private String userName;
	private String expiresIn;
	private String refreshToken;
	public OauthInfo(String accessToken, String userName, String expiresIn,
			String refreshToken) {
		super();
		this.accessToken = accessToken;
		this.userName = userName;
		this.expiresIn = expiresIn;
		this.refreshToken = refreshToken;
	}
	public OauthInfo() {
		super();
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getExpiresIn() {
		return expiresIn;
	}
	public void setExpiresIn(String expiresIn) {
		this.expiresIn = expiresIn;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
