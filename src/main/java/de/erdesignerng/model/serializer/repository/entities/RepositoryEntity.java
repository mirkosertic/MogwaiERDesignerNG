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

    public void setDomains(final List<DomainEntity> domains) {
        this.domains = domains;
    }

    public List<TableEntity> getTables() {
        return tables;
    }

    public void setTables(final List<TableEntity> tables) {
        this.tables = tables;
    }

    public List<RelationEntity> getRelations() {
        return relations;
    }

    public void setRelations(final List<RelationEntity> relations) {
        this.relations = relations;
    }

    public List<CommentEntity> getComments() {
        return comments;
    }

    public void setComments(final List<CommentEntity> comments) {
        this.comments = comments;
    }

    public List<SubjectAreaEntity> getSubjectareas() {
        return subjectAreas;
    }

    public void setSubjectareas(final List<SubjectAreaEntity> aAreas) {
        subjectAreas = aAreas;
    }

    public List<ChangeEntity> getChanges() {
        return changes;
    }

    public void setChanges(final List<ChangeEntity> changes) {
        this.changes = changes;
    }

    public String getDialect() {
        return dialect;
    }

    public void setDialect(final String dialect) {
        this.dialect = dialect;
    }

    public List<ViewEntity> getViews() {
        return views;
    }

    public void setViews(final List<ViewEntity> views) {
        this.views = views;
    }

    public List<CustomTypeEntity> getCustomType() {
        return customTypes;
    }

    public void setCustomType(final List<CustomTypeEntity> customType) {
        customTypes = customType;
    }

    /**
     * Create a changelog.
     *
     * @param aSource      the source change ( included )
     * @param aDestination the destination change ( included )
     * @return the statements
     */
    public StatementList createChangeLog(final ChangeEntity aSource, final ChangeEntity aDestination) {
        final StatementList theStatements = new StatementList();

        final int a = changes.indexOf(aSource);
        final int b = changes.indexOf(aDestination);
        for (int i = a; i <= b; i++) {
            final ChangeEntity theChange = changes.get(i);
            theStatements.addAll(theChange.getStatements().stream().map(Statement::new).collect(Collectors.toList()));
        }

        return theStatements;
    }
}