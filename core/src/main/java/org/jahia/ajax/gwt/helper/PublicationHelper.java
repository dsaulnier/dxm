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

package org.jahia.ajax.gwt.helper;

import org.apache.commons.collections.OrderedMap;
import org.apache.commons.collections.map.LinkedMap;
import org.apache.commons.collections.map.ListOrderedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodeProperty;
import org.jahia.ajax.gwt.client.data.definition.GWTJahiaNodePropertyValue;
import org.jahia.ajax.gwt.client.data.publication.GWTJahiaPublicationInfo;
import org.jahia.ajax.gwt.client.service.GWTJahiaServiceException;
import org.jahia.ajax.gwt.client.widget.publication.PublicationWorkflow;
import org.jahia.api.Constants;
import org.jahia.exceptions.JahiaException;
import org.jahia.registries.ServicesRegistry;
import org.jahia.services.content.*;
import org.jahia.services.scheduler.BackgroundJob;
import org.jahia.services.usermanager.JahiaUser;
import org.jahia.services.workflow.WorkflowRule;
import org.jahia.services.workflow.WorkflowService;
import org.jahia.services.workflow.WorkflowVariable;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;

import javax.jcr.RepositoryException;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Sep 28, 2009
 * Time: 2:15:34 PM
 * 
 */
public class PublicationHelper {
    private static Logger logger = org.slf4j.LoggerFactory.getLogger(PublicationHelper.class);

    private JCRPublicationService publicationService;
    private WorkflowService workflowService;

    public void setPublicationService(JCRPublicationService publicationService) {
        this.publicationService = publicationService;
    }

    public void setWorkflowService(WorkflowService workflowService) {
        this.workflowService = workflowService;
    }

    /**
     * Get the publication status information for a particular path.
     *
     * @param uuid               to get publication info from
     * @param currentUserSession
     * @return a GWTJahiaPublicationInfo object filled with the right status for the publication state of this path
     * @throws org.jahia.ajax.gwt.client.service.GWTJahiaServiceException
     *          in case of any RepositoryException
     */
    public GWTJahiaPublicationInfo getSimplePublicationInfo(String uuid, Set<String> languages, JCRSessionWrapper currentUserSession) throws GWTJahiaServiceException {
        try {
            PublicationInfo pubInfo = publicationService.getPublicationInfo(uuid, languages, true, true, false, currentUserSession.getWorkspace().getName(), Constants.LIVE_WORKSPACE).get(0);
            GWTJahiaPublicationInfo gwtInfo = new GWTJahiaPublicationInfo(pubInfo.getRoot().getPath(), pubInfo.getRoot().getStatus(), pubInfo.getRoot().isCanPublish());
            if (pubInfo.getRoot().isLocked()  ) {
//                gwtInfo.setLocked(true);
            }
            for (PublicationInfoNode sub : pubInfo.getRoot().getChildren()) {
                if (sub.getPath().contains("/j:translation")) {
                    if (sub.getStatus() > gwtInfo.getStatus()) {
                        gwtInfo.setStatus(sub.getStatus());
                    }
                    if (gwtInfo.getStatus() == GWTJahiaPublicationInfo.UNPUBLISHED && sub.getStatus() != GWTJahiaPublicationInfo.UNPUBLISHED) {
                        gwtInfo.setStatus(sub.getStatus());
                    }
                    if (sub.isLocked()) {
                        gwtInfo.setLocked(true);
                    }
                }
            }


            if (gwtInfo.getStatus() < GWTJahiaPublicationInfo.NOT_PUBLISHED) {
                Set<Integer> status = new HashSet<Integer>(pubInfo.getTreeStatus());
                for (PublicationInfo refInfo : pubInfo.getAllReferences()) {
                    status.addAll(refInfo.getTreeStatus());
                }
                if (!status.isEmpty() && Collections.max(status) > GWTJahiaPublicationInfo.PUBLISHED) {
                    gwtInfo.setStatus(GWTJahiaPublicationInfo.MODIFIED);
                }
            }
            return gwtInfo;
        } catch (RepositoryException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        }

    }

