package br.com.stefanini.treinamento.boleto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

import br.com.stefanini.treinamento.exception.ManagerException;

public abstract class BloquetoBBImpl implements BloquetoBB {

	protected String codigoBanco;
	protected String codigoMoeda;
	protected String fatorVencimento;
	protected Date dataVencimento;
	protected Date dataBase;
	protected BigDecimal valor;
	protected String numeroConvenioBanco;
	protected String complementoNumeroConvenioBancoSemDV;
	protected String numeroAgenciaRelacionamento;
	protected String contaCorrenteRelacionamentoSemDV;
	protected String tipoCarteira;

	private int dvCodigoBarras;

	protected abstract void validaDados() throws ManagerException;

	/**
	 * Inicializa o fator de vencimento
	 */
	protected void setFatorVencimento() {

		long dias = diferencaEmDias(dataBase, dataVencimento);

		// TODO: EXPLICAR O QUE ESTE M�TODO EST� FAZENDO

		fatorVencimento = String.format("%04d", dias);

	}

	/**
	 * Inicializa os valores, formata
	 */
	protected void init() {

		setFatorVencimento();

	}

	/**
	 * Retorna o valor formatado do boleto banc�rio
	 * 
	 * @return
	 */
	protected String getValorFormatado() {
		/* Este metodo pega o valor decimal e arredonda para 2 decimais,
		 * para ter no final da linha digitavel
		 * 8 inteiros e 2 decimais, no valor do boleto e no codigo de barras.
		 */
		
		// TODO: Explicar o que este m�todo est� fazendo
		return String.format(
				"%010d",
				Long.valueOf(valor.setScale(2, RoundingMode.HALF_UP).toString()
						.replace(".", "")));
	}

	/**
	 * Formata o n�mero do conv�nio da Linha Digit�vel
	 * 
	 * @return
	 */
	protected abstract String getLDNumeroConvenio();

	/**
	 * Retorna o c�digo de barras do Bloqueto
	 * 
	 * @return c�digo de barras
	 */
	protected abstract String getCodigoBarrasSemDigito();

	public abstract String getCodigoBarras();

	/**
	 * Campo 5 da Linha Digit�vel
	 * 
	 * @return
	 */
	private String ldCampo5() {
		// TODO: COMPLETAR
		return "";
	}

	/**
	 * Campo 4 da Linha Digit�vel
	 * 
	 * @return
	 */
	private String ldCampo4() {
		// TODO: COMPLETAR
		return "";
	}

	/**
	 * Campo 3 da Linha Digit�vel
	 * 
	 * @return
	 */
	private String ldCampo3() {

		// TODO: COMPLETAR
		return "";
	}

	/**
	 * Campo 2 da Linha Digit�vel
	 * 
	 * @return
	 */
	private String ldCampo2() {
		// TODO: COMPLETAR

		return "";
	}

	/**
	 * Calcula o digito verificador do campo
	 * 
	 * @param campo
	 * @return
	 */
	protected int digitoVerificadorPorCampo(String campo, boolean valor) {
		// TODO: COMPLETAR

		return 0;
	}

	/**
	 * Calcula o digito verificado do c�digo de barras
	 * 
	 * @param codigoBarras
	 * @return
	 */
	protected int digitoVerificadorCodigoBarras(String codigoBarras) {
		// TODO: COMPLETAR
		return 0;
	}

	/**
	 * Campo 1 da Linha Digit�vel
	 * 
	 * - C�digo do Banco - C�digo da Moeda - N�mero do conv�nio
	 * 
	 * @return
	 */
	private String ldCampo1() {
		StringBuilder buffer = new StringBuilder();
		// TODO: COMPLETAR
		return buffer.toString();

	}

	public String getLinhaDigitavel() {

		init();

		StringBuilder buffer = new StringBuilder();
		// TODO: COMPLETAR

		return buffer.toString();
	}

	/**
	 * Retorna a diferen�a em dias de duas datas
	 * 
	 * @param dataInicial
	 *            Data inicial
	 * @param dataFinal
	 *            Data final
	 * @return
	 */
	protected static long diferencaEmDias(Date dataInicial, Date dataFinal) {

		/*Pega (data final - data inicial) que divide pelo numero
		 * 86400000D que seria 1 dia. E descobre quantos dias ter�.
		*/

		return Math
				.round((dataFinal.getTime() - dataInicial.getTime()) / 86400000D);
	}

	public int getDvCodigoBarras() {

		getCodigoBarras();

		return dvCodigoBarras;
	}
}
