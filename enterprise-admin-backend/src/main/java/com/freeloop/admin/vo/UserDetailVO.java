package com.freeloop.admin.vo;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        description = "用户详情响应，仅包含可公开展示的字段，不包含密码和逻辑删除标记"
)
public class UserDetailVO {
    @Schema(
            description = "用户 ID",
            example = "1"
    )
    private Long id;
    @Schema(
            description = "登录用户名",
            example = "alice"
    )
    private String username;
    @Schema(
            description = "用户昵称",
            example = "Alice Chen"
    )
    private String nickname;
    @Schema(
            description = "手机号",
            example = "13800138000"
    )
    private String phone;
    @Schema(
            description = "邮箱",
            example = "alice@example.com",
            format = "email"
    )
    private String email;
    @Schema(
            description = "用户状态：0 表示禁用，1 表示启用",
            example = "1",
            allowableValues = {"0", "1"}
    )
    private Integer status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
}
