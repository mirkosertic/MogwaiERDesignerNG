/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.model.serializer.repository.entities;

import java.util.ArrayList;
import java.util.List;

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;

/**
 * @author msertic
 */
public class RepositoryEntity extends ModelEntity {

    private String dialect;

    private List<DomainEntity> domains = new ArrayList<DomainEntity>();

    private List<TableEntity> tables = new ArrayList<TableEntity>();

    private List<RelationEntity> relations = new ArrayList<RelationEntity>();

    private List<CommentEntity> comments = new ArrayList<CommentEntity>();

    private List<ViewEntity> views = new ArrayList<ViewEntity>();

    private List<SubjectAreaEntity> subjectareas = new ArrayList<SubjectAreaEntity>();

    private List<ChangeEntity> changes = new ArrayList<ChangeEntity>();

    /**
     * @return the domains
     */
    public List<DomainEntity> getDomains() {
        return domains;
    }

    /**
     * @param domains
     *                the domains to set
     */
    public void setDomains(List<DomainEntity> domains) {
        this.domains = domains;
    }

    /**
     * @return the tables
     */
    public List<TableEntity> getTables() {
        return tables;
    }

    /**
     * @param tables
     *                the tables to set
     */
    public void setTables(List<TableEntity> tables) {
        this.tables = tables;
    }

    /**
     * @return the relations
     */
    public List<RelationEntity> getRelations() {
        return relations;
    }

    /**
     * @param relations
     *                the relations to set
     */
    public void setRelations(List<RelationEntity> relations) {
        this.relations = relations;
    }

    /**
     * @return the comments
     */
    public List<CommentEntity> getComments() {
        return comments;
    }

    /**
     * @param comments
     *                the comments to set
     */
    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    /**
     * @return the subjectareas
     */
    public List<SubjectAreaEntity> getSubjectareas() {
        return subjectareas;
    }

    /**
     * @param subjectareas
     *                the subjectareas to set
     */
    public void setSubjectareas(List<SubjectAreaEntity> subjectareas) {
        this.subjectareas = subjectareas;
    }

    /**
     * @return the changes
     */
    public List<ChangeEntity> getChanges() {
        return changes;
    }

    /**
     * @param changes
     *                the changes to set
     */
    public void setChanges(List<ChangeEntity> changes) {
        this.changes = changes;
    }

    /**
     * @return the dialect
     */
    public String getDialect() {
        return dialect;
    }

    /**
     * @param dialect
     *                the dialect to set
     */
    public void setDialect(String dialect) {
        this.dialect = dialect;
    }
    
    public List<ViewEntity> getViews() {
        return views;
    }

    public void setViews(List<ViewEntity> views) {
        this.views = views;
    }

    /**
     * Create a changelog.
     * 
     * @param aSource
     *                the source change ( included )
     * @param aDestination
     *                the destination change ( included )
     * @return the statements
     */
    public StatementList createChangeLog(ChangeEntity aSource, ChangeEntity aDestination) {
        StatementList theStatements = new StatementList();

        int a = changes.indexOf(aSource);
        int b = changes.indexOf(aDestination);
        for (int i = a; i <= b; i++) {
            ChangeEntity theChange = changes.get(i);
            for (String theStm : theChange.getStatements()) {
                theStatements.add(new Statement(theStm));
            }
        }

        return theStatements;
    }
}