package org.jahia.ajax.gwt.client.data.job;

import com.extjs.gxt.ui.client.data.BaseModelData;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Sep 17, 2010
 * Time: 2:13:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class GWTJahiaJobDetail extends BaseModelData {

    public GWTJahiaJobDetail() {
    }

    public GWTJahiaJobDetail(String name, String type, Date creationTime) {
        setName(name);
        setType(type);
        setCreationTime(creationTime);
    }

    public String getType() {
        return get("type");
    }

    public void setType(String type) {
        set("type", type);
    }

    public String getLabel() {
        return get("label");
    }

    public void setLabel(String label) {
        set("label", label);
    }

    public String getName() {
        return get("name");
    }

    public void setName(String name) {
        set("name", name);
    }

    public Date getStartTime() {
        return get("startTime");
    }

    public void setCreationTime(Date creationTime) {
        set("creationTime", creationTime);
    }
}
