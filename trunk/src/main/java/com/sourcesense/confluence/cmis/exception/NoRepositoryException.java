package com.sourcesense.confluence.cmis.exception;

public class NoRepositoryException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = -8817285741911970151L;

    @Override
    public String getMessage() {

        return "No repository foud";
    }

}
