package org.jahia.ajax.gwt.client.widget.edit;

import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.List;
import java.util.ArrayList;

import org.jahia.ajax.gwt.client.data.node.GWTJahiaNode;
import org.jahia.ajax.gwt.client.service.content.JahiaContentManagementService;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Aug 19, 2009
 * Time: 7:02:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class EditModeDNDListener extends DNDListener {
    private TreePanelDragSource contentTreeSource;
    private GridDragSource createGridSource;
    private GridDragSource displayGridSource;

    public void setContentTreeSource(TreePanelDragSource contentTreeSource) {
        this.contentTreeSource = contentTreeSource;
        contentTreeSource.addDNDListener(this);
    }

    public void setCreateGridSource(GridDragSource createGridSource) {
        this.createGridSource = createGridSource;
        createGridSource.addDNDListener(this);
    }

    public void setDisplayGridSource(GridDragSource displayGridSource) {
        this.displayGridSource = displayGridSource;
        displayGridSource.addDNDListener(this);
    }



    @Override
    public void dragMove(DNDEvent e) {
        super.dragMove(e);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void dragStart(DNDEvent e) {
        if (e.getSource() == contentTreeSource) {
            e.getStatus().setData("sourceType", "content");

            List list = (List) e.getData();
            e.getStatus().setData("size", list.size());

            List<GWTJahiaNode> l = new ArrayList<GWTJahiaNode>();
            for (Object o : list) {
                l.add((GWTJahiaNode) ((BaseTreeModel) o).get("model"));
            }
            e.getStatus().setData("sourceNodes", l);

        }

        super.dragStart(e);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void dragEnter(DNDEvent e) {
        if (e.getDropTarget().getComponent() instanceof PlaceholderModule) {
            e.getStatus().setData("targetType", "placeholder");
            e.getStatus().setData("targetPath", ((PlaceholderModule)e.getDropTarget().getComponent()).getPath());
        }
        super.dragEnter(e);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void dragLeave(DNDEvent e) {
        super.dragLeave(e);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public void dragDrop(DNDEvent e) {
        if ("placeholder".equals(e.getStatus().getData("targetType"))) {
            if ("content".equals(e.getStatus().getData("sourceType"))) {
                List<GWTJahiaNode> nodes = (List<GWTJahiaNode>) e.getStatus().getData("sourceNodes");
                String path = e.getStatus().getData("targetPath");
                int i = path.lastIndexOf('/');
                String name = path.substring(i +1);
                path = path.substring(0,i);

                if ("*".equals(name)) {
                    JahiaContentManagementService.App.getInstance().pasteReferences(nodes, path, new AsyncCallback() {
                        public void onSuccess(Object result) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public void onFailure(Throwable caught) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });

                } else if (nodes.size() == 1) {
                    JahiaContentManagementService.App.getInstance().pasteReference(nodes.get(0), path, name, new AsyncCallback() {
                        public void onSuccess(Object result) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }

                        public void onFailure(Throwable caught) {
                            //To change body of implemented methods use File | Settings | File Templates.
                        }
                    });

                }

            }
        }
        Log.info("xx"+e.getStatus().getData("sourceNode"));
        super.dragDrop(e);    //To change body of overridden methods use File | Settings | File Templates.
    }
}