    public List<GWTJahiaPublicationInfo> getPublicationInfo(List<String> uuids, Set<String> languages,
                                                            JCRSessionWrapper currentUserSession, boolean allSubTree) throws GWTJahiaServiceException {
        try {
            List<PublicationInfo> infos = publicationService.getPublicationInfos(uuids, languages, true, true, allSubTree, currentUserSession.getWorkspace().getName(), Constants.LIVE_WORKSPACE);
            return convert(infos, currentUserSession);
        } catch (RepositoryException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        }
    }

    public List<GWTJahiaPublicationInfo> convert(List<PublicationInfo> pubInfos, JCRSessionWrapper currentUserSession) {
        List<GWTJahiaPublicationInfo> gwtInfos = new ArrayList<GWTJahiaPublicationInfo>();
        List<String> mainTitles = new ArrayList<String>();
        for (PublicationInfo pubInfo : pubInfos) {
            gwtInfos.addAll((Collection<GWTJahiaPublicationInfo>) convert(pubInfo, pubInfo.getRoot().getPath(), mainTitles, currentUserSession).values());
        }
        return gwtInfos;
    }

    private OrderedMap convert(PublicationInfo pubInfo, String mainTitle, List<String> mainTitles,
                                                  JCRSessionWrapper currentUserSession) {
        PublicationInfoNode node = pubInfo.getRoot();
        OrderedMap gwtInfos = new LinkedMap();
        List<PublicationInfo> references = new ArrayList<PublicationInfo>();
        convert(gwtInfos, mainTitle, mainTitles, node, references, currentUserSession);
        for (PublicationInfo pi : references) {
            if (!gwtInfos.containsKey(pi.getRoot().getUuid())) {
                gwtInfos.putAll(convert(pi, pi.getRoot().getPath(), mainTitles, currentUserSession));
            }
        }

        return gwtInfos;
    }

    private GWTJahiaPublicationInfo convert(OrderedMap all, String mainTitle,
                                            List<String> mainTitles, PublicationInfoNode node,
                                            List<PublicationInfo> references, JCRSessionWrapper currentUserSession) {
        GWTJahiaPublicationInfo gwtInfo = convert(node, currentUserSession);
        all.put(node.getUuid(), gwtInfo);
        gwtInfo.set("mainTitle", mainTitle);
        if (!mainTitles.contains(mainTitle)) {
            mainTitles.add(mainTitle);
        }
        gwtInfo.set("mainTitleIndex", mainTitles.indexOf(mainTitle));
        Map<String, GWTJahiaPublicationInfo> gwtInfos = new HashMap<String, GWTJahiaPublicationInfo>();
        gwtInfos.put(node.getPath(), gwtInfo);
        List<String> refUuids = new ArrayList<String>();
        if (node.isLocked()  ) {
//            gwtInfo.setLocked(true);
        }
        for (PublicationInfoNode sub : node.getChildren()) {
            if (sub.getPath().contains("/j:translation")) {
                String key = StringUtils.substringBeforeLast(sub.getPath(), "/j:translation");
                GWTJahiaPublicationInfo lastPub = gwtInfos.get(key);
                if (lastPub != null) {
                    if (sub.getStatus() > lastPub.getStatus()) {
                        lastPub.setStatus(sub.getStatus());
                    }
                    if (lastPub.getStatus() == GWTJahiaPublicationInfo.UNPUBLISHED && sub.getStatus() != GWTJahiaPublicationInfo.UNPUBLISHED) {
                        lastPub.setStatus(sub.getStatus());
                    }
                    if (sub.isLocked()) {
                        gwtInfo.setLocked(true);
                    }
                }
                references.addAll(sub.getReferences());
//                for (PublicationInfo pi : sub.getReferences()) {
//                    if (!refUuids.contains(pi.getRoot().getUuid())) {
//                        refUuids.add(pi.getRoot().getUuid());
//                        all.addAll(convert(pi, pi.getRoot().getPath(), mainTitles, currentUserSession));
//                    }
//                }

            } else if (sub.getPath().indexOf("/j:translation") == -1) {
                GWTJahiaPublicationInfo lastPub = convert(all, mainTitle, mainTitles, sub, references, currentUserSession);
                gwtInfos.put(lastPub.getPath(), lastPub);
            }
        }
        references.addAll(node.getReferences());
//        for (PublicationInfo pi : node.getReferences()) {
//            if (!refUuids.contains(pi.getRoot().getUuid())) {
//                refUuids.add(pi.getRoot().getUuid());
//                all.addAll(convert(pi, pi.getRoot().getPath(), mainTitles, currentUserSession));
//            }
//        }

        return gwtInfo;
    }

