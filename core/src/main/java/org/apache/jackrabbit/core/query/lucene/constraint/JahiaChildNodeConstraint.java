package org.apache.jackrabbit.core.query.lucene.constraint;

import org.apache.jackrabbit.core.id.NodeId;
import org.apache.jackrabbit.core.query.lucene.FieldNames;
import org.apache.jackrabbit.core.query.lucene.JahiaNodeIndexer;
import org.apache.jackrabbit.core.query.lucene.ScoreNode;
import org.apache.jackrabbit.core.query.lucene.constraint.ChildNodeConstraint;
import org.apache.jackrabbit.core.query.lucene.constraint.EvaluationContext;
import org.apache.jackrabbit.spi.Name;
import org.apache.jackrabbit.spi.commons.query.qom.ChildNodeImpl;
import org.apache.jackrabbit.spi.commons.query.qom.SelectorImpl;
import org.apache.lucene.document.Document;

import javax.jcr.RepositoryException;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: toto
 * Date: Aug 2, 2010
 * Time: 6:47:17 PM
 * 
 */
public class JahiaChildNodeConstraint extends ChildNodeConstraint {

    public JahiaChildNodeConstraint(ChildNodeImpl constraint,
                               SelectorImpl selector) throws RepositoryException {
        super(constraint, selector);
    }

    @Override public boolean evaluate(ScoreNode[] row, Name[] selectorNames, EvaluationContext context)
            throws IOException {
        ScoreNode sn = row[getSelectorIndex(selectorNames)];
        if (sn == null) {
            return false;
        }

        sn.getDoc(context.getIndexReader());
        Document doc = context.getIndexReader().document(sn.getDoc(context.getIndexReader()));
        NodeId baseNode = getBaseNodeId(context);
        if (baseNode == null) {
            return false;
        } else {
            final String id = baseNode.toString();
            String parentId = doc.get(FieldNames.PARENT);
            String translatedNodeParentId = doc.get(JahiaNodeIndexer.TRANSLATED_NODE_PARENT);        
            return id.equals(translatedNodeParentId) || (id.equals(parentId) && translatedNodeParentId == null);
        }
    }
}
