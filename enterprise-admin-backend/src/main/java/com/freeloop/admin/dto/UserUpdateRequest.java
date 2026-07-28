package com.freeloop.admin.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateRequest {
    @Size(min = 2, max = 50, message = "用户名长度必须在 2 到 50 个字符之间")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "用户名必须以字母开头，只能包含字母、数字和下划线"
    )
    private String username;

    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    @Pattern(
            regexp = ".*\\S.*",
            message = "昵称不能为空或纯空白字符"
    )
    private String nickname;

    @Pattern(
            regexp = "^1[3-9]\\d{9}$",
            message = "手机号必须是有效的 11 位中国大陆手机号"
    )
    private String phone;

    @Size(min = 1, max = 100, message = "邮箱长度必须在 1 到 100 个字符之间")
    @Email(message = "邮箱格式不正确")
    private String email;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    private Integer status;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @AssertTrue(message = "至少提供一个需要修改的字段")
    public boolean isAnyFieldPresent() {
        return username != null
                || nickname != null
                || phone != null
                || email != null
                || status != null;
    }
}