    private GWTJahiaPublicationInfo convert(PublicationInfoNode node, JCRSessionWrapper currentUserSession) {
        GWTJahiaPublicationInfo gwtInfo = new GWTJahiaPublicationInfo(node.getPath(), node.getStatus(), node.isCanPublish());
        try {
            JCRNodeWrapper n = currentUserSession.getNodeByUUID(node.getUuid());
            if (n.hasProperty("jcr:title")) {
                gwtInfo.setTitle(n.getProperty("jcr:title").getString());
            } else {
                gwtInfo.setTitle(n.getName());
            }
            gwtInfo.setNodetype(n.getPrimaryNodeType().getLabel(currentUserSession.getLocale()));
        } catch (RepositoryException e) {
            gwtInfo.setTitle(node.getPath());
        }
        return gwtInfo;
    }

    /**
     * Publish a node into the live workspace.
     * Referenced nodes will also be published.
     * Parent node must be published, or will be published if publishParent is true.
     *
     * @param path      Path of the node to publish
     * @param languages Set of languages to publish if null publish all languages
     * @param reverse
     * @throws org.jahia.ajax.gwt.client.service.GWTJahiaServiceException
     *          in case of any RepositoryException
     */
    public void publish(String path, Set<String> languages, boolean allSubTree, boolean reverse, JCRSessionWrapper session, List<String> comments) throws GWTJahiaServiceException {
        try {
            if (reverse) {
                publicationService.publish(path, Constants.LIVE_WORKSPACE, session.getWorkspace().getName(), languages,
                        allSubTree, comments);
            } else {
                publicationService.publish(path, session.getWorkspace().getName(), Constants.LIVE_WORKSPACE, languages,
                        allSubTree, comments);
            }
        } catch (RepositoryException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        }
    }

    /**
     * Publish a list of nodes into the live workspace.
     * Referenced nodes will also be published.
     * Parent node must be published, or will be published if publishParent is true.
     *
     * @param uuids    list of uuids of the nodes to publish
     * @param language language to publish if null publish all languages
     * @param workflow @throws org.jahia.ajax.gwt.client.service.GWTJahiaServiceException
     * @param comments
     */
    public void publish(List<String> uuids, String language, boolean allSubTree, boolean workflow, boolean reverse,
                        JCRSessionWrapper session, List<GWTJahiaNodeProperty> properties, List<String> comments) throws GWTJahiaServiceException {
        try {
            // todo : if workflow started on untranslated node, translation will be created and not added into the publish tree calculated here 

            final String workspaceName = session.getWorkspace().getName();
            List<PublicationInfo> infos = publicationService.getPublicationInfos(uuids, Collections.singleton(language), true, true, allSubTree,
                    workspaceName, Constants.LIVE_WORKSPACE);
            if (workflow) {
                Map<WorkflowRule, List<PublicationInfo>> m = new ListOrderedMap();

                for (PublicationInfo info : infos) {
                    if (info.needPublication()) {
                        splitWorkflows(m, info.getRoot(), null, session);
                    }
                }

                HashMap<String, Object> map = new HashMap<String, Object>();
                List<WorkflowVariable> values = new ArrayList<WorkflowVariable>();
                map.put("jcr:title", infos.get(0).getRoot().getPath());
                if (properties != null) {
                    for (GWTJahiaNodeProperty property : properties) {
                        List<GWTJahiaNodePropertyValue> propertyValues = property.getValues();
                        values = new ArrayList<WorkflowVariable>(propertyValues.size());
                        boolean toBeAdded = false;
                        for (GWTJahiaNodePropertyValue value : propertyValues) {
                            String s = value.getString();
                            if (s != null && !"".equals(s)) {
                                values.add(new WorkflowVariable(s, value.getType()));
                                toBeAdded = true;
                            }
                        }
                        if (toBeAdded) {
                            map.put(property.getName(), values);
                        } else {
                            map.put(property.getName(), new ArrayList<WorkflowVariable>());
                        }
                    }
                }
                for (Map.Entry<WorkflowRule, List<PublicationInfo>> entry : m.entrySet()) {
                    List<String> ids = new ArrayList<String>();
                    final List<PublicationInfo> localInfos = entry.getValue();
                    boolean needed = false;
                    for (PublicationInfo localInfo : localInfos) {
                        needed |= localInfo.needPublication();
                    }
                    if (needed) {
                        map.put("publicationInfos", localInfos);
                        List<GWTJahiaPublicationInfo> gwtInfos = convert(localInfos, session);
                        map.put("customWorkflowInfo", new PublicationWorkflow(gwtInfos, uuids, allSubTree, language));

                        for (PublicationInfo node : localInfos) {
                            ids.add(node.getRoot().getUuid());
                        }
                        String id = workflowService.startProcess(ids, session, entry.getKey().getWorkflowDefinitionKey(), entry.getKey().getProviderKey(), map);
                        for (String s : comments) {
                            workflowService.addComment(id, entry.getKey().getProviderKey(), s, session.getUser().getUserKey());
                        }
                    }
                }
            } else {
                if (reverse) {
//                    publicationService.publish(infos, Constants.LIVE_WORKSPACE, workspaceName);
                } else {
                    JobDetail jobDetail = BackgroundJob.createJahiaJob("Publication", PublicationJob.class);
                    JobDataMap jobDataMap = jobDetail.getJobDataMap();
                    jobDataMap.put(PublicationJob.PUBLICATION_COMMENTS, comments);
                    jobDataMap.put(PublicationJob.PUBLICATION_INFOS, infos);
                    jobDataMap.put(PublicationJob.SOURCE, workspaceName);
                    jobDataMap.put(PublicationJob.DESTINATION, Constants.LIVE_WORKSPACE);

                    ServicesRegistry.getInstance().getSchedulerService().scheduleJobNow(jobDetail);
                }
            }
        } catch (RepositoryException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        } catch (JahiaException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        }
    }

