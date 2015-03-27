/*******************************************************************************
* Copyright (c) 2014 Andre van Hoorn, Nikolas Herbst, http://descartes.tools/wcf
* All rights reserved. This program and the accompanying materials
* are made available under the terms of the Eclipse Public License v1.0
* which accompanies this distribution, and is available at
* http://www.eclipse.org/legal/epl-v10.html
*******************************************************************************/

package tools.descartes.wcf.rServerBridge;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.math.R.RserverConf;
import org.math.R.Rsession;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REXPVector;

public class RServerBridgeControl {
	
	/**
	 * Configure here your RServe Connection:
	 */
	private static final String HOST = "localhost";
	private static final int PORT = 6311;
	private static final String USER = null;
	private static final String PASSWD = null;

	private static final Log LOG = LogFactory.getLog(RServerBridgeControl.class);
	private static final Log RSERVELOG = LogFactory.getLog("RSERVE");

	Rsession rCon;

	public static RServerBridgeControl INSTANCE = null;

	private RServerBridgeControl(boolean silent) {

		OutputStream out = System.out;

		if (true == silent) {
			out = new OutputStream() {

				@Override
				public void write(final int arg0) throws IOException {
				}
			};
		} else {
			out = new OutputStream() {

				private int lineEnd = (int) '\n';
				private ByteArrayOutputStream baos = new ByteArrayOutputStream();

				@Override
				public void write(int b) throws IOException {
					if (b == lineEnd) {
						RSERVELOG.info(baos.toString());
						baos.reset();
					} else 
						baos.write(b);
				}

			};
		}
		RserverConf serverconf = new RserverConf(HOST, PORT, USER, PASSWD, null);
		this.rCon = Rsession.newInstanceTry(new PrintStream(out), serverconf);
	}

	public static RServerBridgeControl getInstance() {
		if (null == RServerBridgeControl.INSTANCE) {
			RServerBridgeControl.INSTANCE = new RServerBridgeControl(true);
		}
		return RServerBridgeControl.INSTANCE;
	}

	/**
	 * wraps the execution of an arbitrary R expression. Logs result and error
	 * 
	 * @param input
	 * @return
	 */
	public Object e(final String input) {
		Object out = null;
		try {
			out = this.rCon.eval(input);

			Object output = out;

			if (out instanceof REXPString)
				output = ((REXPString) out).asString();
			if (out instanceof REXPLogical)
				output = ((REXPLogical) out).toDebugString();

			RServerBridgeControl.LOG.info("> REXP: " + input + " return: " + output);

		} catch (final Exception exc) {
			RServerBridgeControl.LOG.error("Error R expr.: " + input + " Cause: "
					+ exc);
			exc.printStackTrace();
		}
		return out;
	}

	public double eDbl(final String input) {
		try {
			return ((REXPDouble) this.e(input)).asDouble();
		} catch (final Exception exc) {
			RServerBridgeControl.LOG.error("Error casting value from R: " + input
					+ " Cause: " + exc);
			return -666.666;
		}
	}

	public String eString(final String input) {
		try {
			final REXPString str = (REXPString) this.e(input);
			return str.toString();
		} catch (final Exception e) {
			return "";
		}
	}

	public double[] eDblArr(final String input) {
		try {
			final REXPVector res = (REXPVector) this.e(input);
			return res.asDoubles();
		} catch (final Exception e) {
			return new double[0];
		}
	}

	public void assign(final String variable, final double[] values) {
		try {
			final StringBuffer buf = new StringBuffer();
			buf.append(variable + " <<- c(");
			boolean first = true;
			for (final double item : values) {
				if (!first) {
					buf.append(",");
				} else {
					first = false;
				}
				buf.append(item);
			}
			buf.append(")");
			this.e(buf.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public void assign(final String variable, final Double[] values) {
		try {
			final StringBuffer buf = new StringBuffer();
			buf.append(variable + " <<- c(");
			boolean first = true;
			for (final Double item : values) {
				if (!first) {
					buf.append(",");
				} else {
					first = false;
				}
				if (null == item || item.isNaN()) {
					buf.append("NA");
				} else {
					buf.append(item);
				}
			}
			buf.append(")");
			this.e(buf.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public void assign(final String variable, final Long[] values) {
		try {
			final StringBuffer buf = new StringBuffer();
			buf.append(variable + " <<- c(");
			boolean first = true;
			for (final Long item : values) {
				if (!first) {
					buf.append(",");
				} else {
					first = false;
				}
				buf.append(item);
			}
			buf.append(")");
			this.e(buf.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	public void toTS(final String variable, long frequency) {
		try {
			final StringBuffer buf = new StringBuffer();
			buf.append(variable + " <<- ts("+ variable + ",frequency=" + frequency + ")");
			this.e(buf.toString());
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private final static AtomicInteger nextVarId = new AtomicInteger(1);

	/**
	 * Returns a globally unique variable name.
	 * 
	 * @param prefix
	 *            may be null
	 * @return
	 */
	public static String uniqueVarname() {
		return String.format("var_%s",
				RServerBridgeControl.nextVarId.getAndIncrement());
	}
}
