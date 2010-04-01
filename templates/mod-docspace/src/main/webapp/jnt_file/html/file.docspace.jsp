<%@ page import="org.jahia.services.content.JCRNodeWrapper" %>
<%@ page import="org.jahia.utils.FileUtils" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="functions" uri="http://www.jahia.org/tags/functions" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<%--@elvariable id="acl" type="java.lang.String"--%>
<template:addResources type="css" resources="docspace.css,files.css,toggle-docspace.css"/>
<template:addResources type="javascript" resources="jquery.min.js"/>
<template:addResources type="javascript" resources="jquery.jeditable.js"/>
<template:addResources type="javascript"
                       resources="${url.context}/gwt/resources/ckeditor/ckeditor.js"/>

<template:addResources type="javascript" resources="jquery.cuteTime.js"/>
<template:addResources type="javascript" resources="jquery.jeditable.ckeditor.js"/>

<script>
    $(document).ready(function() {
        $("#ckeditorEditDescription").editable(function (value, settings) {
            var url = $(this).attr('jcr:url');
            var submitId = $(this).attr('jcr:id');
            var data = {};
            data[submitId] = value;
            data['methodToCall'] = 'put';
            $.post(url, data, null, "json");
            return(value);
        }, {
            type : 'ckeditor',
            onblur : 'ignore',
            submit : 'Ok',
            cancel : 'Cancel',
            tooltip : 'Click to edit'
        });

        $("#actions").click(function() {
            if ($(this).hasClass( 'delete' )) {
                if (confirm("Do you REALLY want to delete this file?")) {
                    var data = {};
                    data['methodToCall'] = 'delete';
                    $.post('${url.base}${currentNode.path}', data, function () {
                        window.location.href = '${url.base}${currentNode.parent.path}.html';
                    }, "json");
                } else {
                    $(this).val("");
                }
            }
        });
    });
</script>
<div class='grid_16'><!--start grid_16-->
<a class="docspaceBack" href="${url.base}${currentNode.parent.path}.html">Back to: ${currentNode.parent.name}</a>
<div class='clear'></div></div>
<div class='grid_12'><!--start grid_12-->
    <div class="boxdocspace "><!--start boxdocspace -->
        <div class="boxdocspacepadding16 boxdocspacemarginbottom16">
            <div class="boxdocspace-inner">
                <div class="boxdocspace-inner-border">
                    <div class="floatright">
                                <a href="#" id="actions" title="Delete" class="delete"><span>Delete this file </span><img src="${url.currentModule}/css/img/delete.png"/></a>
                    </div>
                    <div class="imagefloatleft">
                        <div class="itemImage itemImageLeft"><a
                                href="${currentNode.url}"><img title="Download" value="download" src="${url.currentModule}/css/img/documentbig.png"/></a>
                        </div>
                    </div>
                    <h3><fmt:message key="docspace.label.document.name"/> : <a href="${currentNode.url}"><img title="Download" value="download" src="${url.currentModule}/css/img/download.png"/>  ${currentNode.name}</a></h3>

                    <p class="clearMaringPadding docspacedate "><fmt:message key="label.created"/> : <fmt:formatDate
                            value="${currentNode.properties['jcr:created'].time}" pattern="yyyy/MM/dd"/>, <fmt:message
                            key="docspace.label.document.createdBy"/> : <span class="author"><a
                            href="${url.base}/users/${currentNode.properties['jcr:createdBy'].string}.html">${currentNode.properties['jcr:createdBy'].string}</a></span>
                    </p>

                    <p class="clearMaringPadding docspacedate"><fmt:message
                            key="docspace.label.document.lastModification"/> : <fmt:formatDate
                            value="${currentNode.properties['jcr:lastModified'].time}" pattern="yyyy/MM/dd"/>,
                        <fmt:message key="docspace.label.document.createdBy"/> : <span class="author"><a
                                href="${url.base}/users/${currentNode.properties['jcr:lastModifiedBy'].string}.html">${currentNode.properties['jcr:lastModifiedBy'].string}</a></span>
                    </p>
                    <div class="clear"></div>
                   <template:option node="${currentNode}" template="hidden.tags"
                                        nodetype="jmix:tagged"/><template:option node="${currentNode}"
                                                                                      template="hidden.addTag"
                                                                                      nodetype="jmix:tagged"/>
