package pt.ulisboa.tecnico.softeng.bank.domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import pt.ulisboa.tecnico.softeng.bank.dataobjects.BankOperationData;
import pt.ulisboa.tecnico.softeng.bank.exception.BankException;

public class Bank {
	public static Set<Bank> banks = new HashSet<>();

	public static final int CODE_SIZE = 4;

	private final String name;
	private final String code;
	private final Set<Account> accounts = new HashSet<>();
	private final Set<Client> clients = new HashSet<>();
	private final List<Operation> log = new ArrayList<>();

	public Bank(String name, String code) {
		checkArguments(name, code);

		this.name = name;
		this.code = code;

		Bank.banks.add(this);
	}

	private void checkArguments(String name, String code) {
		if (name == null || code == null || name.trim().equals("") || code.trim().equals("")) {
			throw new BankException();
		}

		if (code.length() != Bank.CODE_SIZE) {
			throw new BankException();
		}

		for (Bank bank : banks) {
			if (bank.getCode().equals(code)) {
				throw new BankException();
			}
		}
	}

	String getName() {
		return this.name;
	}

	String getCode() {
		return this.code;
	}

	int getNumberOfAccounts() {
		return this.accounts.size();
	}

	int getNumberOfClients() {
		return this.clients.size();
	}

	void addAccount(Account account) {
		this.accounts.add(account);
	}

	boolean hasClient(Client client) {
		return this.clients.contains(client);
	}

	void addClient(Client client) {
		this.clients.add(client);
	}

	void addLog(Operation operation) {
		this.log.add(operation);
	}

	public Account getAccount(String IBAN) {
		for (Account account : this.accounts) {
			if (account.getIBAN().equals(IBAN)) {
				return account;
			}
		}
		throw new BankException();
	}

	public Operation getOperation(String reference) {
		for (Operation operation : this.log) {
			if (operation.getReference().equals(reference)) {
				return operation;
			}
		}
		return null;
	}

	public static String processPayment(String IBAN, int amount) {
		for (Bank bank : Bank.banks) {
			if (bank.getAccount(IBAN) != null) {
				return bank.getAccount(IBAN).withdraw(amount);
			}
		}
		throw new BankException();
	}

	public static String cancelPayment(String reference) {
		
		if (reference == null || reference.trim().equals("")) throw new BankException();
		
		Operation opW = null;
		for (Bank bank : banks){
			opW = bank.getOperation(reference);
			if (!opW.equals(null)){
				break;
			}
		}
		
		if (opW.getType() == Operation.Type.DEPOSIT || opW.equals(null)) throw new BankException();
		
		Account acc = opW.getAccount();
		int result = opW.getValue();
		
		return acc.deposit(result);
	}

	public static BankOperationData getOperationData(String reference) {
		if (reference == null || reference.trim().equals("")) throw new BankException();
		Operation op = null;
		for (Bank bank : Bank.banks) {
			if (bank.getOperation(reference) != null) {
				op = bank.getOperation(reference);
				break;
			}
		}
		if (op == null) throw new BankException();
		
		BankOperationData bod = new BankOperationData();
		bod.setReference(reference);
		switch(op.getType()) {
		case DEPOSIT:
			bod.setType("Deposit");
			break;
		case WITHDRAW:
			bod.setType("Withdraw");
			break;
		}
		bod.setIban(op.getAccount().getIBAN());
		bod.setValue(op.getValue());
		bod.setTime(op.getTime());
		
		return bod;
	}

}
