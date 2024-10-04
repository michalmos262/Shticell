package engine.user.permission;

public enum UserPermission {
    OWNER,
    READER,
    WRITER,
    CANNOT_ACCESS;

    @Override
    public String toString() {
        return this.name();
    }
}