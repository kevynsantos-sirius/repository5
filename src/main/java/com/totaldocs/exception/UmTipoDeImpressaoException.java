package com.totaldocs.exception;

public class UmTipoDeImpressaoException extends Exception {
	
	public UmTipoDeImpressaoException()
	{
		super("Só é possivel ter um tipo de formatação / Impressão: Duplex ou simples");
	}

}