    public boolean splitWorkflows(Map<WorkflowRule, List<PublicationInfo>> m, PublicationInfoNode node, WorkflowRule currentDef, JCRSessionWrapper session) throws RepositoryException {
        JCRNodeWrapper n = session.getNodeByUUID(node.getUuid());
        boolean split = false;

        if (currentDef == null || n.hasNode("j:workflow")) {
            WorkflowRule rule = workflowService.getWorkflowRuleForAction(n, session.getUser(), "publish", null);
            if (rule == null) {
                return false;
            } else {
                if (!rule.equals(currentDef)) {
                    currentDef = rule;
                    if (!m.containsKey(currentDef)) {
                        m.put(currentDef, new ArrayList<PublicationInfo>());
                    }
                    m.get(currentDef).add(new PublicationInfo(node));
                    split = true;
                }
            }
        }
        List<PublicationInfoNode> childSplit = new ArrayList<PublicationInfoNode>();
        for (PublicationInfoNode childNode : node.getChildren()) {
            if (splitWorkflows(m, childNode, currentDef, session)) {
                childSplit.add(childNode);
            }
        }
        node.getChildren().removeAll(childSplit);

        List<PublicationInfo> refSplit = new ArrayList<PublicationInfo>();
        for (PublicationInfo publicationNode : node.getReferences()) {
            if (publicationNode.needPublication()) {
                if (splitWorkflows(m, publicationNode.getRoot(), null, session)) {
                    refSplit.add(publicationNode);
                }
            } else {
                refSplit.add(publicationNode);
            }
        }
        node.getReferences().removeAll(refSplit);
        return split;
    }


    /**
     * Unpublish a node from live workspace.
     * Referenced Node will not be unpublished.
     *
     * @param uuid      path of the node to unpublish
     * @param languages Set of languages to unpublish if null unpublish all languages
     * @param user      the user for obtaining the jcr session
     * @throws org.jahia.ajax.gwt.client.service.GWTJahiaServiceException
     *          in case of any RepositoryException
     */
    public void unpublish(String uuid, Set<String> languages, JahiaUser user) throws GWTJahiaServiceException {
        try {
            publicationService.unpublish(uuid, languages);
        } catch (RepositoryException e) {
            logger.error("repository exception", e);
            throw new GWTJahiaServiceException(e.getMessage());
        }
    }
}
