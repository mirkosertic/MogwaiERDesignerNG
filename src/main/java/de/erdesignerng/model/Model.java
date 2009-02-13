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
package de.erdesignerng.model;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;

import de.erdesignerng.dialect.DataType;
import de.erdesignerng.dialect.DataTypeList;
import de.erdesignerng.dialect.Dialect;
import de.erdesignerng.dialect.DialectFactory;
import de.erdesignerng.exception.CannotDeleteException;
import de.erdesignerng.exception.ElementAlreadyExistsException;
import de.erdesignerng.exception.ElementInvalidNameException;
import de.erdesignerng.modificationtracker.EmptyModelModificationTracker;
import de.erdesignerng.modificationtracker.ModelModificationTracker;
import de.erdesignerng.modificationtracker.VetoException;
import de.erdesignerng.util.ApplicationPreferences;
import de.erdesignerng.util.ConnectionDescriptor;

/**
 * 
 * @author $Author: mirkosertic $
 * @version $Date: 2009-02-13 18:47:14 $
 */
public class Model implements OwnedModelItemVerifier {

    public static final String PROPERTY_DRIVER = "DRIVER";

    public static final String PROPERTY_URL = "URL";

    public static final String PROPERTY_ALIAS = "ALIAS";

    public static final String PROPERTY_USER = "USER";

    public static final String PROPERTY_PASSWORD = "PASSWORD";

    private TableList tables = new TableList();

    private RelationList relations = new RelationList();

    private SubjectAreaList subjectAreas = new SubjectAreaList();

    private CommentList comments = new CommentList();

    private DomainList domains = new DomainList();

    private ViewList views = new ViewList();

    private Dialect dialect;

    private ModelProperties properties = new ModelProperties();

    private ModelModificationTracker modificationTracker = new EmptyModelModificationTracker();

    /**
     * Add a table to the database model.
     * 
     * @param aTable
     *                the table
     * @throws ElementAlreadyExistsException
     *                 is thrown in case of an error
     * @throws ElementInvalidNameException
     *                 is thrown in case of an error
     * @throws VetoException
     *                 if there is a veto for doing this
     */
    public void addTable(Table aTable) throws ElementAlreadyExistsException, ElementInvalidNameException, VetoException {

        modificationTracker.addTable(aTable);

        if (dialect != null) {
            ModelUtilities.checkNameAndExistance(tables, aTable, dialect);

            for (Attribute theAttribute : aTable.getAttributes()) {
                theAttribute.setName(dialect.checkName(theAttribute.getName()));
            }
        }

        aTable.setOwner(this);
        tables.add(aTable);
    }

    /**
     * Add a relation to the database model.
     * 
     * @param aRelation
     *                the table
     * @throws ElementAlreadyExistsException
     *                 is thrown in case of an error
     * @throws ElementInvalidNameException
     *                 is thrown in case of an error
     * @throws VetoException
     *                 is thrown in case of an error
     */
    public void addRelation(Relation aRelation) throws ElementAlreadyExistsException, ElementInvalidNameException,
            VetoException {

        ModelUtilities.checkNameAndExistance(relations, aRelation, dialect);

        aRelation.setOwner(this);
        modificationTracker.addRelation(aRelation);

        relations.add(aRelation);
    }

    public void checkNameAlreadyExists(ModelItem aSender, String aName) throws ElementAlreadyExistsException {
        if (aSender instanceof Table) {
            ModelUtilities.checkExistance(tables, aName, dialect);
        }
    }

    public Dialect getDialect() {
        return dialect;
    }

    public void setDialect(Dialect aDialect) {
        dialect = aDialect;
    }

    public void delete(ModelItem aSender) throws CannotDeleteException {
        if (aSender instanceof Table) {

            Table theTable = (Table) aSender;

            if (relations.isTableInUse(theTable)) {
                throw new CannotDeleteException("Table is used by relations!");
            }

            tables.remove(theTable);

            return;
        }

        if (aSender instanceof Relation) {

            Relation theRelation = (Relation) aSender;

            relations.remove(theRelation);

            return;
        }

        throw new UnsupportedOperationException("Unknown element " + aSender);
    }

    public String checkName(String aName) throws ElementInvalidNameException {
        return dialect.checkName(aName);
    }

    public RelationList getRelations() {
        return relations;
    }

    public TableList getTables() {
        return tables;
    }

    public ModelProperties getProperties() {
        return properties;
    }