<hr/>
                    <template:option node="${currentNode}" template="hidden.average" nodetype="jmix:rating"/>
                    <!--stop boxdocspace -->
                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
    <!--stop boxdocspace -->

    <h4 class="boxdocspace-title2">Document history</h4>
    
        <div class="boxdocspace"><!--start boxdocspace -->
        <div class="boxdocspacegrey boxdocspacepadding10 ">
            <div class="boxdocspace-inner">
                <div class="boxdocspace-inner-border">
                                

                                <form action="${url.base}${currentNode.parent.path}/*" method="POST" name="uploadFile"
                                      enctype="multipart/form-data">
                                        <input type="hidden" name="nodeType" value="jnt:file"/>
                                        <input type="hidden" name="redirectTo"
                                               value="${url.base}${renderContext.mainResource.node.path}"/>
                                        <input type="hidden" name="newNodeOutputFormat" value="docspace.html"/>
                                        <input type="hidden" name="targetDirectory" value="${currentNode.parent.path}"/>
                                        <input type="hidden" name="jcr:mixinTypes" value="jmix:comments"/>
                                        <input type="hidden" name="jcr:mixinTypes" value="jmix:tagged"/>
                                        <input type="hidden" name="jcr:mixinTypes" value="jnt:docspaceFile"/>
                                        <input type="hidden" name="jcr:mixinTypes" value="jmix:rating"/>
                                        <input type="hidden" name="jcr:mixinTypes" value="mix:title"/>
                                        <input type="hidden" name="version" value="true"/>
										<span><strong><fmt:message key="docspace.label.document.add.version"/></strong></span>

                                            <input type="file" name="file">
                                            <input class="button" type="submit" id="upload" value="Upload"/>
                                </form>
              <div class="clear"></div></div>
            </div>
        </div>
    </div>

                            <div class="boxdocspace">
                                <div class="boxdocspacepadding10 boxdocspacemarginbottom16">
                                    <div class="boxdocspace-inner">
                                        <div class="boxdocspace-inner-border"><!--start boxdocspace -->
                                        
                                            <template:option nodetype="jmix:comments" template="hidden.options.wrapper" node="${currentNode}"/>
                        
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <!--stop boxdocspace -->

    <div class='clear'></div>
</div>
<!--stop grid_12-->


<div class='grid_4'><!--start grid_4-->
    <h4 class="boxdocspace-title">Description</h4>

    <div class="boxdocspace"><!--start boxdocspace -->
        <div class="boxdocspacepadding16 boxdocspacemarginbottom16">
            <div class="boxdocspace-inner">
                <div class="boxdocspace-inner-border">
                    <p class="clearMaringPadding">
                        <span jcr:id="jcr:description" id="ckeditorEditDescription"
                              jcr:url="${url.base}${currentNode.path}">
                        <c:if test="${not empty currentNode.properties['jcr:description'].string}">${currentNode.properties['jcr:description'].string}</c:if>
                        <c:if test="${empty currentNode.properties['jcr:description'].string}">Add a description (click here)</c:if>
                    </span>

                    </p>

                    <div class="clear"></div>
                </div>
            </div>
        </div>
    </div>
    <!--stop boxdocspace -->

    <h4 class="boxdocspace-title">Versions</h4>
    <ul class="docspacelist docspacelistversion">
        <c:set var="checkPublishedVersion" value="true"/>
        <c:forEach items="${functions:reverse(currentNode.versionsAsVersion)}" var="version" varStatus="status">
            <li>
                <c:if test="${checkPublishedVersion}">
                    <c:set var="publishedVersion" value="false"/>
                    <c:forEach items="${functions:reverse(currentNode.versionInfos)}" var="versionInfo">
                        <c:if test="${not empty versionInfo.checkinDate}">
                            <c:if test="${version.created.time.time <= versionInfo.checkinDate.time.time}">
                                <c:set var="publishedVersion" value="true"/>
                            </c:if>
                        </c:if>
                    </c:forEach>
                </c:if>
                <img class="floatleft" alt="user default icon" src="${url.currentModule}/css/img/version.png"/>
                <c:choose>
                    <c:when test="${jcr:hasPermission(currentNode, 'write')}">
                        <a href="${currentNode.url}?v=${version.name}">Version ${version.name}</a>
                    </c:when>
                    <c:when test="${publishedVersion}">
                        <a href="${currentNode.url}">Version ${version.name}</a>
                    </c:when>
                    <c:otherwise>
                        Version ${version.name}
                    </c:otherwise>
                </c:choose>
                <p class="docspacedate"><fmt:formatDate
                        value="${version.created.time}" pattern="yyyy/MM/dd HH:mm"/>
                    <c:if test="${publishedVersion eq 'true'}">
                        &nbsp;(published)
                        <c:set var="checkPublishedVersion" value="false"/>
                        <c:set var="publishedVersion" value="false"/>
                    </c:if>
                </p>

                <div class='clear'></div>
            </li>
        </c:forEach>
    </ul>
    <form method="POST" name="publishFile" action="${url.base}${currentNode.path}.publishFile.do">
        <input class="button" type="submit" value="<fmt:message key="docspace.label.document.publish"/>"/>
    </form>
</div>
<!--stop grid_4-->

