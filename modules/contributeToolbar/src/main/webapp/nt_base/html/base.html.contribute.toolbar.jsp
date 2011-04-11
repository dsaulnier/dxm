<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="workflow" uri="http://www.jahia.org/tags/workflow" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="utils" uri="http://www.jahia.org/tags/utilityLib" %>
<%--@elvariable id="currentNode" type="org.jahia.services.content.JCRNodeWrapper"--%>
<%--@elvariable id="propertyDefinition" type="org.jahia.services.content.nodetypes.ExtendedPropertyDefinition"--%>
<%--@elvariable id="type" type="org.jahia.services.content.nodetypes.ExtendedNodeType"--%>
<%--@elvariable id="out" type="java.io.PrintWriter"--%>
<%--@elvariable id="script" type="org.jahia.services.render.scripting.Script"--%>
<%--@elvariable id="scriptInfo" type="java.lang.String"--%>
<%--@elvariable id="workspace" type="java.lang.String"--%>
<%--@elvariable id="renderContext" type="org.jahia.services.render.RenderContext"--%>
<%--@elvariable id="currentResource" type="org.jahia.services.render.Resource"--%>
<%--@elvariable id="url" type="org.jahia.services.render.URLGenerator"--%>
<template:addResources type="javascript" resources="jquery.js,jquery-ui.min.js,jquery.fancybox.js"/>
<template:addResources type="css" resources="contribute-toolbar.css,jquery.fancybox.css"/>
<template:addResources>
    <script>
        var contributeParams = new Array();
        $.ajaxSetup({
            accepts: {
                script: "application/json"
            }
        })
        /*
         $("#delete-${currentNode.identifier}").button();
         $("#copy-${currentNode.identifier}").button();
         $("#paste-${currentNode.identifier}").button();
         */
        function getUuids() {
            var uuids = new Array();
            var i = 0;
            $("input:checked").each(function(index) {
                uuids[i++] = $(this).attr("name");
            });
            return uuids;
        }

        function reload() {
            for(var i=0; i < contributeParams.length; i++ ){

                $("#" + contributeParams[i].contributeReplaceTarget).load(contributeParams[i].contributeReplaceUrl, '', null);
            }
        }

        function deleteNodes() {
            var uuids = getUuids();
            if (uuids.length > 0) {
                $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.deleteNodes.do'/>", {"uuids": uuids}, function(result) {
                    reload();
                }, "json");
            }
        }

        function copyNodes() {
            var uuids = getUuids();
            if (uuids.length > 0) {
                $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.copy.do'/>", {"uuids": uuids}, function(result) {
                    showClipboard();
                }, "json");
            }
        }

        function cutNodes() {
            var uuids = getUuids();
            if (uuids.length > 0) {
                $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.cut.do'/>", {"uuids": uuids}, function(result) {
                    showClipboard();
                }, "json");
            }
        }

        function publishNodes() {
            var uuids = getUuids();
            if (uuids.length > 0) {
                $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.publishNodes.do'/>", {"uuids": uuids}, function(result) {
                    reload();
                }, "json");
            }
        }

        function pasteNodes(contributeParams) {
            $.post("<c:url value='${url.base}'/>"+contributeParams.contributeTarget+".paste.do", {}, function(result) {
                reload();
                hideClipboard();
            }, "json");
        }

        function emptyClipboard() {
            $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.emptyclipboard.do'/>", {}, function(result) {
                hideClipboard();
            }, "json");
        }

        function showClipboard() {
            $.post("<c:url value='${url.base}${renderContext.mainResource.node.path}.checkclipboard.do'/>", {}, function(data) {
                if (data != null && data.size > 0) {
                    $(".titleaddnewcontent").show();
                    $(".pastelink").show();
                    $("#empty-${currentNode.identifier}").show();
                    $("#clipboard-${currentNode.identifier}").html('<fmt:message key="label.clipboard.contains"/> ' + data.size +
                            ' element(s)</span></a>');
                    $("#clipboard-${currentNode.identifier}").show();
                    $("#clipboardpreview-${currentNode.identifier}").empty();
                    var paths = data.paths;
                    for (var i = 0; i < paths.length; i++) {
                        $.get("<c:url value='${url.base}'/>" + paths[i] + ".html.ajax", {}, function(result) {
                            $("#clipboardpreview-${currentNode.identifier}").append("<div style='border:thin'>");
                            $("#clipboardpreview-${currentNode.identifier}").append(result);
                            $("#clipboardpreview-${currentNode.identifier}").append("</div>");
                        }, "html")
                    }
                    $("#clipboard-${currentNode.identifier}").fancybox();
                }
            }, "json");
        }

        function hideClipboard() {
            $(".titleaddnewcontent").hide();
            $(".pastelink").hide();
            $("#empty-${currentNode.identifier}").hide();
            $("#clipboard-${currentNode.identifier}").hide();
        }

        function onresizewindow() {
            h = document.documentElement.clientHeight - $("#contributeToolbar").height();
            $("#contributewrapper").attr("style","position:relative; overflow:auto; height:"+ h +"px");
        }

        $(document).ready(function() {
            $(".fancylink").fancybox({
                'titleShow' : false,
                'autoDimensions' : false,
                'width' : 800,
                'height' : 600,
                'onComplete' : function() {
                    animatedcollapse.init();
                }
            });
        });

    </script>
