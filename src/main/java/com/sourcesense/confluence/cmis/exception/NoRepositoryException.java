package com.sourcesense.confluence.cmis.exception;

public class NoRepositoryException extends Exception {

    @Override
    public String getMessage() {

        return "No repository foud";
    }

}
