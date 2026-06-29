package model;

public class WorkspaceNotEmptyException extends RuntimeException {
    public WorkspaceNotEmptyException(String message) {
        super(message);
    }
}