    /**
     * Create a JDBC database connection.
     * 
     * @param aPreferences
     *                the preferences
     * @return the database connection
     * @throws ClassNotFoundException
     *                 is thrown in case of an exception
     * @throws InstantiationException
     *                 is thrown in case of an exception
     * @throws IllegalAccessException
     *                 is thrown in case of an exception
     * @throws SQLException
     *                 is thrown in case of an exception
     */
    public Connection createConnection(ApplicationPreferences aPreferences) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, SQLException {
        Connection theConnection = getDialect().createConnection(aPreferences.createDriverClassLoader(),
                properties.getProperty(Model.PROPERTY_DRIVER), properties.getProperty(Model.PROPERTY_URL),
                properties.getProperty(Model.PROPERTY_USER), properties.getProperty(Model.PROPERTY_PASSWORD));
        return theConnection;
    }

    public boolean checkIfUsedAsForeignKey(Table aTable, Attribute aAttribute) {
        Attribute theRealAttribute = aTable.getAttributes().findBySystemId(aAttribute.getSystemId());
        return getRelations().isForeignKeyAttribute(theRealAttribute);
    }

    /**
     * Remove a table from the model.
     * 
     * @param aTable
     *                the table
     * @throws VetoException
     *                 will be thrown if the modificationtracker has a veto for
     *                 completing this operation
     */
    public void removeTable(Table aTable) throws VetoException {

        modificationTracker.removeTable(aTable);

        tables.remove(aTable);
        relations.removeByTable(aTable);

        subjectAreas.removeTable(aTable);
    }

    /**
     * Remove a relation from the model.
     * 
     * @param aRelation
     *                the relation
     * @throws VetoException
     *                 will be thrown if the modificationtracker has a veto for
     *                 completing this operation
     */
    public void removeRelation(Relation aRelation) throws VetoException {

        modificationTracker.removeRelation(aRelation);
        relations.remove(aRelation);
    }

    public void removeAttributeFromTable(Table aTable, Attribute aAttribute) throws VetoException {

        modificationTracker.removeAttributeFromTable(aTable, aAttribute);
        aTable.getAttributes().removeById(aAttribute.getSystemId());
    }

    public void removeIndex(Table aTable, Index aIndex) throws VetoException {

        if (IndexType.PRIMARYKEY.equals(aIndex.getIndexType())) {
            modificationTracker.removePrimaryKeyFromTable(aTable, aIndex);
        } else {
            modificationTracker.removeIndexFromTable(aTable, aIndex);
        }
        aTable.getIndexes().removeById(aIndex.getSystemId());
    }

    public void addAttributeToTable(Table aTable, Attribute aAttribute) throws VetoException,
            ElementAlreadyExistsException, ElementInvalidNameException {

        modificationTracker.addAttributeToTable(aTable, aAttribute);
        aTable.addAttribute(this, aAttribute);
    }

    public void changeAttribute(Attribute aExistantAttribute, Attribute aNewAttribute) throws Exception {

        modificationTracker.changeAttribute(aExistantAttribute, aNewAttribute);

        aExistantAttribute.restoreFrom(aNewAttribute);
    }

    public void addIndexToTable(Table aTable, Index aIndex) throws VetoException, ElementAlreadyExistsException,
            ElementInvalidNameException {

        if (IndexType.PRIMARYKEY.equals(aIndex.getIndexType())) {
            modificationTracker.addPrimaryKeyToTable(aTable, aIndex);
        } else {
            modificationTracker.addIndexToTable(aTable, aIndex);
        }

        aTable.addIndex(this, aIndex);
    }

    public void changeIndex(Index aExistantIndex, Index aNewIndex) throws Exception {

        modificationTracker.changeIndex(aExistantIndex, aNewIndex);

        aExistantIndex.restoreFrom(aNewIndex);
    }

    public void renameTable(Table aTable, String aNewName) throws VetoException {

        modificationTracker.renameTable(aTable, aNewName);

        aTable.setName(aNewName);
    }

    public void changeTableComment(Table aTable, String aNewComment) throws VetoException {

        modificationTracker.changeTableComment(aTable, aNewComment);

        aTable.setComment(aNewComment);
    }

    public void renameAttribute(Attribute aExistantAttribute, String aNewName) throws VetoException {
        modificationTracker.renameAttribute(aExistantAttribute, aNewName);

        aExistantAttribute.setName(aNewName);
    }

    public void changeRelation(Relation aRelation, Relation aTempRelation) throws Exception {

        modificationTracker.changeRelation(aRelation, aTempRelation);
        aRelation.restoreFrom(aTempRelation);
    }

    /**
     * @return the modificationTracker
     */
    public ModelModificationTracker getModificationTracker() {
        return modificationTracker;
    }

    /**
     * @param modificationTracker
     *                the modificationTracker to set
     */
    public void setModificationTracker(ModelModificationTracker modificationTracker) {
        this.modificationTracker = modificationTracker;
    }

    /**
     * Add a new subject area.
     * 
     * @param aArea
     *                the area
     */
    public void addSubjectArea(SubjectArea aArea) {
        subjectAreas.add(aArea);
    }

