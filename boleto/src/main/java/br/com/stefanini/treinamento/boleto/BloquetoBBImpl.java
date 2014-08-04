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
		fatorVencimento = String.format("%04d", dias);

	}

	/**
	 * Inicializa os valores, formata
	 */
	protected void init() {

		setFatorVencimento();

	}

	/**
	 * Retorna o valor formatado do boleto bancário
	 * 
	 * @return
	 */
	protected String getValorFormatado() {
		/*
		 * Este metodo pega o valor decimal e arredonda para 2 decimais, para
		 * ter no final da linha digitavel 8 inteiros e 2 decimais, no valor do
		 * boleto e no codigo de barras.
		 */
		return String.format(
				"%010d",
				Long.valueOf(valor.setScale(2, RoundingMode.HALF_UP).toString()
						.replace(".", "")));
	}

	/**
	 * Formata o número do convênio da Linha Digitável
	 * 
	 * @return
	 */
	protected abstract String getLDNumeroConvenio();

	/**
	 * Retorna o código de barras do Bloqueto
	 * 
	 * @return código de barras
	 */
	protected abstract String getCodigoBarrasSemDigito();

	public abstract String getCodigoBarras();

	/**
	 * Campo 5 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo5() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(fatorVencimento);
		buffer.append(getValorFormatado());
		return buffer.toString();
	}

	/**
	 * Campo 4 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo4() {
		return String
				.valueOf(digitoVerificadorCodigoBarras(getCodigoBarrasSemDigito()));
	}

	/**
	 * Campo 3 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo3() {
		return String.format("%s.%s", getCodigoBarras().substring(34, 39), // FAZER
																			// EM
																			// CASA,
																			// JUNTAR
																			// AS
																			// DUAS...
				getCodigoBarras().substring(39, 44));
	}

	/**
	 * Campo 2 da Linha Digitável
	 * 
	 * @return
	 */
	private String ldCampo2() {
		return String.format("%s.%s", getCodigoBarras().substring(24, 29), // FAZER
																			// EM
																			// CASA,
																			// JUNTAR
																			// AS
																			// DUAS...
				getCodigoBarras().substring(29, 34));
	}

	/**
	 * Calcula o digito verificador do campo
	 * 
	 * @param campo
	 * @return
	 */
	protected int digitoVerificadorPorCampo(String campo, boolean valor) {
		String str = campo.replace(".", "");
		int soma = 0, val = 2, dv = 0;

		for (int i = str.length() - 1; i >= 0; i--) {

			dv = Integer.valueOf(str.substring(i, i + 1));

			dv = dv * val;
			
		// Soma a dezena > 9
			if (dv > 9) {
				dv = (dv / 10) + (dv % 10);
			}

		// Altera de 1 ou 2
			if (val == 2) {
				val = 1;
			} else
				val = 2;

			soma += dv;
		}
		
		// Pega o resto da soma e divide por 5
		dv = (((soma / 5) + 1) * 5) - soma;

		if (dv == 10) {
			return 0;
		} else {
			return dv;
		}

	}

	/**
	 * Calcula o digito verificado do código de barras
	 * 
	 * @param codigoBarras
	 * @return
	 */
	protected int digitoVerificadorCodigoBarras(String codigoBarras) {
		int soma = 0, dv = 2;
		int tamanho = codigoBarras.length();
		for (int i = tamanho - 1; i >= 0; i--) {
			if (dv == 10) {
				dv = 2;
			}
			soma += Integer.valueOf(codigoBarras.substring(i, i + 1)) * dv;
			dv++;
		}
		int resto = soma % 11;

		dv = 11 - resto;
		if (dv == 0 || dv == 10 || dv == 11) {
			return 1;
		} else {
			return dv;
		}
	}

	/**
	 * Campo 1 da Linha Digitável
	 * 
	 * - Código do Banco - Código da Moeda - Número do convênio
	 * 
	 * @return
	 */
	private String ldCampo1() {
		StringBuilder buffer = new StringBuilder();
		buffer.append(codigoBanco);
		buffer.append(codigoMoeda);
		buffer.append(getLDNumeroConvenio());
		return buffer.toString();

	}

	public String getLinhaDigitavel() {

		init();

		StringBuilder buffer = new StringBuilder();
		buffer.append(ldCampo1());
		buffer.append(digitoVerificadorPorCampo(ldCampo1(), true));
		buffer.append(" ");

		buffer.append(ldCampo2());
		buffer.append(digitoVerificadorPorCampo(ldCampo2(), false));
		buffer.append(" ");
		buffer.append(ldCampo3());
		buffer.append(digitoVerificadorPorCampo(ldCampo3(), false));
		buffer.append(" ");

		buffer.append(ldCampo4());
		buffer.append(" ");
		buffer.append(ldCampo5());

		return buffer.toString();
	}

	/**
	 * Retorna a diferença em dias de duas datas
	 * 
	 * @param dataInicial
	 *            Data inicial
	 * @param dataFinal
	 *            Data final
	 * @return
	 */
	protected static long diferencaEmDias(Date dataInicial, Date dataFinal) {

		/*
		 * Pega (data final - data inicial) que divide pelo numero 86400000D que
		 * seria 1 dia. E descobre quantos dias terá.
		 */

		return Math
				.round((dataFinal.getTime() - dataInicial.getTime()) / 86400000D);
	}

	public int getDvCodigoBarras() {

		getCodigoBarras();

		return dvCodigoBarras;
	}
}
