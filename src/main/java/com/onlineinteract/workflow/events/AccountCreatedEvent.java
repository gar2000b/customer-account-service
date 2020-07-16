package com.onlineinteract.workflow.events;

import com.onlineinteract.workflow.domain.account.AccountEvent;

public class AccountCreatedEvent extends Event {
	private AccountEvent account;
	
	public AccountCreatedEvent() {
	}
	
	public AccountCreatedEvent(AccountEvent account) {
		this.account = account;
		this.setType("AccountCreatedEvent");
	}

	public AccountEvent getAccount() {
		return account;
	}

	public void setAccount(AccountEvent account) {
		this.account = account;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((account == null) ? 0 : account.hashCode());
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
		AccountCreatedEvent other = (AccountCreatedEvent) obj;
		if (account == null) {
			if (other.account != null)
				return false;
		} else if (!account.equals(other.account))
			return false;
		return true;
	}
}
