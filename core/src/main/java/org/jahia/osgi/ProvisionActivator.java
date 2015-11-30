/**
 * ==========================================================================================
 * =                   JAHIA'S DUAL LICENSING - IMPORTANT INFORMATION                       =
 * ==========================================================================================
 *
 *     Copyright (C) 2002-2015 Jahia Solutions Group SA. All rights reserved.
 *
 *     THIS FILE IS AVAILABLE UNDER TWO DIFFERENT LICENSES:
 *     1/GPL OR 2/JSEL
 *
 *     1/ GPL
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOSE THE GPL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     "This program is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU General Public License
 *     as published by the Free Software Foundation; either version 2
 *     of the License, or (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program; if not, write to the Free Software
 *     Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 *     As a special exception to the terms and conditions of version 2.0 of
 *     the GPL (or any later version), you may redistribute this Program in connection
 *     with Free/Libre and Open Source Software ("FLOSS") applications as described
 *     in Jahia's FLOSS exception. You should have received a copy of the text
 *     describing the FLOSS exception, also available here:
 *     http://www.jahia.com/license"
 *
 *     2/ JSEL - Commercial and Supported Versions of the program
 *     ======================================================================================
 *
 *     IF YOU DECIDE TO CHOOSE THE JSEL LICENSE, YOU MUST COMPLY WITH THE FOLLOWING TERMS:
 *
 *     Alternatively, commercial and supported versions of the program - also known as
 *     Enterprise Distributions - must be used in accordance with the terms and conditions
 *     contained in a separate written agreement between you and Jahia Solutions Group SA.
 *
 *     If you are unsure which license is appropriate for your use,
 *     please contact the sales department at sales@jahia.com.
 *
 *
 * ==========================================================================================
 * =                                   ABOUT JAHIA                                          =
 * ==========================================================================================
 *
 *     Rooted in Open Source CMS, Jahia’s Digital Industrialization paradigm is about
 *     streamlining Enterprise digital projects across channels to truly control
 *     time-to-market and TCO, project after project.
 *     Putting an end to “the Tunnel effect”, the Jahia Studio enables IT and
 *     marketing teams to collaboratively and iteratively build cutting-edge
 *     online business solutions.
 *     These, in turn, are securely and easily deployed as modules and apps,
 *     reusable across any digital projects, thanks to the Jahia Private App Store Software.
 *     Each solution provided by Jahia stems from this overarching vision:
 *     Digital Factory, Workspace Factory, Portal Factory and eCommerce Factory.
 *     Founded in 2002 and headquartered in Geneva, Switzerland,
 *     Jahia Solutions Group has its North American headquarters in Washington DC,
 *     with offices in Chicago, Toronto and throughout Europe.
 *     Jahia counts hundreds of global brands and governmental organizations
 *     among its loyal customers, in more than 20 countries across the globe.
 *
 *     For more information, please visit http://www.jahia.com
 */
package org.jahia.osgi;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.startlevel.StartLevel;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Initial startup OSGi provision activator
 *
 * @author loom
 *         Date: Oct 11, 2010
 *         Time: 5:18:48 PM
 */
public final class ProvisionActivator implements BundleActivator {

    private final ServletContext servletContext;
    private BundleContext bundleContext;

    private static ProvisionActivator instance = null;
    private static final Logger logger = LoggerFactory.getLogger(ProvisionActivator.class);

    public ProvisionActivator(ServletContext servletContext) {
        this.servletContext = servletContext;
        instance = this;
    }

    public static ProvisionActivator getInstance() {
        return instance;
    }

    @Override
    public void start(BundleContext context) throws Exception {

        bundleContext = context;
        servletContext.setAttribute(BundleContext.class.getName(), context);

        ArrayList<Bundle> installed = new ArrayList<Bundle>();
        for (URL url : findBundles()) {
            logger.info("Installing bundle [{}]", url);
            Bundle bundle = context.installBundle(url.toExternalForm());
            installed.add(bundle);
        }

        ServiceTracker st = new ServiceTracker(context, StartLevel.class.getName(), null);
        st.open();
        StartLevel sl = ((StartLevel)st.getService());

        for (Bundle bundle : installed) {
            if (bundle.getSymbolicName().equals("org.apache.felix.fileinstall")) {
                // Start fileInstall only on level 2
                sl.setBundleStartLevel(bundle,2);
            }

            // we first check if it is a fragment bundle, in which case we will not start it.
            if (bundle.getHeaders().get("Fragment-Host") == null) {
                bundle.start();
            }
        }
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        bundleContext = null;
        servletContext.removeAttribute(BundleContext.class.getName());
        instance = null;
    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    private List<URL> findBundles() throws Exception {
        ArrayList<URL> list = new ArrayList<URL>();
        for (Object o : this.servletContext.getResourcePaths("/WEB-INF/bundles/")) {
            String name = (String) o;
            if (name.endsWith(".jar")) {
                URL url = this.servletContext.getResource(name);
                if (url != null) {
                    list.add(url);
                }
            }
        }
        return list;
    }
}
