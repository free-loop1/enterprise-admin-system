package com.freeloop.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(description = "公开注册请求")
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 50, message = "用户名长度必须在 2 到 50 个字符之间")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "用户名必须以字母开头，只能包含字母、数字和下划线"
    )
    @Schema(
            description = "登录用户名，必须以字母开头，只能包含字母、数字和下划线",
            example = "alice",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, max = 64, message = "密码长度必须在 8 到 64 个字符之间")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[^A-Za-z0-9\\s])\\S+$",
            message = "密码必须包含字母、数字和特殊字符，且不能包含空白字符"
    )
    @Schema(
            description = "登录密码，必须包含字母、数字和特殊字符",
            example = "Test@123456",
            format = "password",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    @Schema(
            description = "用户昵称",
            example = "Alice Chen",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String nickname;

    @NotBlank(message = "手机号不能为空")
    @Pattern(
            regexp = "^1[3-9]\\d{9}$",
            message = "手机号必须是有效的 11 位中国大陆手机号"
    )
    @Schema(
            description = "中国大陆手机号",
            example = "13800138000",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Size(min = 1, max = 100, message = "邮箱长度必须在 1 到 100 个字符之间")
    @Email(message = "邮箱格式不正确")
    @Schema(
            description = "用户邮箱",
            example = "alice@example.com",
            format = "email",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
