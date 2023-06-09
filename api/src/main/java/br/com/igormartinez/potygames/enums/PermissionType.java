package br.com.igormartinez.potygames.enums;

public enum PermissionType {
    ADMIN("ADMIN"),
    CUSTOMER("CUSTOMER");

    private String value;

    PermissionType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
