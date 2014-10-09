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

import de.erdesignerng.dialect.Statement;
import de.erdesignerng.dialect.StatementList;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author mirkosertic
 */
public class RepositoryEntity extends ModelEntity {

    private String dialect;

    private List<DomainEntity> domains = new ArrayList<>();

    private List<CustomTypeEntity> customTypes = new ArrayList<>();

    private List<TableEntity> tables = new ArrayList<>();

    private List<RelationEntity> relations = new ArrayList<>();

    private List<CommentEntity> comments = new ArrayList<>();

    private List<ViewEntity> views = new ArrayList<>();

    private List<SubjectAreaEntity> subjectAreas = new ArrayList<>();

    private List<ChangeEntity> changes = new ArrayList<>();

    public List<DomainEntity> getDomains() {
        return domains;
    }

    public void setDomains(List<DomainEntity> domains) {
        this.domains = domains;
    }

    public List<TableEntity> getTables() {
        return tables;
    }

    public void setTables(List<TableEntity> tables) {
        this.tables = tables;
    }

    public List<RelationEntity> getRelations() {
        return relations;
    }

    public void setRelations(List<RelationEntity> relations) {
        this.relations = relations;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(List<CommentEntity> comments) {
        this.comments = comments;
    }

    public List<SubjectAreaEntity> getSubjectareas() {
        return subjectAreas;
    }

    public void setSubjectareas(List<SubjectAreaEntity> aAreas) {
        subjectAreas = aAreas;
    }

    public List<ChangeEntity> getChanges() {
        return changes;
    }

    public void setChanges(List<ChangeEntity> changes) {
        this.changes = changes;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(String dialect) {
        this.dialect = dialect;
    }

    public List<ViewEntity> getViews() {
        return views;
    }

    public void setViews(List<ViewEntity> views) {
        this.views = views;
    }

    public List<CustomTypeEntity> getCustomType() {
        return customTypes;
    }

    public void setCustomType(List<CustomTypeEntity> customType) {
        customTypes = customType;
    }

    /**
     * Create a changelog.
     *
     * @param aSource      the source change ( included )
     * @param aDestination the destination change ( included )
     * @return the statements
     */
    public StatementList createChangeLog(ChangeEntity aSource, ChangeEntity aDestination) {
        StatementList theStatements = new StatementList();

        int a = changes.indexOf(aSource);
        int b = changes.indexOf(aDestination);
        for (int i = a; i <= b; i++) {
            ChangeEntity theChange = changes.get(i);
            theStatements.addAll(theChange.getStatements().stream().map(Statement::new).collect(Collectors.toList()));
        }

        return theStatements;
    }
}