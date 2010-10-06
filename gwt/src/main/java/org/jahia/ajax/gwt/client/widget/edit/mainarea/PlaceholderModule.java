/**
 * This file is part of Jahia: An integrated WCM, DMS and Portal Solution
 * Copyright (C) 2002-2010 Jahia Solutions Group SA. All rights reserved.
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
 * Commercial and Supported Versions of the program
 * Alternatively, commercial and supported versions of the program may be used
 * in accordance with the terms contained in a separate written agreement
 * between you and Jahia Solutions Group SA. If you are unsure which license is appropriate
 * for your use, please contact the sales department at sales@jahia.com.
 */

package org.jahia.ajax.gwt.client.widget.edit.mainarea;

import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.dnd.DND;
import com.extjs.gxt.ui.client.dnd.DropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.SwallowEvent;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import org.jahia.ajax.gwt.client.core.JahiaGWTParameters;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodeType;
import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.messages.Messages;
import org.jahia.ajax.gwt.client.util.content.actions.ContentActions;
import org.jahia.ajax.gwt.client.widget.edit.EditModeDNDListener;


/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Aug 19, 2009
 * Time: 12:03:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlaceholderModule extends Module {
    private HorizontalPanel panel;

    public PlaceholderModule(String id, String path, String nodeTypes, String referenceType, MainModule mainModule) {
        super(id, path, null, null, nodeTypes, referenceType, mainModule, new FlowLayout());
        this.id = id;
        this.path = path;
        this.mainModule = mainModule;
        this.nodeTypes = nodeTypes;

        if (path.endsWith("*")) {
            setBorders(false);
        } else {
            setBorders(true);
        }

        setHeight(20);

        panel = new HorizontalPanel();
        panel.setHorizontalAlign(Style.HorizontalAlignment.CENTER);
        panel.addStyleName("x-small-editor");
        panel.addStyleName("x-panel-header");
        panel.addStyleName("x-panel-placeholder");

//        html = new HTML("<img src=\""+JahiaGWTParameters.getContextPath() + "/modules/default/images/add.png"+"\" /> Add new content here");
        html = new HTML(Messages.get("label.add") + " : &nbsp;");
        panel.add(html);
        add(panel);
    }

    @Override
    public void onParsed() {
/*
        DropTarget target = new ModuleDropTarget(this, EditModeDNDListener.PLACEHOLDER_TYPE);
        target.setOperation(DND.Operation.COPY);
        target.setFeedback(DND.Feedback.INSERT);

        target.addDNDListener(mainModule.getEditLinker().getDndListener());
        if (getParentModule() != null && getParentModule().getNodeTypes() != null) {
        String[] nodeTypesArray = getParentModule().getNodeTypes().split(" ");
//        HorizontalPanel buttonsPanel = new HorizontalPanel();
//        buttonsPanel.setStyleName("listEditToolbar");
        for (String s : nodeTypesArray) {
            Button button = new Button(ModuleHelper.getNodeType(s)!= null ? ModuleHelper.getNodeType(s).getLabel():s);
            button.setStyleName("button-placeholder");
            button.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    ContentActions.showContentWizard(mainModule.getEditLinker(), parentModule.getNodeTypes(), getParentModule().getNode());
                }
            });
            panel.add(button);
        }
        }
*/
//        add(buttonsPanel);
//        html.setHTML("Drop here : " + getParentModule().getNodeTypes());
//        button.addSelectionListener(new SelectionListener<ButtonEvent>() {
//            @Override
//            public void componentSelected(ButtonEvent ce) {
//                ContentActions.showContentWizard(mainModule.getEditLinker(), parentModule.getNodeTypes());
//            }
//        });
    }

    public void onNodeTypesLoaded() {
        DropTarget target = new ModuleDropTarget(this, EditModeDNDListener.PLACEHOLDER_TYPE);
        target.setOperation(DND.Operation.COPY);
        target.setFeedback(DND.Feedback.INSERT);

        target.addDNDListener(mainModule.getEditLinker().getDndListener());
        if (getParentModule() != null && getParentModule().getNodeTypes() != null) {
        String[] nodeTypesArray = getParentModule().getNodeTypes().split(" ");
//        HorizontalPanel buttonsPanel = new HorizontalPanel();
//        buttonsPanel.setStyleName("listEditToolbar");
        for (String s : nodeTypesArray) {
            Button button = new Button(ModuleHelper.getNodeType(s)!= null ? ModuleHelper.getNodeType(s).getLabel():s);
            button.setStyleName("button-placeholder");
            button.addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    final GWTJahiaNode parentNode = getParentModule().getNode();
                    if (parentNode != null && parentNode.isWriteable() && !parentNode.isLocked()) {
                        ContentActions.showContentWizard(mainModule.getEditLinker(), parentModule.getNodeTypes(), parentNode);
                    }
                }
            });
            panel.add(button);
            panel.layout();
        }
        }

    }

    public boolean isDraggable() {
        return false;
    }
}
