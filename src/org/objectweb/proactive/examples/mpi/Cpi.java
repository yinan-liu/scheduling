/*
 * ################################################################
 *
 * ProActive: The Java(TM) library for Parallel, Distributed,
 *            Concurrent computing with Security and Mobility
 *
 * Copyright (C) 1997-2005 INRIA/University of Nice-Sophia Antipolis
 * Contact: proactive@objectweb.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 * USA
 *
 *  Initial developer(s):               The ProActive Team
 *                        http://www.inria.fr/oasis/ProActive/contacts.html
 *  Contributor(s):
 *
 * ################################################################
 */
package org.objectweb.proactive.examples.mpi;

import org.apache.log4j.Logger;

import org.objectweb.proactive.ProActive;
import org.objectweb.proactive.core.ProActiveException;
import org.objectweb.proactive.core.config.ProActiveConfiguration;
import org.objectweb.proactive.core.descriptor.data.ProActiveDescriptor;
import org.objectweb.proactive.core.descriptor.data.VirtualNode;
import org.objectweb.proactive.core.util.log.Loggers;
import org.objectweb.proactive.core.util.log.ProActiveLogger;


/**
 *  This example uses a simple mpi program (cpi) which calculates
 *  an approximation of PI number on localhost.
 *  One purpose is the possibility to launch several times consecutively
 *  this program just by calling the startMPI() method on the virtualnode
 *  which with the MPI program is associated.
 *  It permitts to manage as many MPI program as the user define some virtual nodes.
 *  
 */
public class Cpi {
    static public void main(String[] args) {
        Logger logger = ProActiveLogger.getLogger(Loggers.EXAMPLES);

        if (args.length != 2) {
            logger.error("Usage: java " + Cpi.class.getName() +
                " <number of iterations> <deployment file>");
            System.exit(0);
        }

        ProActiveConfiguration.load();

        VirtualNode vnCpi;
        ProActiveDescriptor pad = null;
        int count;
        int exitValue;
        try {
            pad = ProActive.getProactiveDescriptor("file:" + args[1]);
            count = new Integer(args[0]).intValue();
            int initValue = count-1;
            // gets virtual node 
            vnCpi = pad.getVirtualNode("CPI");
            // activates VN
            vnCpi.activate();
            
            while ((count--) > 0) {
                logger.info(" -> Iteration [" + (initValue-count) + "]");
                exitValue = vnCpi.startMPI();
                if (exitValue != 0){
                	logger.error("ERROR : try to run \"lamboot\" command");
                	break;
                }else{
                	logger.info(" MPI code returned value  "+exitValue );
                }
            }
            vnCpi.killAll(false);
            System.exit(0);
        } catch (ProActiveException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            logger.error("Pb when reading descriptor");
        }
    }
}
