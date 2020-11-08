package data;

public class UserInfoBean {
	private String requestValue;
	private String employeeCode;
	private String accessCode;
	private String userName;
	private String userPhone;
	private boolean userLevel; // true : manager   false : mate
	private String accessTime;
	
	
	public String getRequestValue() {
		return requestValue;
	}
	public void setRequestValue(String requestValue) {
		this.requestValue = requestValue;
	}
	public String getAccessTime() {
		return accessTime;
	}
	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}
	public String getEmployeeCode() {
		return employeeCode;
	}
	public void setEmployeeCode(String employeeCode) {
		this.employeeCode = employeeCode;
	}
	public String getAccessCode() {
		return accessCode;
	}
	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserPhone() {
		return userPhone;
	}
	public void setUserPhone(String userPhone) {
		this.userPhone = userPhone;
	}
	public boolean isUserLevel() {
		return userLevel;
	}
	public void setUserLevel(boolean userLevel) {
		this.userLevel = userLevel;
	}
}
