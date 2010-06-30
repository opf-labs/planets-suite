package eu.planets_project.tb.gui.util;

/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/


/**
 * Convenient base class for sortable lists.
 * @author Thomas Spiegl (latest modification by $Author: grantsmith $)
 * @version $Revision: 472610 $ $Date: 2006-11-08 20:46:34 +0100 (Mi, 08 Nov 2006) $
 */
public abstract class SortableList
{
    private String _sort;
    private boolean _ascending;

    protected SortableList(String defaultSortColumn)
    {
        _sort = defaultSortColumn;
        _ascending = isDefaultAscending(defaultSortColumn);
    }

    /**
     * Sort the list.
     */
    protected abstract void sort(String column, boolean ascending);

    /**
     * Is the default sort direction for the given column "ascending" ?
     */
    protected abstract boolean isDefaultAscending(String sortColumn);


    public void sort(String sortColumn)
    {
        if (sortColumn == null)
        {
            throw new IllegalArgumentException("Argument sortColumn must not be null.");
        }

        if (_sort.equals(sortColumn))
        {
            //current sort equals new sortColumn -> reverse sort order
            _ascending = !_ascending;
        }
        else
        {
            //sort new column in default direction
            _sort = sortColumn;
            _ascending = isDefaultAscending(_sort);
        }

        sort(_sort, _ascending);
    }

    public String getSort()
    {
        return _sort;
    }

    public void setSort(String sort)
    {
        _sort = sort;
    }

    public boolean isAscending()
    {
        return _ascending;
    }

    public void setAscending(boolean ascending)
    {
        _ascending = ascending;
    }
}

