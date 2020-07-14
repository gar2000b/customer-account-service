package com.onlineinteract.workflow.domain.account;

public class Account {
	private String id;
	private String name;
	private String type;
	private String openingBalance;
	private String savingsRate;
	
	public Account() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOpeningBalance() {
		return openingBalance;
	}

	public void setOpeningBalance(String openingBalance) {
		this.openingBalance = openingBalance;
	}

	public String getSavingsRate() {
		return savingsRate;
	}

	public void setSavingsRate(String savingsRate) {
		this.savingsRate = savingsRate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((openingBalance == null) ? 0 : openingBalance.hashCode());
		result = prime * result + ((savingsRate == null) ? 0 : savingsRate.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (openingBalance == null) {
			if (other.openingBalance != null)
				return false;
		} else if (!openingBalance.equals(other.openingBalance))
			return false;
		if (savingsRate == null) {
			if (other.savingsRate != null)
				return false;
		} else if (!savingsRate.equals(other.savingsRate))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}
}
