/**
 * This file is part of Jahia, next-generation open source CMS:
 * Jahia's next-generation, open source CMS stems from a widely acknowledged vision
 * of enterprise application convergence - web, search, document, social and portal -
 * unified by the simplicity of web content management.
 *
 * For more information, please visit http://www.jahia.com.
 *
 * Copyright (C) 2002-2014 Jahia Solutions Group SA. All rights reserved.
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

package org.jahia.data.templates;

/**
 * Represents the state of a module in its life cycle.
 */
public class ModuleState {
    public static enum State {
        ERROR_DURING_START, INSTALLED, PARSED, RESOLVED, STARTED, STARTING, STOPPED, STOPPING, UNINSTALLED, UNRESOLVED, UPDATED, WAITING_TO_BE_PARSED, WAITING_TO_BE_STARTED, WAITING_TO_BE_IMPORTED, SPRING_NOT_STARTED;

        @Override
        public String toString() {
            switch (this) {
                case UNINSTALLED:
                    return "Uninstalled";
                case UNRESOLVED:
                    return "Unresolved";
                case RESOLVED:
                    return "Resolved";
                case WAITING_TO_BE_PARSED:
                    return "Waiting to be parsed";
                case PARSED:
                    return "Parsed";
                case INSTALLED:
                    return "Installed";
                case UPDATED:
                    return "Updated";
                case STOPPED:
                    return "Stopped";
                case STOPPING:
                    return "Stopping";
                case STARTING:
                    return "Starting";
                case WAITING_TO_BE_STARTED:
                    return "Waiting to be started";
                case ERROR_DURING_START:
                    return "Error during start";
                case STARTED:
                    return "Started";
                case SPRING_NOT_STARTED:
                    return "Spring not started";
                case WAITING_TO_BE_IMPORTED:
                    return "Waiting to be imported by processing server";
                default:
                    break;
            }
            return super.toString();
        }
    }

    private Object details;

    private State state;

    public Object getDetails() {
        return details;
    }

    public State getState() {
        return state;
    }

    public void setDetails(Object details) {
        this.details = details;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return details != null ? new StringBuilder(256).append("state: ").append(state).append("; details: ")
                .append(details).toString() : state.toString();
    }
}
