package com.freeloop.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Schema(
        description = "修改用户请求，所有字段均可选，但至少需要提供一个字段"
)
public class UserUpdateRequest {
    @Size(min = 2, max = 50, message = "用户名长度必须在 2 到 50 个字符之间")
    @Pattern(
            regexp = "^[A-Za-z][A-Za-z0-9_]*$",
            message = "用户名必须以字母开头，只能包含字母、数字和下划线"
    )
    @Schema(
            description = "新的登录用户名",
            example = "alice_new"
    )
    private String username;

    @Size(max = 50, message = "昵称长度不能超过 50 个字符")
    @Pattern(
            regexp = ".*\\S.*",
            message = "昵称不能为空或纯空白字符"
    )
    @Schema(
            description = "新的用户昵称",
            example = "Alice Updated"
    )
    private String nickname;

    @Pattern(
            regexp = "^1[3-9]\\d{9}$",
            message = "手机号必须是有效的 11 位中国大陆手机号"
    )
    @Schema(
            description = "新的中国大陆手机号",
            example = "13900139001"
    )
    private String phone;

    @Size(min = 1, max = 100, message = "邮箱长度必须在 1 到 100 个字符之间")
    @Email(message = "邮箱格式不正确")
    @Schema(
            description = "新的用户邮箱",
            example = "alice-new@example.com",
            format = "email"
    )
    private String email;

    @Min(value = 0, message = "状态只能是 0 或 1")
    @Max(value = 1, message = "状态只能是 0 或 1")
    @Schema(
            description = "用户状态：0 表示禁用，1 表示启用",
            example = "1",
            allowableValues = {"0", "1"}
    )
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

    @Schema(hidden = true)
    @AssertTrue(message = "至少提供一个需要修改的字段")
    public boolean isAnyFieldPresent() {
        return username != null
                || nickname != null
                || phone != null
                || email != null
                || status != null;
    }
}
