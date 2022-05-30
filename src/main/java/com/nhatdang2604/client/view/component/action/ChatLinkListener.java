package com.nhatdang2604.client.view.component.action;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import com.nhatdang2604.server.entities.Message;

public class ChatLinkListener extends AbstractAction  {
    
	private Message message;

    public ChatLinkListener(Message message) {
        this.message = message;
    }

    public Message getMessage() {return this.message;}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		System.out.println("I think i should do something");
	}
}
