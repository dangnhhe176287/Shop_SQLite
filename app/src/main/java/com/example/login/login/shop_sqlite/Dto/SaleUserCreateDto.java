package com.example.login.login.shop_sqlite.Dto;

import java.util.Date; // Để xử lý DateTime từ C#

public class SaleUserCreateDto {
    private int roleId;
    private String email;
    private String password;
    private String phone;
    private String userName;
    private Date dateOfBirth; // Sử dụng java.util.Date
    private String address;
    // Bỏ qua CreateDate ở đây nếu backend tự động tạo nó khi POST
    // private Date createDate; // <-- Bỏ hoặc comment dòng này

    private int status;
    private boolean isDelete;

    // Constructor đã điều chỉnh để không bao gồm createDate
    public SaleUserCreateDto(int roleId, String email, String password, String phone,
                             String userName, Date dateOfBirth, String address,
                             int status, boolean isDelete) {
        this.roleId = roleId;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.userName = userName;
        this.dateOfBirth = dateOfBirth;
        this.address = address;
        this.status = status;
        this.isDelete = isDelete;
    }

    // Getters cho tất cả các trường (cần thiết cho Gson để serialize)
    public int getRoleId() {
        return roleId;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserName() {
        return userName;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public String getAddress() {
        return address;
    }

    // Phương thức này sẽ không tồn tại nếu bạn không gửi createDate
    // public Date getCreateDate() {
    //     return createDate;
    // }

    public int getStatus() {
        return status;
    }

    // Getter cho boolean thường là is<FieldName> hoặc get<FieldName>
    public boolean getIsDelete() {
        return isDelete;
    }

    // Setters (tùy chọn, chỉ cần nếu bạn thay đổi giá trị sau khi tạo đối tượng)
    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // public void setCreateDate(Date createDate) {
    //     this.createDate = createDate;
    // }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setIsDelete(boolean isDelete) {
        this.isDelete = isDelete;
    }
}