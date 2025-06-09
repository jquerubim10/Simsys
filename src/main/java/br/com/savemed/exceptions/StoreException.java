package br.com.savemed.exceptions;

public class StoreException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoreException(Exception ex) {
        super(ex);
    }

    public StoreException(String message) {
        super(message);
    }

}
