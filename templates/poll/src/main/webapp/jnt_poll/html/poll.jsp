<%@ taglib prefix="jcr" uri="http://www.jahia.org/tags/jcr" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="utility" uri="http://www.jahia.org/tags/utilityLib" %>
<%@ taglib prefix="template" uri="http://www.jahia.org/tags/templateLib" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<template:addResources type="javascript" resources="jquery.min.js,jquery.validate.js,jquery.maskedinput-1.2.2.js"/>
<template:addResources type="css" resources="poll.css"/>

<script type="text/javascript">
function doVote(answers) {

    var answersList = document.forms['form_${currentNode.name}'].voteAnswer;
    answerUUID = null;

    for (i=0; i< answersList.length; i++) {
    	answer = answersList[i];
    	if (answer.checked) {
    		answerUUID = answer.value;
    		break;
    	}
    }


    if (answerUUID == null) {
        alert("Please select an answer");
    }

    var data = {};
    data["answerUUID"] = answerUUID;
    $.post("${url.base}${currentNode.path}.vote.do", data, function(result) {


        var answers = result.answerNodes;
        /* strAnswers = "";
        for (i=0; i<answers.length; i++) {
            strAnswers += "\nAnswer["+[i]+"] label : " + answers[i].label + "\nAnswer["+[i]+"] votes: " + answers[i].nbOfVotes;
        }

        alert("Question: " + result.question + "\nTotal votes: " + result.totalOfVotes + "\nanswers: " + strAnswers);
           */

	statDivTest = document.getElementById("statContainer_${currentNode.name}");
	if (statDivTest != null) {
	    statDivTest.parentNode.removeChild(statDivTest);
	}


        var statDiv = document.createElement("div");
        statDiv.id = "statContainer_${currentNode.name}";
        // statDiv.style.zIndex = 99999;
	pollVotes = Math.floor(result.totalOfVotes);

        for (i=0; i<answers.length; i++) {
            var statAnswerLabel = document.createElement("div");
            statAnswerLabel.id = "statContainer_${currentNode.name}_label_a"+[i];
            statAnswerLabel.innerHTML = answers[i].label;


            var statAnswerValue = document.createElement("div");
            statAnswerValue.id = "statContainer_${currentNode.name}_value_a"+[i];
            statAnswerValue.innerHTML = answers[i].nbOfVotes;
	    answerVotes = Math.floor(answers[i].nbOfVotes);
	    percentage = (answerVotes == 0 || pollVotes == 0)?0:answerVotes/pollVotes*100;
            statAnswerValue.style.width = (percentage * 5) + "px";
            statAnswerValue.style.backgroundColor = "#3399CC";

            statDiv.appendChild(statAnswerLabel);
            statDiv.appendChild(statAnswerValue);

        }

       document.getElementById("stats_${currentNode.name}").appendChild(statDiv);


    }, "json");


}

</script>

<div class=poll>

    <h3>
        ${currentNode.propertiesAsString['question']}
    </h3>


    <c:if test="${not renderContext.editMode}">
        <div id="formContainer_${currentNode.name}">
        <form id="form_${currentNode.name}" name="form_${currentNode.name}" method="post" >
    </c:if>
            <c:if test="${renderContext.editMode}">
                <div class="addanswers">
                <span>Add the answers here</span>
            </c:if>

            <template:area path="${currentNode.path}/answers" nodeTypes="jnt:answer" editable="true"/>

            <c:if test="${renderContext.editMode}">
                </div>
            </c:if>

    <c:if test="${not renderContext.editMode}">
        <div class="validation"></div>
        <input type="button" value="Vote" onclick="doVote($('${currentNode.name}_voteAnswer').value);" />
        </form>
        </div>
    </c:if>

    <div id="stats_${currentNode.name}">

    </div>
</div>
