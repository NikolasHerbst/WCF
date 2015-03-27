/*******************************************************************************
* Copyright (c) 2014 Andre van Hoorn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.rServerBridgeTest;

import static org.junit.Assert.assertTrue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;

import tools.descartes.wcf.rServerBridge.RServerBridgeControl;


public class RServerBridgeControlTest {
	private static final Log LOG = LogFactory.getLog(RServerBridgeControlTest.class);

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {

		RServerBridgeControl r = RServerBridgeControl.getInstance();	
		Object result = r.e("4+5");
		LOG.info(result);
		assertTrue(result != null);
		//assertTrue(result instanceof org.rosuda.REngine.REXPInteger);
	}

}
