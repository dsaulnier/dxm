<%@ include file="../../common/declarations.jspf" %>

<template:container id="peopleContainer" displayActionMenu="true">

	<div class="peopleListItem"><!--start peopleListItem -->
        <template:field name="peoplePicture" valueBeanID="peoplePicture" display="false"/>
		<div class="peoplePhoto">
        <template:field name="peopleLastname" display="false" valueBeanID="peopleLastname"/>
        <img src="${peoplePicture.thumbnailUrl}" alt="${peopleLastname} picture"></div>
        <div class="peopleBody"><!--start eventsBody -->
            <h5><template:field name="peopleFirstname"/>&nbsp;<template:field name="peopleLastname"/></h5>
            <p class="peopleFonction"><template:field name="peopleFunction"/></p>
            <p class="peopleBusinessUnit"><template:field name="peopleBusinessUnit"/></p>

                <template:field name="peopleEmail" display="false" valueBeanID="email"/>
				<p class="peopleEmail"><a href='mailto:${email}'><template:field name="peopleEmail"/></a></p>

				<div class="peopleAction">
<a class="peopleDownload" href="${peoplePicture.downloadUrl}" target="_blank">View full size</a>
<a class="peopleBiographiy" href="javascript:;" onclick="ShowHideLayer(${peopleContainer.ID});"><utility:resourceBundle
                            resourceName='web_templates_peopleContainer.peopleBiography' defaultValue='web_templates_peopleContainer.peopleBiography'/></a>
				</div>
				<div id="collapseBox${peopleContainer.ID}" class="collapsible"><!--start collapsible -->
							 <template:field name="peopleBiography"/>
				</div><!--stop collapsible -->
					<div class="clear"></div></div><!--stop peopleBody -->
				<div class="clear"></div></div><!--stop peopleListItem -->

</template:container>


<!-- unused fields <template:field name="peopleTelephone"/> <template:field name="peopleCellular"/> <template:field name="peopleFax"/> -->



