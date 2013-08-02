/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2012 Jahia Solutions Group SA. All rights reserved.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 * As a special exception to the terms and conditions of version 2.0 of
 * the GPL (or any later version), you may redistribute this Program in connection
 * with Free/Libre and Open Source Software ("FLOSS") applications as described
 * in Jahia's FLOSS exception. You should have received a copy of the text
 * describing the FLOSS exception, and it is also available here:
 * http://www.jahia.com/license
 *
 * Commercial and Supported Versions of the program (dual licensing):
 * alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms and conditions contained in a separate
 * written agreement between you and Jahia Solutions Group SA.
 *
 * If you are unsure which license is appropriate for your use,
 * please contact the sales department at sales@jahia.com.
 */

package org.jahia.services.cache.ehcache;

import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.event.CacheEventListener;
import org.jahia.services.render.filter.cache.ModuleCacheProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * A listener that will invalidate HTMLCache upon messages received.
 *
 * Right now there is two commands :
 * FLUSH_PATH with the path to flush as a value
 * FLUSH_REGEXP with the regexp to flush as a value
 *
 * @author cedric . mailleux @ jahia . com
 *
 * @since 6.6.2
 */
public class FlushCacheEventListener implements CacheEventListener {
    private static Logger logger = LoggerFactory.getLogger(FlushCacheEventListener.class);

    public FlushCacheEventListener(Properties properties) {
    }

    public void notifyElementRemoved(Ehcache ehcache, Element element) throws CacheException {

    }

    public void notifyElementPut(Ehcache ehcache, Element element) throws CacheException {
        String command = (String) element.getObjectKey();
        if (command.startsWith("FLUSH_PATH")) {
            // We want to avoid loops of events so we do not propagate
            String pathToFlush = (String) element.getValue();
            logger.info(ehcache.getName() + ": Received command FLUSH_PATH ("+ pathToFlush +")remotely.");
            ModuleCacheProvider.getInstance().invalidate(pathToFlush, false);
        }
        else if (command.startsWith("FLUSH_REGEXP")) {
            // We want to avoid loops of events so we do not propagate
            String regexpToFlush = (String) element.getValue();
            logger.info(ehcache.getName() + ": Received command FLUSH_REGEXP ("+ regexpToFlush +")remotely.");
            ModuleCacheProvider.getInstance().invalidateRegexp(regexpToFlush, false);
        }
    }

    public void notifyElementUpdated(Ehcache ehcache, Element element) throws CacheException {
        notifyElementPut(ehcache, element);
    }

    public void notifyElementExpired(Ehcache ehcache, Element element) {

    }

    public void notifyElementEvicted(Ehcache ehcache, Element element) {

    }

    public void notifyRemoveAll(Ehcache ehcache) {

    }

    public void dispose() {

    }

    public Object clone() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