</template:addResources>
<utils:setBundle basename="JahiaContributeToolbar" useUILocale="true" templateName="Jahia Contribute Toolbar"/>
<div id="contributeToolbar" >

    <div id="edit">
        <a href="<c:url value='${url.live}'/>" ><img src="<c:url value='/icons/live.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.live"/></a>
        <a href="<c:url value='${url.preview}'/>" ><img src="<c:url value='/icons/preview.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.preview"/></a>
        <a href="<c:url value='${url.edit}'/>"><img src="<c:url value='/icons/editMode.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.editMode"/></a>
        <span> </span>
        <a href="#" id="delete-${currentNode.identifier}" onclick="deleteNodes();"><img src="<c:url value='/icons/delete.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.delete"/></a>
        <a href="#" id="copy-${currentNode.identifier}" onclick="copyNodes();"><img src="<c:url value='/icons/copy.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.copy"/></a>
        <a href="#" id="cut-${currentNode.identifier}" onclick="cutNodes();"><img src="<c:url value='/icons/cut.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.cut"/></a>
        <a href="#" id="publish-${currentNode.identifier}" onclick="publishNodes();"><img src="<c:url value='/icons/publish.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.publication"/></a>
        <a href="#" id="empty-${currentNode.identifier}" onclick="emptyClipboard();" style="display:none;"><img src="<c:url value='/icons/clipboard.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.clipboard.reset"/></a>
        <a href="#clipboardpreview-${currentNode.identifier}" id="clipboard-${currentNode.identifier}" style="display:none;"><img src="<c:url value='/icons/clipboard.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.clipboard.contains"/></a>
        <a href="<c:url value='${url.basePreview}/users/${renderContext.user.username}.contributeTasklist.html.ajax'/>" class="fancylink"><img src="<c:url value='/icons/user.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                key="label.goto.myTasks"/></a>
        <c:choose>
            <c:when test="${jcr:isNodeType(currentNode, 'jnt:folder') || jcr:isNodeType(currentNode, 'nt:file')}">
                <c:url var="mgrUrl" value="/engines/manager.jsp">
                    <c:param name="conf" value="filemanager"/>
                    <c:param name="site" value="${renderContext.site.identifier}"/>
                    <c:param name="selectedPaths" value="${currentNode.path}"/>
                </c:url>
                <a href="${mgrUrl}" target="_blank"><img src="<c:url value='/icons/fileManager.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                        key="label.filemanager"/></a>
            </c:when>
            <c:otherwise>
                <c:set var="contentPath" value="${currentNode.resolveSite.path}/contents"/>
                <c:if test="${fn:startsWith(currentNode.path,contentPath)}">
                <c:url var="mgrUrl" value="/engines/manager.jsp">
                    <c:param name="conf" value="editorialcontentmanager"/>
                    <c:param name="site" value="${renderContext.site.identifier}"/>
                    <c:param name="selectedPaths" value="${currentNode.path}"/>
                </c:url>
                    <a href="${mgrUrl}" target="_blank"><img src="<c:url value='/icons/treepanel-content-manager-1616.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message
                            key="label.contentManager"/></a>
                </c:if>
            </c:otherwise>
        </c:choose>
        <span><fmt:message key="label.goto"/>: </span> <a href="<c:url value='${url.base}${currentNode.resolveSite.path}/home.html'/>"><img src="<c:url value='/icons/siteManager.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.siteHomepage"/></a>
        <a href="<c:url value='${url.base}${currentNode.resolveSite.path}/contents.html'/>"><img src="<c:url value='/icons/content-manager-1616.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.siteContent"/></a>
        <a href="<c:url value='${url.base}${currentNode.resolveSite.path}/files.html'/>"><img src="<c:url value='/icons/fileManager.png'/>" width="16" height="16" alt=" " role="presentation" style="position:relative; top: 4px; margin-right:2px; "><fmt:message key="label.siteFiles"/></a>
    </div>
    <div style="display:none;">
        <div id="clipboardpreview-${currentNode.identifier}">
        </div>
    </div>
</div>

<div style="display:none;">
    <div id="tasks" >
        <%-- Just load the resources here ! --%>
        <template:module path="/users/${renderContext.user.username}" view="contributeTasklist" var="temp"/>
    </div>
</div>
<div id="contributewrapper">