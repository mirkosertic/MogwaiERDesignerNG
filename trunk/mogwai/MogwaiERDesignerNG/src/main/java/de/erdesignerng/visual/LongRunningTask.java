/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.visual;

import de.erdesignerng.visual.common.ERDesignerWorldConnector;

import javax.swing.*;
import java.util.List;

public abstract class LongRunningTask<T> extends Thread {

	private final ERDesignerWorldConnector connector;

	public interface MessagePublisher {

		void publishMessage(String aMessage);
	}

	protected abstract class MySwingWorker<X> extends SwingWorker<X, String> implements MessagePublisher {

		@Override
		public void publishMessage(String aMessage) {
			publish(aMessage );
		}
	}

	public LongRunningTask(ERDesignerWorldConnector aConnector) {
		connector = aConnector;
	}

	@Override
	public void run() {
		SwingWorker<T, String> worker=new MySwingWorker<T>() {

			@Override
			protected T doInBackground() throws Exception {
				return doWork(this);
			}

			@Override
			protected void process(List<String> aChunks) {
				handleProcess(aChunks);
			}
		};
		worker.execute();
		try {
			handleResult(worker.get());
		} catch (Exception e) {
			connector.notifyAboutException(e);
		} finally {
			try {
				cleanup();
			} catch (Exception e) {
				connector.notifyAboutException(e);
			}
		}
	}

	public void handleProcess(List<String> aChunks) {
        aChunks.forEach(connector::setStatusText);
	}

	public abstract T doWork(MessagePublisher aMessagePublisher) throws Exception;

	public abstract void handleResult(T aResult);

	public void cleanup() throws Exception {
	}
}