    /**
     * Remove a subject area.
     * 
     * @param aArea
     *                the area
     */
    public void removeSubjectArea(SubjectArea aArea) {
        subjectAreas.remove(aArea);
    }

    /**
     * @return the subjectAreas
     */
    public SubjectAreaList getSubjectAreas() {
        return subjectAreas;
    }

    /**
     * Create a connection history entry for the current loaded connection.
     * 
     * @return the history entry
     */
    public ConnectionDescriptor createConnectionHistoryEntry() {
        String theDialectName = dialect != null ? dialect.getUniqueName() : null;
        return new ConnectionDescriptor(getProperties().getProperty(PROPERTY_ALIAS), theDialectName, getProperties()
                .getProperty(PROPERTY_URL), getProperties().getProperty(PROPERTY_USER), getProperties().getProperty(
                PROPERTY_DRIVER), getProperties().getProperty(PROPERTY_PASSWORD));
    }

    /**
     * Initialize the model with a defined connection.
     * 
     * @param aConnection
     *                the connection
     */
    public void initializeWith(ConnectionDescriptor aConnection) {
        setDialect(DialectFactory.getInstance().getDialect(aConnection.getDialect()));
        getProperties().setProperty(PROPERTY_ALIAS, aConnection.getAlias());
        getProperties().setProperty(PROPERTY_DRIVER, aConnection.getDriver());
        getProperties().setProperty(PROPERTY_USER, aConnection.getUsername());
        getProperties().setProperty(PROPERTY_PASSWORD, aConnection.getPassword());
        getProperties().setProperty(PROPERTY_URL, aConnection.getUrl());
    }

    /**
     * Gibt den Wert des Attributs <code>comments</code> zurück.
     * 
     * @return Wert des Attributs comments.
     */
    public CommentList getComments() {
        return comments;
    }

    /**
     * Remove a comment from the model.
     * 
     * @param aComment
     *                the comment
     */
    public void removeComment(Comment aComment) {
        comments.remove(aComment);
        subjectAreas.removeComment(aComment);
    }

    /**
     * Add a comment to the model.
     * 
     * @param aComment
     *                the comment
     */
    public void addComment(Comment aComment) {
        aComment.setOwner(this);
        comments.add(aComment);
    }

    /**
     * Gibt den Wert des Attributs <code>domains</code> zurück.
     * 
     * @return Wert des Attributs domains.
     */
    public DomainList getDomains() {
        return domains;
    }

    /**
     * Get the available data types.
     * 
     * The available data types are the dialect datatypes plus the defined
     * domains.
     * 
     * @return the available data types
     */
    public DataTypeList getAvailableDataTypes() {
        DataTypeList theResult = new DataTypeList();
        if (dialect != null) {
            theResult.addAll(dialect.getDataTypes());
        }
        theResult.addAll(domains);
        Collections.sort(theResult);
        return theResult;
    }

    /**
     * Add a domain to the model.
     * 
     * @param aDomain
     *                the domain
     */
    public void addDomain(Domain aDomain) {
        domains.add(aDomain);
    }

    /**
     * Remove a domain from the model.
     * 
     * @param aDomain
     *                a domain
     */
    public void removeDomain(Domain aDomain) {
        domains.remove(aDomain);
    }

    /**
     * Add a view to the model.
     * 
     * @param aView
     *                the view
     * @throws VetoException 
     * @throws ElementAlreadyExistsException 
     * @throws ElementInvalidNameException 
     */
    public void addView(View aView) throws VetoException, ElementInvalidNameException, ElementAlreadyExistsException {
        
        modificationTracker.addView(aView);

        if (dialect != null) {
            ModelUtilities.checkNameAndExistance(tables, aView, dialect);
        }

        aView.setOwner(this);
        views.add(aView);
    }

    /**
     * Remove a view from the model.
     * 
     * @param aView
     *                a view
     * @throws VetoException 
     */
    public void removeView(View aView) throws VetoException {
        
        modificationTracker.removeView(aView);

        views.remove(aView);
        subjectAreas.removeView(aView);
    }
    
    /**
     * Gibt den Wert des Attributs <code>views</code> zurück.
     * 
     * @return Wert des Attributs views.
     */
    public ViewList getViews() {
        return views;
    }

    /**
     * Get the list of supported datatypes for domains.
     * 
     * @return the list of datatypes.
     */
    public DataTypeList getDomainDataTypes() {
        DataTypeList theResult = new DataTypeList();
        theResult.add(DataType.UNDEFINED);
        if (dialect != null) {
            theResult.addAll(dialect.getDataTypes());
        }
        return theResult;
    }

    /**
     * Mark a view as changed. 
     * 
     * @param aView the view
     * @throws VetoException 
     */
    public void changeView(View aView) throws VetoException {
        modificationTracker.changeView(aView);
    }
}