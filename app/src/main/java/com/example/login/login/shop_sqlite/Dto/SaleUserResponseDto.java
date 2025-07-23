package com.example.login.login.shop_sqlite.Dto;


 import java.util.Date;

public class SaleUserResponseDto {
    private int userId;
    private int roleId;
    private String email;
    private String password;
    private String phone;
    private String userName;

    private Date dateOfBirth;
    private String address;
    private Date createDate;
    private int status;
    private boolean isDelete;

    public SaleUserResponseDto() {
    }
    public SaleUserResponseDto(int userId, int roleId, String email, String password, String phone, String userName, Date dateOfBirth, String address, Date createDate, int status, boolean isDelete) {
        this.userId = userId;
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.createDate = createDate;
        this.status = status;
        this.isDelete = isDelete;
    }


    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isIsDelete() {
        return isDelete;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}