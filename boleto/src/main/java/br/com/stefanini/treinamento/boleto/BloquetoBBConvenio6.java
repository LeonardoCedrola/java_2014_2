/*
 * Implementação do Bloqueto de Cobranças do Banco do Brasil
 * - Convênio com 6 posições
 * 
 */
package br.com.stefanini.treinamento.boleto;

import java.math.BigDecimal;
import java.util.Date;

import br.com.stefanini.treinamento.exception.ManagerException;

public class BloquetoBBConvenio6 extends BloquetoBBImpl implements BloquetoBB {

	@Override
	protected void validaDados() throws ManagerException {

		if (codigoBanco == null || codigoBanco.length() != 3) {
			throw new ManagerException(
					"Código do Banco não informado. O Codigo deve ter 3 posições");
		}

		if (codigoMoeda == null || codigoMoeda.length() != 1) {
			throw new ManagerException(
					"Código de moeda não informado ou inválido");
		}

		if (dataVencimento == null) {
			throw new ManagerException("Data de vencimento não informada");
		}

		if (valor == null) {
			throw new ManagerException(
					"Valor do bloqueto bancário não informado");
		}

		if (numeroConvenioBanco == null || numeroConvenioBanco.length() != 6) {
			throw new ManagerException(
					"número de convênio não informado ou o convênio informado é inválido. O convênio deve ter 6 posições");
		}

		if (complementoNumeroConvenioBancoSemDV == null
				&& complementoNumeroConvenioBancoSemDV.length() != 5) {
			throw new ManagerException(
					"Complemento do número do convênio não informado. O complemento deve ter 5 posições");
		}

		if (numeroAgenciaRelacionamento == null
				|| numeroAgenciaRelacionamento.length() != 4) {
			throw new ManagerException(
					"número da agência de Relacionamento não informado. O número da agência deve ter 4 posições");
		}

		if (contaCorrenteRelacionamentoSemDV == null
				|| contaCorrenteRelacionamentoSemDV.length() != 8) {
			throw new ManagerException(
					"Conta corrente de relacionamento não informada. O número da conta deve ter 8 posições");
		}

		if (tipoCarteira == null || tipoCarteira.length() != 2) {
			throw new ManagerException(
					"Tipo carteira não informado ou o valor é inválido");
		}

		if ("21".equals(tipoCarteira)
				&& (complementoNumeroConvenioBancoSemDV.length()) != 17) {
			throw new ManagerException(
					"Número do Convênio do Banco + Complemento do número do Convenio deve ter 17 posições para o tipo convênio igual a 21");
		}

		if (!"21".equals(tipoCarteira)
				&& (complementoNumeroConvenioBancoSemDV.length() + numeroConvenioBanco
						.length()) != 11) {
			throw new ManagerException(
					"Número do Convênio do Banco + Complemento do número do Convênio deve ter 11 posições para o tipo convênio igual diferente de 21");
		}

		if (dataBase == null) {
			throw new ManagerException("A database não foi informada.");
		}

	}

	public BloquetoBBConvenio6(String codigoBanco, String codigoMoeda,
			Date dataVencimento, Date dataBase, BigDecimal valor,
			String numeroConvenioBanco,
			String complementoNumeroConvenioBancoSemDV,
			String numeroAgenciaRelacionamento,
			String contaCorrenteRelacionamentoSemDV, String tipoCarteira)
			throws ManagerException {

		this.codigoBanco = codigoBanco;
		this.codigoMoeda = codigoMoeda;
		this.dataVencimento = dataVencimento;
		this.valor = valor;
		this.numeroConvenioBanco = numeroConvenioBanco;
		this.complementoNumeroConvenioBancoSemDV = complementoNumeroConvenioBancoSemDV;
		this.numeroAgenciaRelacionamento = numeroAgenciaRelacionamento;
		this.contaCorrenteRelacionamentoSemDV = contaCorrenteRelacionamentoSemDV;
		this.tipoCarteira = tipoCarteira;
		this.dataBase = dataBase;

		validaDados();

	}

	@Override
	protected String getLDNumeroConvenio() {

		String convenio = String.format("%06d",
				Long.valueOf(numeroConvenioBanco));
		return String.format("%s.%s", convenio.substring(0, 1),
				convenio.substring(1, 5));

	}

	@Override
	protected String getCodigoBarrasSemDigito() {

		init();

		StringBuilder buffer = new StringBuilder();
		buffer.append(codigoBanco);
		buffer.append(codigoMoeda);
		buffer.append(fatorVencimento);
		buffer.append(getValorFormatado());
		buffer.append(numeroConvenioBanco);
		buffer.append(complementoNumeroConvenioBancoSemDV);

		if (!"21".equals(tipoCarteira)) {
			buffer.append(numeroAgenciaRelacionamento);
			buffer.append(contaCorrenteRelacionamentoSemDV);
		}
		
		buffer.append(tipoCarteira);

		return buffer.toString();
	}

	@Override
	public String getCodigoBarras() {

		init();

		StringBuilder buffer = new StringBuilder();
		buffer.append(codigoBanco); // Campo 01-03 (03)
		buffer.append(codigoMoeda); // Campo 04-04 (01)
		buffer.append(digitoVerificadorCodigoBarras(getCodigoBarrasSemDigito())); //Campo

		buffer.append(fatorVencimento); // Campo 06-09 (01)
		buffer.append(getValorFormatado()); // Campo 10-19 (10)
		buffer.append(numeroConvenioBanco); // Campo 20-23 (06)
		buffer.append(complementoNumeroConvenioBancoSemDV); // Campo 24-30 (17)
		
		if(!"21".equals(tipoCarteira)){
		buffer.append(numeroAgenciaRelacionamento); // Campo 31-34 (04)
		buffer.append(contaCorrenteRelacionamentoSemDV); // Campo 35-42 (08)
		}
		buffer.append(tipoCarteira); // Campo 43-44 (02)

		return buffer.toString();
	}

}
